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
import android.support.v4.widget.DrawerLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.data.playlist.PlaylistIterator
import hr.caellian.notestream.gui.fragments.FragmentItemPlayable
import java.util.*

class ActivitySearch : NavigationActivity() {
    internal var showingResults = false
    val resultsPlaylist = Playlist.get("searchResults")

    private var fragmentCounter = 0
    private val searchItems = ArrayList<FragmentItemPlayable>()

    override val drawerLayout: DrawerLayout?
        get() = findViewById<View>(R.id.search_layout) as DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_search)

        navigationView = findViewById(R.id.nav_view)
        navigationView!!.setNavigationItemSelectedListener(this)

        findViewById<View>(R.id.buttonClearSearch).setOnClickListener {
            (findViewById<View>(R.id.exitTextSearch) as EditText).setText("")
            resultsPlaylist.clear()
            refreshSearchResults()
        }

        (findViewById<View>(R.id.exitTextSearch) as EditText).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val searched = charSequence.toString()

                resultsPlaylist.clear()
                resultsPlaylist.add(NoteStream.instance?.library?.localMusic?.filtered(searched)
                        ?: Playlist.Empty)

//                object : AsyncTask<Void, Void, Void>() {
//                    override fun doInBackground(params: Array<Void>): Void? {
//                        for (youtubeID in YouTubeFetcher.searchFor(searched)) {
//                            resultsPlaylist.add(PlayableYouTube(youtubeID))
//                            Toast.makeText(this@ActivitySearch, youtubeID, Toast.LENGTH_SHORT).show()
//                        }
//                        resultsPlaylist.add(PlayableYouTube("8xg3vE8Ie_E"))
//                        refreshSearchResults()
//                        return null
//                    }
//                }.execute()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    fun refreshSearchResults() {
        val fm = fragmentManager
        var ft = fm.beginTransaction()
        for (searchItem in searchItems) {
            ft.remove(fm.findFragmentById(searchItem.id))
        }
        ft.commit()

        ft = fm.beginTransaction()
        searchItems.clear()
        for (playable in PlaylistIterator(resultsPlaylist)) {
            val fragment = FragmentItemPlayable.newInstance(playable, resultsPlaylist)
            searchItems.add(fragment)
            ft.add(R.id.foundContent, fragment, "argumentPlayable-" + fragmentCounter++)
        }
        ft.commit()
    }

    override fun onBackPressed() {
        if ((findViewById<View>(R.id.exitTextSearch) as EditText?)?.text.toString() != "") {
            (findViewById<View>(R.id.exitTextSearch) as EditText?)?.setText("")
            resultsPlaylist.clear()
            refreshSearchResults()
        } else {
            super.onBackPressed()
        }
    }
}
