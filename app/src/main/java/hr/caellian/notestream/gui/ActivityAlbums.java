package hr.caellian.notestream.gui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.gui.fragments.FragmentCategoryTile;
import hr.caellian.notestream.util.Genres;
import hr.caellian.notestream.util.Util;

/**
 * Created by tinsv on 10/07/2017.
 */

public class ActivityAlbums extends TableActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((TextView) findViewById(R.id.labelTableTitle)).setText(R.string.label_albums);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        int counter = 0;
        TableRow row = addTableRow();
        for (Map.Entry<String, Playlist> album : NoteStream.getInstance().library.albums.entrySet()) {
            FragmentCategoryTile fragment = FragmentCategoryTile.newInstance(
                    false,
                    R.drawable.ic_album,
                    album.getKey(),
                    album.getValue());
            ft.add(row.getId(), fragment , Playlist.ALBUM_PREFIX + album.getKey());
            counter = ++counter % 2;
            if (counter == 0) row = addTableRow();
        }
        ft.commit();
    }
}
