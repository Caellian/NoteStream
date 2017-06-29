package hr.caellian.notestream.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.lib.Constants;

/**
 * Created by caellyan on 25/06/17.
 */

public abstract class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected NavigationView navigationView;

    protected abstract DrawerLayout getDrawerLayout();

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = getDrawerLayout();
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Intent intent;
        switch (id) {
            case R.id.nav_library:
                intent = new Intent(NavigationActivity.this, ActivityLibrary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_songs:
                intent = new Intent(NavigationActivity.this, ActivityPlaylist.class);
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.getInstance().library.savedMusic);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_playlists:

                break;
            case R.id.nav_albums:
                intent = new Intent(NavigationActivity.this, ActivityAlbums.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_artists:
                intent = new Intent(NavigationActivity.this, ActivityArtists.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_search:
                intent = new Intent(NavigationActivity.this, ActivitySearch.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_local:
                intent = new Intent(NavigationActivity.this, ActivityPlaylist.class);
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.getInstance().library.localMusic);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(NavigationActivity.this, ActivitySettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
        }

        getDrawerLayout().closeDrawer(GravityCompat.START);
        return true;
    }
}
