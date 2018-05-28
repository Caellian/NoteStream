/*
 * The MIT License (MIT)
 * NoteStream, android music player and streamer
 * Copyright (c) 2018 Tin Švagelj <tin.svagelj.email@gmail.com> a.k.a. Caellian
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.gui

import android.os.Bundle
import android.widget.TextView
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.gui.fragments.FragmentPlaylistTile
import hr.caellian.notestream.lib.Constants

class ActivityAlbums : TableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        findViewById<TextView>(R.id.labelTableTitle).setText(R.string.label_albums)

        val fm = fragmentManager
        val ft = fm.beginTransaction()

        var counter = 0
        var row = addTableRow()
        for (album in NoteStream.instance.data.albums.entries) {
            val fragment = FragmentPlaylistTile.create(album.value, R.drawable.ic_album, true)
            ft.add(row.id, fragment, Constants.PLAYLIST_ALBUM_PREFIX + album.key)
            counter = ++counter % 2
            if (counter == 0) row = addTableRow()
        }
        ft.commit()
    }
}
