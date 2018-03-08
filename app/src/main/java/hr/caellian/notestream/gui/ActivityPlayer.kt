package hr.caellian.notestream.gui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableDownloadable
import hr.caellian.notestream.data.playable.PlayableYouTube
import hr.caellian.notestream.util.RepeatState
import hr.caellian.notestream.util.Util.timeToString
import java.util.*

class ActivityPlayer : NavigationActivity(), NavigationView.OnNavigationItemSelectedListener, Playable.ControlListener, Playable.ProgressListener {

    internal var psb: PlayerService.PlayerServiceBinder? = null
    internal var suggestionsView: NavigationView? = null

    override val drawerLayout: DrawerLayout?
        get() = findViewById(R.id.player_layout)

    // TODO: Implement album cover swiping for skipping songs.
    // Maybe implement smooth transition?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        psb = NoteStream.instance?.psb

        findViewById<TextView>(R.id.labelSongTitle).isSelected = true
        findViewById<TextView>(R.id.labelTileDescription).isSelected = true

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)
        suggestionsView = findViewById(R.id.suggestions_view)
        suggestionsView?.setNavigationItemSelectedListener(this)

        findViewById<Button>(R.id.buttonDownload).setOnClickListener {
            if (psb?.currentPlayable is PlayableDownloadable?) {
                (psb?.currentPlayable as PlayableDownloadable?)?.download()
            }
        }

        val saveButton = findViewById<Button>(R.id.buttonLibraryAdd)
        saveButton.setOnClickListener {
            if (NoteStream.instance?.library?.isSaved(psb?.currentPlayable) == true) {
                NoteStream.instance?.library?.removePlayable(psb?.currentPlayable)
                saveButton.background = ContextCompat.getDrawable(this, R.drawable.ic_add)
            } else {
                NoteStream.instance?.library?.savePlayable(psb?.currentPlayable)
                saveButton.background = ContextCompat.getDrawable(this, R.drawable.ic_check)
            }
        }

        findViewById<SeekBar>(R.id.songProgressBar).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    psb?.progress = progress
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        findViewById<Button>(R.id.buttonShuffle)?.setOnClickListener { psb?.setShuffle(psb?.doShuffle() == true) }

        val buttonPrevious = findViewById<Button>(R.id.buttonPrevious)
        val buttonNext = findViewById<Button>(R.id.buttonNext)

        buttonPrevious.setOnClickListener { psb!!.switchPrevious() }

        buttonPrevious.setOnLongClickListener {
            val holdTimer = Timer()
            holdTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (buttonPrevious.isPressed) {
                        psb!!.progress = Math.max(psb!!.progress - PlayerService.DEFAULT_PROGRESS_CHANGE, 0)
                    } else {
                        holdTimer.cancel()
                    }
                }
            }, 1000, 500)
            true
        }

        buttonNext.setOnClickListener { psb!!.switchNext() }

        buttonNext.setOnLongClickListener {
            val holdTimer = Timer()
            holdTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (buttonNext.isPressed) {
                        psb?.progress = Math.min(psb?.progress ?: 0 + PlayerService.DEFAULT_PROGRESS_CHANGE,
                                psb?.currentPlayable?.info?.length ?: 0)
                    } else {
                        holdTimer.cancel()
                    }
                }
            }, 1000, 500)
            true
        }

        findViewById<Button>(R.id.buttonTogglePlay).setOnClickListener { psb!!.togglePlay() }

        findViewById<Button>(R.id.buttonRepeat).setOnClickListener { psb!!.toggleRepeat() }

        findViewById<ImageView>(R.id.albumImage).setOnLongClickListener {
            //                findViewById(R.id.lyricsDisplay).setVisibility(findViewById(R.id.lyricsDisplay).getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            findViewById<View>(R.id.lyricsDisplay).visibility = View.VISIBLE
            true
        }

        findViewById<View>(R.id.lyricsContainer).setOnLongClickListener {
            findViewById<View>(R.id.lyricsDisplay).visibility = View.GONE
            true
        }

        NoteStream.registerControlListener(this)
        NoteStream.registerProgressListener(this)
        NoteStream.registerPlayerServiceListener(object : NoteStream.PlayerServiceListener() {
            override fun onPlayerServiceConnected(psb: PlayerService.PlayerServiceBinder) {
                this@ActivityPlayer.psb = psb
                this@ActivityPlayer.onProgressChanged(psb.progress)
                this@ActivityPlayer.onShuffleStateChanged(psb.doShuffle())
                this@ActivityPlayer.onPlayStatusChanged(psb.isPlaying)
                this@ActivityPlayer.onRepeatStateChanged(psb.repeatState)
                this@ActivityPlayer.onPlayableChanged(psb.currentPlayable!!)
            }
        })
    }

    override fun onProgressChanged(progress: Int) {
        val source = findViewById<TextView>(R.id.labelCurrentTime)
        source.text = timeToString(progress)
        findViewById<SeekBar>(R.id.songProgressBar).progress = progress
    }

    override fun onPlayableChanged(current: Playable?) {
        if (current == null) return

        val metadata = current.info
        findViewById<ImageView>(R.id.albumImage).setImageBitmap(metadata.cover)

        val lyrics = metadata.lyrics
        (findViewById<TextView>(R.id.textViewLyrics)).text = lyrics

        findViewById<View>(R.id.lyricsDisplay).visibility = View.GONE
        (findViewById<TextView>(R.id.labelSource)).text = current.location

        if (current is PlayableDownloadable) {
            findViewById<Button>(R.id.buttonDownload).visibility = View.VISIBLE
        } else {
            findViewById<Button>(R.id.buttonDownload).visibility = View.INVISIBLE
        }

        if (current is PlayableYouTube) {
            suggestionsView?.visibility = View.VISIBLE
        } else {
            suggestionsView?.visibility = View.GONE
        }

        val saveButton = findViewById<Button>(R.id.buttonLibraryAdd)
        if (NoteStream.instance?.library?.isSaved(psb?.currentPlayable) == true) {
            saveButton.background = ContextCompat.getDrawable(this, R.drawable.ic_check)
        } else {
            saveButton.background = ContextCompat.getDrawable(this, R.drawable.ic_add)
        }

        (findViewById<TextView>(R.id.labelSongTitle)).text = metadata.title
        (findViewById<TextView>(R.id.labelTileDescription)).text = metadata.author

        findViewById<Button>(R.id.buttonMenu).setOnClickListener(object : View.OnClickListener {
            internal var playablePopupMenu: PlayablePopupMenu? = null

            override fun onClick(v: View) {
                if (playablePopupMenu == null) {
                    psb?.currentPlayable?.also {
                        playablePopupMenu = PlayablePopupMenu(this@ActivityPlayer, findViewById(R.id.buttonMenu), it)
                    }
                }
                playablePopupMenu?.show()
            }
        })

        findViewById<SeekBar>(R.id.songProgressBar).max = metadata.length

        val length = findViewById<TextView>(R.id.labelLength)
        length.text = timeToString(metadata.length)
    }

    override fun onPlayStatusChanged(playing: Boolean) {
        if (!playing) {
            (findViewById<Button>(R.id.buttonTogglePlay)).background = ContextCompat.getDrawable(this, R.drawable.ic_play_circle)
        } else {
            (findViewById<Button>(R.id.buttonTogglePlay)).background = ContextCompat.getDrawable(this, R.drawable.ic_pause_circle)
        }
    }

    override fun onShuffleStateChanged(currentState: Boolean) {
        if (currentState) {
            (findViewById<Button>(R.id.buttonShuffle)).background = ContextCompat.getDrawable(this, R.drawable.ic_shuffle_on)
        } else {
            (findViewById<Button>(R.id.buttonShuffle)).background = ContextCompat.getDrawable(this, R.drawable.ic_shuffle)
        }
    }

    override fun onRepeatStateChanged(currentState: RepeatState) {
        when (currentState) {
            RepeatState.NONE -> (findViewById<Button>(R.id.buttonRepeat)).background = ContextCompat.getDrawable(this, R.drawable.ic_repeat)
            RepeatState.ALL -> (findViewById<Button>(R.id.buttonRepeat)).background = ContextCompat.getDrawable(this, R.drawable.ic_repeat_on)
            RepeatState.ONE -> (findViewById<Button>(R.id.buttonRepeat)).background = ContextCompat.getDrawable(this, R.drawable.ic_repeat_one)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val drawer = findViewById<DrawerLayout>(R.id.player_layout)
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        super.onNavigationItemSelected(item)
        // TODO: Suggestions for online content.

        drawerLayout!!.closeDrawer(GravityCompat.END)
        return false
    }
}
