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
import android.widget.LinearLayout
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playlist.PlaylistIterator
import hr.caellian.notestream.gui.fragments.FragmentItemPlayable
import java.util.*

class ActivitySearch : NavigationActivity() {
    internal var showingResults = false
    val resultList = mutableListOf<Playable>()

    private var fragmentCounter = 0
    private val searchItems = ArrayList<FragmentItemPlayable>()

    override val drawerLayout: DrawerLayout?
        get() = findViewById<View>(R.id.search_layout) as DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_search)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        findViewById<View>(R.id.buttonClearSearch)?.setOnClickListener {
            (findViewById<View>(R.id.textEditSearch) as? EditText)?.setText("")
            resultList.clear()
            refreshSearchResults()
        }

        findViewById<EditText>(R.id.textEditSearch)?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val searched = charSequence.toString()

                resultList.clear()
                resultList.addAll(NoteStream.instance.data.localMusic?.filtered(searched)
                        ?: emptyList())
            }

            override fun afterTextChanged(editable: Editable) {
                refreshSearchResults()
            }
        })
    }

    fun refreshSearchResults() {
        findViewById<LinearLayout>(R.id.foundContent)?.removeAllViewsInLayout()
        searchItems.clear()

        val fm = fragmentManager
        val ft = fm.beginTransaction()
        for (playable in resultList) {
            val fragment = FragmentItemPlayable.newInstance(playable, PlaylistIterator(resultList))
            searchItems.add(fragment)
            ft.add(R.id.foundContent, fragment, "resultFragment-" + fragmentCounter++)
        }
        ft.commit()
    }

    override fun onBackPressed() {
        if ((findViewById<View>(R.id.textEditSearch) as EditText?)?.text.toString() != "") {
            (findViewById<View>(R.id.textEditSearch) as EditText?)?.setText("")
            resultList.clear()
            refreshSearchResults()
        } else {
            super.onBackPressed()
        }
    }
}
