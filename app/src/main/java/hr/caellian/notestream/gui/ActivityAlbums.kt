package hr.caellian.notestream.gui

import android.os.Bundle
import android.view.View
import android.widget.TextView

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.gui.fragments.FragmentCategoryTile

/**
 * Created by tinsv on 10/07/2017.
 */

class ActivityAlbums : TableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        (findViewById<View>(R.id.labelTableTitle) as TextView).setText(R.string.label_albums)

        val fm = fragmentManager
        val ft = fm.beginTransaction()

        var counter = 0
        var row = addTableRow()
        for (album in NoteStream.instance?.library?.albums?.entries ?: emptyList<MutableMap.MutableEntry<String, Playlist>>()) {
            val fragment = FragmentCategoryTile.newInstance(
                    false,
                    R.drawable.ic_album,
                    album.key,
                    album.value)
            ft.add(row.id, fragment, Playlist.ALBUM_PREFIX + album.key)
            counter = ++counter % 2
            if (counter == 0) row = addTableRow()
        }
        ft.commit()
    }
}
