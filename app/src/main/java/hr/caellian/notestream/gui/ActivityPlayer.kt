/*
 * Copyright (C) 2018 Tin Svagelj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
import hr.caellian.notestream.lib.Constants
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
        super.onCreate(null)
        setContentView(R.layout.activity_player)
        psb = NoteStream.instance?.psb

        if (savedInstanceState?.getBoolean(Constants.BUNDLE_LYRICS_VISIBLE) == true) {
            findViewById<View>(R.id.lyricsDisplay).visibility = View.VISIBLE
        }

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
                psb!!.switchNext()
            } else {
                NoteStream.instance?.library?.savePlayable(psb?.currentPlayable)
                saveButton.background = ContextCompat.getDrawable(this, R.drawable.ic_check)
            }
        }

        findViewById<SeekBar>(R.id.songProgressBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    psb?.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        findViewById<Button>(R.id.buttonShuffle)?.setOnClickListener {
            psb!!.setShuffle(psb!!.doShuffle())
        }

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
                        psb?.progress = Math.min(psb?.progress ?: 0
                        +PlayerService.DEFAULT_PROGRESS_CHANGE,
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
            findViewById<View>(R.id.lyricsDisplay).visibility = View.VISIBLE
            true
        }


        setOf(findViewById<View>(R.id.lyricsDisplay),
                findViewById(R.id.lyricsContainer),
                findViewById(R.id.textViewLyrics)).forEach {
            it.setOnLongClickListener {
                findViewById<View>(R.id.lyricsDisplay).visibility = View.GONE
                true
            }
        }

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

    override fun onResume() {
        super.onResume()
        NoteStream.CONTROL_LISTENERS += this
        NoteStream.PROGRESS_LISTENERS += this
    }

    override fun onPause() {
        super.onPause()
        NoteStream.CONTROL_LISTENERS -= this
        NoteStream.PROGRESS_LISTENERS -= this
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
        (findViewById<TextView>(R.id.labelSource)).text = current.playableSource.localizedDisplayName()

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

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.run {
            putBoolean(Constants.BUNDLE_LYRICS_VISIBLE, findViewById<View>(R.id.lyricsDisplay).visibility == View.VISIBLE)
        }

        super.onSaveInstanceState(outState)
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
        (findViewById<Button>(R.id.buttonRepeat)).background = currentState.getDrawable(this)
    }

    override fun onBackPressed() {
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
