package hr.caellian.notestream.gui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Playlist;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.playable.PlayableYouTube;
import hr.caellian.notestream.data.youtube.YouTubeFetcher;
import hr.caellian.notestream.gui.fragments.FragmentItemPlayable;

/**
 * Created by caellyan on 25/06/17.
 */

public class ActivitySearch extends NavigationActivity {

    boolean showingResults = false;
    public final Playlist resultsPlaylist = Playlist.get("searchResults");

    private int fragmentCounter = 0;
    protected final ArrayList<FragmentItemPlayable> searchItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_search);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.buttonClearSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) findViewById(R.id.exitTextSearch)).setText("");
                resultsPlaylist.clear();
                refreshSearchResults();
            }
        });

        ((EditText) findViewById(R.id.exitTextSearch)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String searched = charSequence.toString();

                resultsPlaylist.clear();
                resultsPlaylist.add(NoteStream.getInstance().library.localMusic.filtered(searched));

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void[] params) {
                        for (String youtubeID : YouTubeFetcher.searchFor(searched)) {
                            resultsPlaylist.add(new PlayableYouTube(youtubeID));
                            Toast.makeText(ActivitySearch.this, youtubeID, Toast.LENGTH_SHORT).show();
                        }
                        resultsPlaylist.add(new PlayableYouTube("8xg3vE8Ie_E"));
                        refreshSearchResults();
                        return null;
                    }
                }.execute();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public void refreshSearchResults() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (Fragment searchItem : searchItems) {
            ft.remove(fm.findFragmentById(searchItem.getId()));
        }
        ft.commit();

        ft = fm.beginTransaction();
        searchItems.clear();
        for (Playable playable: resultsPlaylist) {
            FragmentItemPlayable fragment = FragmentItemPlayable.newInstance(playable, resultsPlaylist);
            searchItems.add(fragment);
            ft.add(R.id.foundContent, fragment , "playable-" + fragmentCounter++ );
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (!Objects.equals(((EditText) findViewById(R.id.exitTextSearch)).getText().toString(), "")) {
            ((EditText) findViewById(R.id.exitTextSearch)).setText("");
            resultsPlaylist.clear();
            refreshSearchResults();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.search_layout);
    }
}
