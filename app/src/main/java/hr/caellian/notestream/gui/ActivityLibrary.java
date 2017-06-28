package hr.caellian.notestream.gui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import java.util.ArrayList;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.gui.fragments.FragmentPlayableTile;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.data.playable.Playable;

/**
 * Created by caellyan on 18/06/17.
 */

public class ActivityLibrary extends NavigationActivity implements Library.LibraryListener {

    boolean active = true;

    int fragmentCounter = 0;
    ArrayList<FragmentPlayableTile> playlistItems = new ArrayList<>();
    ArrayList<FragmentPlayableTile> lastListenedAdded = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_library);

        NoteStream.getInstance().library = NoteStream.populateLibrary(this);

        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLibrary.this, ActivitySearch.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        findViewById(R.id.labelPlaylists).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open list of playlists in library.
            }
        });

        findViewById(R.id.labelGenres).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLibrary.this, ActivityGenres.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        findViewById(R.id.labelSongs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLibrary.this, ActivityPlaylist.class);
                intent.putExtra("playlist", NoteStream.getInstance().library.savedMusic);
                startActivity(intent);
            }
        });

        findViewById(R.id.labelAlbums).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open list of albums.
            }
        });

        findViewById(R.id.labelArtists).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open list of artists.
            }
        });

        findViewById(R.id.labelHidden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLibrary.this, ActivityPlaylist.class);
                intent.putExtra("playlist", NoteStream.getInstance().library.hiddenMusic);
                startActivity(intent);
            }
        });

        findViewById(R.id.labelClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteStream.getInstance().library.lastListened.clear();
            }
        });

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Playlist lastListened = NoteStream.getInstance().library.lastListened;
//        ((HorizontalScrollView) findViewById(R.id.lastListenedParent)).fullScroll(View.FOCUS_RIGHT);
        for (Playable playable: lastListened) {
            FragmentPlayableTile fragment = FragmentPlayableTile.newInstance(playable, lastListened);
            playlistItems.add(fragment);
            ft.add(R.id.layoutLastListened, fragment , "tile-" + fragmentCounter++ );
        }
        ft.commit();

        NoteStream.getInstance().library.registerLibraryListener(this);
    }

    @Override
    protected DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.library_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;

        if (!lastListenedAdded.isEmpty()) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            for (FragmentPlayableTile fragmentPlayableTile : lastListenedAdded) {
                playlistItems.add(fragmentPlayableTile);
                ft.add(R.id.layoutLastListened, fragmentPlayableTile , "tile-" + fragmentCounter++);
            }
            lastListenedAdded.clear();
            ft.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onPlayableAddedToPlaylist(Playable playable, Playlist playlist) {
        if (playlist == NoteStream.getInstance().library.lastListened) {
            for (FragmentPlayableTile playlistItem : playlistItems) {
                if (playlistItem.playable == playable) return;
            }

            FragmentPlayableTile fragment = FragmentPlayableTile.newInstance(playable,
                    NoteStream.getInstance().library.lastListened);

            if (active) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                playlistItems.add(fragment);
                ft.add(R.id.layoutLastListened, fragment , "tile-" + fragmentCounter++);
                ft.commit();
            } else {
                lastListenedAdded.add(fragment);
            }
        }
    }

    @Override
    public void onPlayableRemovedFromPlaylist(Playable playable, Playlist playlist) {}
}
