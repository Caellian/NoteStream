package hr.caellian.notestream.gui

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.PopupMenu
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.Library
import hr.caellian.notestream.gui.fragments.FragmentItemPlayable
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 16/06/17.
 */

class ActivityPlaylist : NavigationActivity(), Library.LibraryListener {

    internal var active = true
    internal var playlist: Playlist? = null
    internal var previousOrder = -1
    internal var psb: PlayerService.PlayerServiceBinder? = null

    internal var fragmentCounter = 0
    internal var playlistItems = ArrayList<FragmentItemPlayable>()
    internal var playlistAdded = ArrayList<FragmentItemPlayable>()

    override val drawerLayout: DrawerLayout?
        get() = findViewById<View>(R.id.playlist_layout) as DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_playlist)

        navigationView = findViewById(R.id.nav_view)
        navigationView!!.setNavigationItemSelectedListener(this)

        val playlist = Playlist.get(intent.getStringExtra(Constants.EXTRA_PLAYLIST))
        this.playlist = playlist

        if (playlist == null || playlist.size() == 0) {
            Toast.makeText(this@ActivityPlaylist, getString(R.string.invalid_playlist), Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<View>(R.id.buttonShufflePlay).setOnClickListener {
            if (psb == null) {
                Toast.makeText(this@ActivityPlaylist, getString(R.string.null_player_service), Toast.LENGTH_SHORT).show()
            } else if (playlist != null && !playlist.isEmpty) {
                psb!!.shufflePlay(playlist)

                val intent = Intent(this@ActivityPlaylist, ActivityPlayer::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            } else {
                Toast.makeText(this@ActivityPlaylist, getString(R.string.shuffle_empty_playlist), Toast.LENGTH_SHORT).show()
            }
        }

        (findViewById<View>(R.id.textFilter) as EditText).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                for (fragment in playlistItems) {
                    fragment.filter(s.toString())
                }
            }
        })

        val orderButton = findViewById<Button>(R.id.buttonOrder)
        orderButton.setOnClickListener {
            val popup = PopupMenu(this@ActivityPlaylist, orderButton)
            popup.menuInflater.inflate(R.menu.menu_order, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                setOrder(item.itemId)

                val intent = Intent(this@ActivityPlaylist, ActivityPlaylist::class.java)
                intent.putExtra(Constants.EXTRA_PLAYLIST, playlist)
                startActivity(intent)
                finish()
                true
            }

            popup.show()
        }

        setPlaylist(playlist)

        NoteStream.registerPlayerServiceListener(object : NoteStream.PlayerServiceListener() {
            override fun onPlayerServiceConnected(psb: PlayerService.PlayerServiceBinder?) {
                this@ActivityPlaylist.psb = NoteStream.instance?.psb
            }
        })
    }

    fun setOrder(order: Int): Boolean {
        when (order) {
            R.id.order_title -> if (previousOrder != order) {
                setPlaylist(playlist!!.sort(Playlist.SORT_BY_TITLE, true))
            } else {
                setPlaylist(playlist!!.sort(Playlist.SORT_BY_TITLE, false))
            }
            R.id.order_author -> if (previousOrder != order) {
                setPlaylist(playlist!!.sort(Playlist.SORT_BY_AUTHOR, true))
            } else {
                setPlaylist(playlist!!.sort(Playlist.SORT_BY_AUTHOR, false))
            }
            R.id.order_date -> if (previousOrder != order) {
                setPlaylist(playlist!!.sort(Playlist.SORT_BY_DATE, true))
            } else {
                setPlaylist(playlist!!.sort(Playlist.SORT_BY_DATE, false))
            }
            else -> setPlaylist(playlist!!.sort(Playlist.SORT_BY_TITLE, true))
        }
        previousOrder = order
        return true
    }

    fun setPlaylist(playlist: Playlist?) {
        this.playlist = playlist

        val fm = fragmentManager
        var ft = fm.beginTransaction()
        for (playlistItem in playlistItems) {
            ft.remove(fm.findFragmentById(playlistItem.id))
        }
        ft.commit()

        ft = fm.beginTransaction()
        playlistItems.clear()
        for (playable in playlist!!) {
            val fragment = FragmentItemPlayable.newInstance(playable, playlist)
            playlistItems.add(fragment)
            ft.add(R.id.playlistContent, fragment, "playable-" + fragmentCounter++)
        }
        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        active = true
        if (!playlistAdded.isEmpty()) {
            val fm = fragmentManager
            val ft = fm.beginTransaction()
            for (fragmentItemPlayable in playlistAdded) {
                playlistItems.add(fragmentItemPlayable)
                ft.add(R.id.playlistContent, fragmentItemPlayable, "playable-" + fragmentCounter++)
            }
            playlistAdded.clear()
            ft.commit()
        }
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    override fun onPlayableAddedToPlaylist(playable: Playable, playlist: Playlist) {
        if (playlist === this.playlist) {
            for (playlistItem in playlistItems) {
                val fragmentPlayable = playlistItem.arguments.getSerializable(Constants.EXTRA_PLAYABLE) as Playable?
                if (fragmentPlayable != null && fragmentPlayable == playable) return
            }

            val fragment = FragmentItemPlayable.newInstance(playable, playlist)
            if (active) {
                val fm = fragmentManager
                val ft = fm.beginTransaction()
                playlistItems.add(fragment)
                ft.add(R.id.playlistContent, fragment, "playable-" + fragmentCounter++)
                ft.commit()
            } else {
                playlistAdded.add(fragment)
            }

        }
    }

    override fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist) {

    }
}
