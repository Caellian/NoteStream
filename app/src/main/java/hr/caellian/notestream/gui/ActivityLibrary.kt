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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.NoteStreamData
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.data.playlist.PlaylistIterator
import hr.caellian.notestream.gui.fragments.FragmentPlayableTile
import hr.caellian.notestream.lib.Constants
import java.util.*

import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.content_library.*

class ActivityLibrary : NavigationActivity(), NoteStreamData.LibraryListener {

    internal var active = true

    internal var fragmentCounter = 0
    internal var playlistItems = ArrayList<FragmentPlayableTile>()
    internal var lastListenedAdded = ArrayList<FragmentPlayableTile>()

    override val drawerLayout: DrawerLayout?
        get() = findViewById(R.id.library_layout)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_library)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)
        navigationView?.setCheckedItem(R.id.nav_library)

        buttonSearch.setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivitySearch::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        buttonSettings.setOnClickListener {
            val intent = Intent(this, ActivitySettings::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        labelFavorites.setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityPlaylist::class.java)
            intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance.data.favoriteMusic.id)
            startActivity(intent)
        }

        labelPlaylists.setOnClickListener {
            // TODO: Open list of playlists in library.
        }

        labelGenres.setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityGenres::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        labelSongs.setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityPlaylist::class.java)
            intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance.data.savedMusic.id)
            startActivity(intent)
        }

        labelAlbums.setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityAlbums::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        labelArtists.setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityArtists::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        labelHidden.setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityPlaylist::class.java)
            intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance.data.hiddenMusic.id)
            startActivity(intent)
        }

        labelClear.setOnClickListener { NoteStream.instance.data.lastListened.clear() }

        NoteStream.registerLibraryListener(this)
        populateLibrary()
    }

    private fun populateLibrary() {
        val fm = fragmentManager
        val ft = fm.beginTransaction()
        val lastListened = NoteStream.instance.data.lastListened
        for (playable in PlaylistIterator.Time(lastListened)) {
            val fragment = FragmentPlayableTile.create(lastListened, playable)
            playlistItems.add(fragment)
            ft.add(R.id.layoutLastListened, fragment, "tile-" + fragmentCounter++)
        }
        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        active = true

        if (!lastListenedAdded.isEmpty()) {
            val fm = fragmentManager
            val ft = fm.beginTransaction()
            for (fragmentPlayableTile in lastListenedAdded) {
                playlistItems.add(fragmentPlayableTile)
                ft.add(R.id.layoutLastListened, fragmentPlayableTile, "tile-" + fragmentCounter++)
            }
            lastListenedAdded.clear()
            ft.commit()
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
        if (playlist == NoteStream.instance.data.lastListened) {
            playlistItems
                    .map { it.argumentPlayable }
                    .filter { it != null && it == playable }
                    .forEach { return }

            val fragment = FragmentPlayableTile.create(NoteStream.instance.data.lastListened, playable)

            if (active) {
                val fm = fragmentManager
                val ft = fm.beginTransaction()
                playlistItems.add(fragment)
                ft.add(R.id.layoutLastListened, fragment, "tile-" + fragmentCounter++)
                ft.commit()
            } else {
                lastListenedAdded.add(fragment)
            }
        }
    }

    override fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist) {}
}
