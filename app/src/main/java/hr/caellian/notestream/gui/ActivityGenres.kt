package hr.caellian.notestream.gui

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.View
import android.widget.TableRow
import android.widget.TextView

import hr.caellian.notestream.R
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.gui.fragments.FragmentCategoryTile
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.Genre

/**
 * Created by caellyan on 26/06/17.
 */

class ActivityGenres : TableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        (findViewById<View>(R.id.labelTableTitle) as TextView).setText(R.string.label_genres)

        val fm = fragmentManager
        val ft = fm.beginTransaction()

        var counter = 0
        var row = addTableRow()
        for ((id, name, resourceIcon) in Genre.genres) {
            val genrePlaylist = Playlist.get(Constants.PLAYLIST_TEMPORARY_PREFIX + Constants.PLAYLIST_GENRE_PREFIX + id)
            val fragment = FragmentCategoryTile.newInstance(
                    true,
                    resourceIcon,
                    name,
                    genrePlaylist)
            ft.add(row.id, fragment, Constants.PLAYLIST_GENRE_PREFIX + id)
            counter = ++counter % 2
            if (counter == 0) row = addTableRow()
        }
        ft.commit()
    }
}
