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

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.PopupMenu
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.NoteStreamData
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.data.playlist.PlaylistIterator
import hr.caellian.notestream.gui.fragments.FragmentItemPlayable
import hr.caellian.notestream.lib.Constants
import java.util.*

import kotlinx.android.synthetic.main.content_playlist.*

class ActivityPlaylist : NavigationActivity(), NoteStreamData.LibraryListener {

    internal var active = true
    internal var playlist: Playlist? = null
    internal lateinit var iterator: PlaylistIterator
    internal var previousOrder = -1
    internal var psb: PlayerService.PlayerServiceBinder? = null

    internal var fragmentCounter = 0
    internal var playlistItems = ArrayList<FragmentItemPlayable>()
    internal var playlistAdded = ArrayList<FragmentItemPlayable>()

    override val drawerLayout: DrawerLayout?
        get() = findViewById(R.id.playlist_layout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_playlist)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        val playlist = Playlist.get(intent.getStringExtra(Constants.EXTRA_PLAYLIST))
        this.playlist = playlist

        if (playlist.size() == 0) {
            Toast.makeText(this, getString(R.string.empty_playlist), Toast.LENGTH_SHORT).show()
            finish()
        }

        buttonShufflePlay.also {
            it.setOnClickListener {
                if (psb == null) {
                    Toast.makeText(this, getString(R.string.null_player_service), Toast.LENGTH_SHORT).show()
                } else {
                    psb!!.shufflePlay(playlist)

                    val intent = Intent(this, ActivityPlayer::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                }
            }
            it.setOnLongClickListener {
                if (psb == null) {
                    Toast.makeText(this, getString(R.string.null_player_service), Toast.LENGTH_SHORT).show()
                } else {
                    psb!!.playNext(playlist)
                }
                true
            }
        }

        buttonShufflePlay.text = "${playlist.size()}"

        textFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                for (fragment in playlistItems) {
                    fragment.filter(s.toString())
                }
            }
        })

        buttonOrder.setOnClickListener {
            val popup = PopupMenu(this, buttonOrder)
            popup.menuInflater.inflate(R.menu.menu_order, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                setOrder(item.itemId)
            }

            popup.show()
        }

        setIterator(PlaylistIterator.Title(playlist))

        NoteStream.registerPlayerServiceListener(object : NoteStream.PlayerServiceListener() {
            override fun onPlayerServiceConnected(psb: PlayerService.PlayerServiceBinder) {
                this@ActivityPlaylist.psb = psb
            }
        })
    }

    fun setOrder(order: Int): Boolean {
        when (order) {
            R.id.order_title -> if (previousOrder != order) {
                setIterator(PlaylistIterator.Title(playlist ?: Playlist.Empty))
            } else {
                setIterator(PlaylistIterator.Title(playlist ?: Playlist.Empty, false))
            }
            R.id.order_author -> if (previousOrder != order) {
                setIterator(PlaylistIterator.Author(playlist ?: Playlist.Empty))
            } else {
                setIterator(PlaylistIterator.Author(playlist ?: Playlist.Empty, false))
            }
            R.id.order_date -> if (previousOrder != order) {
                setIterator(PlaylistIterator.Time(playlist ?: Playlist.Empty))
            } else {
                setIterator(PlaylistIterator.Time(playlist ?: Playlist.Empty, false))
            }
            else -> setIterator(PlaylistIterator.Title(playlist ?: Playlist.Empty))
        }
        previousOrder = order
        return true
    }

    fun setIterator(iterator: PlaylistIterator) {
        this.iterator = iterator

        findViewById<LinearLayout>(R.id.playlistContent)?.removeAllViewsInLayout()
        playlistItems.clear()

        val fm = fragmentManager
        val ft = fm.beginTransaction()
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
        Log.d("HMH", iterator.toString())
        for (playable in iterator) {
            val fragment = FragmentItemPlayable.newInstance(playable, iterator)
            playlistItems.add(fragment)
            ft.add(R.id.playlistContent, fragment, "plFragment-" + fragmentCounter++)
        }
        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        active = true
        if (!playlistAdded.isEmpty()) {
            val fm = fragmentManager
            val ft = fm.beginTransaction()
            ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
            for (fragmentItemPlayable in playlistAdded) {
                playlistItems.add(fragmentItemPlayable)
                ft.add(R.id.playlistContent, fragmentItemPlayable, "plFragment-" + fragmentCounter++)
            }
            ft.commit()
            playlistAdded.clear()
        }
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    override fun onPlayableAddedToPlaylist(playable: Playable, playlist: Playlist) {
        if (playlist === this.playlist) {
            for (playlistItem in playlistItems) {
                val fragmentPlayable = playlistItem.arguments.getSerializable(Constants.EXTRA_PLAYABLE) as Playable?
                if (fragmentPlayable != null && fragmentPlayable == playable) return
            }

            val fragment = FragmentItemPlayable.newInstance(playable, iterator)
            if (active) {
                val fm = fragmentManager
                val ft = fm.beginTransaction()
                playlistItems.add(fragment)
                ft.add(R.id.playlistContent, fragment, "plFragment-" + fragmentCounter++)
                ft.commit()
            } else {
                playlistAdded.add(fragment)
            }

        }
    }

    override fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist) {

    }
}
