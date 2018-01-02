package hr.caellian.notestream.gui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.widget.TableRow;
import android.widget.TextView;

import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.gui.fragments.FragmentCategoryTile;
import hr.caellian.notestream.util.Genre;

/**
 * Created by caellyan on 26/06/17.
 */

public class ActivityGenres extends TableActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((TextView) findViewById(R.id.labelTableTitle)).setText(R.string.label_genres);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        int counter = 0;
        TableRow row = addTableRow();
        for (Genre genre : Genre.Companion.getGenres()) {
            Playlist genrePlaylist = Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.GENRE_PREFIX + genre.getId());
            FragmentCategoryTile fragment = FragmentCategoryTile.newInstance(
                    true,
                    genre.getResourceIcon(),
                    genre.getName(),
                    genrePlaylist);
            ft.add(row.getId(), fragment , Playlist.GENRE_PREFIX + genre.getId() );
            counter = ++counter % 2;
            if (counter == 0) row = addTableRow();
        }
        ft.commit();
    }
}
