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
import android.os.Handler
import android.support.v4.widget.DrawerLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableYouTube
import hr.caellian.notestream.data.playlist.PlaylistIterator
import hr.caellian.notestream.data.playlist.PlaylistYouTube
import hr.caellian.notestream.data.youtube.YouTubeFetcher
import hr.caellian.notestream.gui.fragments.FragmentItemPlayable
import hr.caellian.notestream.gui.fragments.FragmentPlaylistTile
import hr.caellian.notestream.gui.fragments.FragmentTileYouTube
import java.util.*

class ActivitySearch : NavigationActivity() {
    internal var showingResults = false
    val youtubePlaylists = mutableListOf<PlaylistYouTube>()
    val youtubeResults = mutableListOf<PlayableYouTube>()
    val localResults = mutableListOf<Playable>()

    private var fragmentCounter = 0

    private val searchHandler = Handler()

    override val drawerLayout: DrawerLayout?
        get() = findViewById<View>(R.id.search_layout) as DrawerLayout


    private val youtubeSearch = Runnable {
        val query = findViewById<EditText>(R.id.textEditSearch)?.text?.toString()!!
        youtubePlaylists.clear()
        youtubeResults.clear()

        youtubePlaylists += YouTubeFetcher.searchPlaylists(query)
        youtubeResults += YouTubeFetcher.searchVideos(query)

        refreshSearchResults()
        findViewById<EditText>(R.id.youtubePlaylistArea)?.visibility = View.VISIBLE
        findViewById<EditText>(R.id.youtubePlayableArea)?.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_search)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        findViewById<View>(R.id.buttonClearSearch)?.setOnClickListener {
            (findViewById<View>(R.id.textEditSearch) as? EditText)?.setText("")
            localResults.clear()
            findViewById<EditText>(R.id.youtubePlaylistArea)?.visibility = View.GONE
            findViewById<EditText>(R.id.youtubePlayableArea)?.visibility = View.GONE
            refreshSearchResults()
        }

        findViewById<EditText>(R.id.textEditSearch)?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val searched = charSequence.toString()
                findViewById<EditText>(R.id.youtubePlaylistArea)?.visibility = View.GONE
                findViewById<EditText>(R.id.youtubePlayableArea)?.visibility = View.GONE

                localResults.clear()
                localResults += NoteStream.instance.data.localMusic.filtered(searched)
                searchHandler.removeCallbacks(youtubeSearch)
                searchHandler.postDelayed(youtubeSearch,2000)
            }

            override fun afterTextChanged(editable: Editable) {
                refreshSearchResults()
            }
        })
    }

    fun refreshSearchResults() {
        findViewById<LinearLayout>(R.id.localPlayable)?.removeAllViewsInLayout()
        findViewById<LinearLayout>(R.id.youtubePlayable)?.removeAllViewsInLayout()
        findViewById<LinearLayout>(R.id.localPlayable)?.removeAllViewsInLayout()

        val fm = fragmentManager
        val ft = fm.beginTransaction()

        for (playable in localResults) {
            val fragment = FragmentItemPlayable.newInstance(playable, PlaylistIterator(localResults))
            ft.add(R.id.localPlayable, fragment, "resultFragment-" + fragmentCounter++)
        }

        for (playable in youtubeResults) {
            val fragment = FragmentItemPlayable.newInstance(playable, PlaylistIterator(youtubeResults))
            ft.add(R.id.youtubePlayable, fragment, "resultFragment-" + fragmentCounter++)
        }

        for (playlist in youtubePlaylists) {
            val fragment = FragmentTileYouTube.create(playlist)
            ft.add(R.id.localPlayable, fragment, "resultFragment-" + fragmentCounter++)
        }
        ft.commit()
    }

    override fun onBackPressed() {
        if ((findViewById<View>(R.id.textEditSearch) as EditText?)?.text.toString() != "") {
            (findViewById<View>(R.id.textEditSearch) as EditText?)?.setText("")
            localResults.clear()
            refreshSearchResults()
        } else {
            super.onBackPressed()
        }
    }
}
