package hr.caellian.notestream.gui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.gui.fragments.FragmentCategoryTile;
import hr.caellian.notestream.util.Util;

/**
 * Created by tinsv on 22/07/2017.
 */

public class ActivityArtists extends TableActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((TextView) findViewById(R.id.labelTableTitle)).setText(R.string.label_artists);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        int counter = 0;
        TableRow row = addTableRow();
        for (Map.Entry<String, Playlist> artist : NoteStream.getInstance().library.artists.entrySet()) {
            FragmentCategoryTile fragment = FragmentCategoryTile.newInstance(
                    true,
                    R.drawable.ic_artist,
                    artist.getKey(),
                    artist.getValue());
            ft.add(row.getId(), fragment , Playlist.ARTIST_PREFIX + artist.getKey());
            counter = ++counter % 2;
            if (counter == 0) row = addTableRow();
        }
        ft.commit();
    }
}
