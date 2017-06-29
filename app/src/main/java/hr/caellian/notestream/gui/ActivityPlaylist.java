package hr.caellian.notestream.gui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Library;
import hr.caellian.notestream.gui.fragments.FragmentItemPlayable;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.PlayerService;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.lib.Constants;

/**
 * Created by caellyan on 16/06/17.
 */

public class ActivityPlaylist extends NavigationActivity implements Library.LibraryListener {

    boolean active = true;
    Playlist playlist;
    int previousOrder = -1;
    PlayerService.PlayerServiceBinder psb = null;

    int fragmentCounter = 0;
    ArrayList<FragmentItemPlayable> playlistItems = new ArrayList<>();
    ArrayList<FragmentItemPlayable> playlistAdded = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_playlist);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Playlist playlist = Playlist.get(getIntent().getStringExtra(Constants.EXTRA_PLAYLIST));
        this.playlist = playlist;

        if (playlist == null || playlist.size() == 0) {
            Toast.makeText(ActivityPlaylist.this, getString(R.string.invalid_playlist), Toast.LENGTH_SHORT).show();
            finish();
        }

        findViewById(R.id.buttonShufflePlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (psb == null) {
                    Toast.makeText(ActivityPlaylist.this, getString(R.string.null_player_service), Toast.LENGTH_SHORT).show();
                } else if (playlist != null && !playlist.isEmpty()) {
                    psb.shufflePlay(playlist);

                    Intent intent = new Intent(ActivityPlaylist.this, ActivityPlayer.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivityPlaylist.this, getString(R.string.shuffle_empty_playlist), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((EditText) findViewById(R.id.textFilter)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (FragmentItemPlayable fragment : playlistItems) {
                    fragment.filter(s.toString());
                }
            }
        });

        final Button orderButton = (Button) findViewById(R.id.buttonOrder);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ActivityPlaylist.this, orderButton);
                popup.getMenuInflater().inflate(R.menu.menu_order, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        setOrder(item.getItemId());

                        Intent intent = new Intent(ActivityPlaylist.this, ActivityPlaylist.class);
                        intent.putExtra(Constants.EXTRA_PLAYLIST, playlist);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                });

                popup.show();
            }
        });

        setPlaylist(playlist);

        NoteStream.registerPlayerServiceListener(new NoteStream.PlayerServiceListener() {
            @Override
            public void onPlayerServiceConnected(PlayerService.PlayerServiceBinder psb) {
                ActivityPlaylist.this.psb = NoteStream.getInstance().getPlayerServiceBinder();
            }
        });
    }

    public boolean setOrder(final int order) {
        switch (order) {
            case R.id.order_title:
                if (previousOrder != order) {
                    setPlaylist(playlist.sort(Playlist.SORT_BY_TITLE, true));
                } else {
                    setPlaylist(playlist.sort(Playlist.SORT_BY_TITLE, false));
                }
                break;
            case R.id.order_author:
                if (previousOrder != order) {
                    setPlaylist(playlist.sort(Playlist.SORT_BY_AUTHOR, true));
                } else {
                    setPlaylist(playlist.sort(Playlist.SORT_BY_AUTHOR, false));
                }
                break;
            case R.id.order_date:
                if (previousOrder != order) {
                    setPlaylist(playlist.sort(Playlist.SORT_BY_DATE, true));
                } else {
                    setPlaylist(playlist.sort(Playlist.SORT_BY_DATE, false));
                }
                break;
            default:
                setPlaylist(playlist.sort(Playlist.SORT_BY_TITLE, true));
        }
        previousOrder = order;
        return true;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (Fragment playlistItem : playlistItems) {
            ft.remove(fm.findFragmentById(playlistItem.getId()));
        }
        ft.commit();

        ft = fm.beginTransaction();
        playlistItems.clear();
        for (Playable playable: playlist) {
            FragmentItemPlayable fragment = FragmentItemPlayable.newInstance(playable, playlist);
            playlistItems.add(fragment);
            ft.add(R.id.playlistContent, fragment , "playable-" + fragmentCounter++ );
        }
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
        if (!playlistAdded.isEmpty()) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            for (FragmentItemPlayable fragmentItemPlayable : playlistAdded) {
                playlistItems.add(fragmentItemPlayable);
                ft.add(R.id.playlistContent, fragmentItemPlayable , "playable-" + fragmentCounter++);
            }
            playlistAdded.clear();
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
        if (playlist == this.playlist) {
            for (FragmentItemPlayable playlistItem : playlistItems) {
                Playable fragmentPlayable = (Playable) playlistItem.getArguments().getSerializable(Constants.EXTRA_PLAYABLE);
                if (fragmentPlayable != null && fragmentPlayable == playable) return;
            }

            FragmentItemPlayable fragment = FragmentItemPlayable.newInstance(playable, playlist);
            if (active) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                playlistItems.add(fragment);
                ft.add(R.id.playlistContent, fragment , "playable-" + fragmentCounter++ );
                ft.commit();
            } else {
                playlistAdded.add(fragment);
            }

        }
    }

    @Override
    public void onPlayableRemovedFromPlaylist(Playable playable, Playlist playlist) {

    }

    @Override
    protected DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.playlist_layout);
    }
}
