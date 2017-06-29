package hr.caellian.notestream.gui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import java.util.ArrayList;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.gui.dialog.DialogCancelOk;
import hr.caellian.notestream.gui.fragments.FragmentPlayableTile;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.lib.Constants;

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
        super.onCreate(null);
        setContentView(R.layout.activity_library);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_library);

        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLibrary.this, ActivitySearch.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        findViewById(R.id.labelFavorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLibrary.this, ActivityPlaylist.class);
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.getInstance().library.favoriteMusic.getID());
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
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.getInstance().library.savedMusic.getID());
                startActivity(intent);
            }
        });

        findViewById(R.id.labelAlbums).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLibrary.this, ActivityAlbums.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        findViewById(R.id.labelArtists).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLibrary.this, ActivityArtists.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        findViewById(R.id.labelHidden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLibrary.this, ActivityPlaylist.class);
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.getInstance().library.hiddenMusic.getID());
                startActivity(intent);
            }
        });

        findViewById(R.id.labelClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteStream.getInstance().library.lastListened.clear();
            }
        });

        NoteStream.registerLibraryListener(this);


        int storageCheck = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int phoneStateCheck = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (storageCheck != PackageManager.PERMISSION_GRANTED || phoneStateCheck != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {

                showRequiredPermissionsDialogue();
            } else {
                ArrayList<String> permissionsLeft = new ArrayList<>(2);
                if (storageCheck != PackageManager.PERMISSION_GRANTED) {
                    permissionsLeft.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if (phoneStateCheck != PackageManager.PERMISSION_GRANTED) {
                    permissionsLeft.add(Manifest.permission.READ_PHONE_STATE);
                }

                requestPermissions(permissionsLeft.toArray(new String[0]), Constants.APP_REQUEST_CODE);
            }
        } else {
            populateLibrary();
        }
    }

    protected void showRequiredPermissionsDialogue() {
        DialogCancelOk dialog = new DialogCancelOk(ActivityLibrary.this,
                getString(R.string.title_permissions_mandatory),
                getString(R.string.mandatory_permissions_explanation),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityLibrary.this.finish();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(myAppSettings, Constants.APP_REQUEST_CODE);
                        ActivityLibrary.this.finish();
                    }
                });

        dialog.setCancelable(false);
        dialog.show();
    }

    protected void populateLibrary() {
        NoteStream.getInstance().populateLibrary(this);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Playlist lastListened = NoteStream.getInstance().library.lastListened;
        for (Playable playable: lastListened) {
            FragmentPlayableTile fragment = FragmentPlayableTile.newInstance(playable, lastListened);
            playlistItems.add(fragment);
            ft.add(R.id.layoutLastListened, fragment , "tile-" + fragmentCounter++ );
        }
        ft.commit();
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
                Playable fragmentPlayable = playlistItem.getPlayable();
                if (fragmentPlayable != null && fragmentPlayable == playable) return;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean finished = true;
        for (int n = 0; n < permissions.length; n++) {
            if (grantResults[n] != PackageManager.PERMISSION_GRANTED) {
                finished = false;

            }
        }

        if (finished) {
            populateLibrary();
        } else {
            showRequiredPermissionsDialogue();
        }
    }
}
