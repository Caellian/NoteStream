/*
 * The MIT License (MIT)
 * NoteStream, android music player and streamer
 * Copyright (c) 2018 Tin Švagelj <tin.svagelj.email@gmail.com> a.k.a. Caellian
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.gui

import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.lib.Constants

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
                intent = Intent(this, ActivityLibrary::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_songs -> {
                intent = Intent(this, ActivityPlaylist::class.java)
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance.data.savedMusic.id)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_playlists -> {
            }
            R.id.nav_albums -> {
                intent = Intent(this, ActivityAlbums::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_artists -> {
                intent = Intent(this, ActivityArtists::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_search -> {
                intent = Intent(this, ActivitySearch::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_local -> {
                intent = Intent(this, ActivityPlaylist::class.java)
                intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance.data.localMusic.id)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                intent = Intent(this, ActivitySettings::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }

        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }
}
