package hr.caellian.notestream.gui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 25/06/17.
 */

abstract class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected var navigationView: NavigationView? = null

    protected abstract val drawerLayout: DrawerLayout?

    override fun onBackPressed() {
        val drawer = drawerLayout
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        val intent: Intent
        when (id) {
            R.id.nav_library -> {
                intent = Intent(this@NavigationActivity, ActivityLibrary::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_songs -> {
                intent = Intent(this@NavigationActivity, ActivityPlaylist::class.java)
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance?.library?.savedMusic)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_playlists -> {
            }
            R.id.nav_albums -> {
                intent = Intent(this@NavigationActivity, ActivityAlbums::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_artists -> {
                intent = Intent(this@NavigationActivity, ActivityArtists::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_search -> {
                intent = Intent(this@NavigationActivity, ActivitySearch::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_local -> {
                intent = Intent(this@NavigationActivity, ActivityPlaylist::class.java)
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance?.library?.localMusic)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                intent = Intent(this@NavigationActivity, ActivitySettings::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }

        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }
}
