package hr.caellian.notestream.gui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.widget.TableRow;

import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.gui.fragments.FragmentCategoryTile;
import hr.caellian.notestream.util.Genres;
import hr.caellian.notestream.util.Util;

/**
 * Created by caellyan on 26/06/17.
 */

public class ActivityGenres extends TableActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        int counter = 0;
        TableRow row = addTableRow();
        for (Genres genre : Genres.values()) {
            FragmentCategoryTile fragment = FragmentCategoryTile.newInstance(
                    null,
                    Util.drawableToBitmap(ContextCompat.getDrawable(ActivityGenres.this, genre.resourceIcon)),
                    genre.name,
                    new Playlist("genre-" + genre.id));
            ft.add(row.getId(), fragment , "genre-" + genre.id );
            counter = ++counter % 2;
            if (counter == 0) row = addTableRow();
        }
        ft.commit();
    }
}
