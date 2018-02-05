package hr.caellian.notestream.gui

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

import java.util.ArrayList

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.gui.fragments.FragmentItemPlayable

/**
 * Created by caellyan on 25/06/17.
 */

class ActivitySearch : NavigationActivity() {
    internal var showingResults = false
    val resultsPlaylist = Playlist.get("searchResults")

    private var fragmentCounter = 0
    protected val searchItems = ArrayList<FragmentItemPlayable>()

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
                resultsPlaylist.add(NoteStream.instance?.library?.localMusic?.filtered(searched))

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
        for (playable in resultsPlaylist) {
            val fragment = FragmentItemPlayable.newInstance(playable, resultsPlaylist)
            searchItems.add(fragment)
            ft.add(R.id.foundContent, fragment, "playable-" + fragmentCounter++)
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
