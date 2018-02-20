package hr.caellian.notestream.gui

import android.os.Bundle
import android.widget.TextView
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.gui.fragments.FragmentCategoryTile
import hr.caellian.notestream.lib.Constants

/**
 * Created by tinsv on 22/07/2017.
 */

class ActivityArtists : TableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        findViewById<TextView>(R.id.labelTableTitle).setText(R.string.label_artists)

        val fm = fragmentManager
        val ft = fm.beginTransaction()

        var counter = 0
        var row = addTableRow()
        for (artist in NoteStream.instance?.library?.artists?.entries ?: emptyList<MutableMap.MutableEntry<String, Playlist>>()) {
            val fragment = FragmentCategoryTile.newInstance(
                    true,
                    R.drawable.ic_artist,
                    artist.key,
                    artist.value)
            ft.add(row.id, fragment, Constants.PLAYLIST_ARTIST_PREFIX + artist.key)
            counter = ++counter % 2
            if (counter == 0) row = addTableRow()
        }
        ft.commit()
    }
}
