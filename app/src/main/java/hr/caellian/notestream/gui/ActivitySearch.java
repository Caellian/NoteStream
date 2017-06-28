package hr.caellian.notestream.gui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.Playlist;

/**
 * Created by caellyan on 25/06/17.
 */

public class ActivitySearch extends NavigationActivity {

    boolean showingResults = false;
    public final Playlist resultsLocal = new Playlist("searchResults");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((EditText) findViewById(R.id.exitTextSearch)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searched = editable.toString();

                resultsLocal.clear();
                resultsLocal.add(NoteStream.getInstance().library.localMusic.filtered(searched));
            }
        });
    }

    public void clearSearchResults() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (showingResults) {
            clearSearchResults();
        }
    }

    @Override
    protected DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.search_layout);
    }
}
