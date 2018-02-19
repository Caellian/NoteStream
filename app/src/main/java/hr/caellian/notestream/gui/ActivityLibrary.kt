package hr.caellian.notestream.gui

import android.Manifest
import android.app.AlertDialog
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.View
import android.widget.Button
import android.widget.TextView

import java.util.ArrayList

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.Library
import hr.caellian.notestream.gui.dialog.DialogCancelOk
import hr.caellian.notestream.gui.fragments.FragmentPlayableTile
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 18/06/17.
 */

class ActivityLibrary : NavigationActivity(), Library.LibraryListener {

    internal var active = true

    internal var fragmentCounter = 0
    internal var playlistItems = ArrayList<FragmentPlayableTile>()
    internal var lastListenedAdded = ArrayList<FragmentPlayableTile>()

    override val drawerLayout: DrawerLayout?
        get() = findViewById(R.id.library_layout)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_library)

        navigationView = findViewById(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)
        navigationView?.setCheckedItem(R.id.nav_library)

        findViewById<Button>(R.id.buttonSearch).setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivitySearch::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.labelFavorites).setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityPlaylist::class.java)
            intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance?.library?.favoriteMusic?.id)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.labelPlaylists).setOnClickListener {
            // TODO: Open list of playlists in library.
        }

        findViewById<TextView>(R.id.labelGenres).setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityGenres::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.labelSongs).setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityPlaylist::class.java)
            intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance?.library?.savedMusic?.id)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.labelAlbums).setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityAlbums::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.labelArtists).setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityArtists::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.labelHidden).setOnClickListener {
            val intent = Intent(this@ActivityLibrary, ActivityPlaylist::class.java)
            intent.putExtra(Constants.EXTRA_PLAYLIST, NoteStream.instance?.library?.hiddenMusic?.id)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.labelClear).setOnClickListener { NoteStream.instance?.library?.lastListened?.clear() }

        NoteStream.registerLibraryListener(this)


        val storageCheck = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        val phoneStateCheck = checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
        if (storageCheck != PackageManager.PERMISSION_GRANTED || phoneStateCheck != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {

                showRequiredPermissionsDialogue()
            } else {
                val permissionsLeft = ArrayList<String>(2)
                if (storageCheck != PackageManager.PERMISSION_GRANTED) {
                    permissionsLeft.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                if (phoneStateCheck != PackageManager.PERMISSION_GRANTED) {
                    permissionsLeft.add(Manifest.permission.READ_PHONE_STATE)
                }

                requestPermissions(permissionsLeft.toTypedArray(), Constants.APP_REQUEST_CODE)
            }
        } else {
            populateLibrary()
        }
    }

    protected fun showRequiredPermissionsDialogue() {
        val dialog = DialogCancelOk(this@ActivityLibrary,
                getString(R.string.title_permissions_mandatory),
                getString(R.string.mandatory_permissions_explanation),
                View.OnClickListener { this@ActivityLibrary.finish() },
                View.OnClickListener {
                    val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName))
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                    myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivityForResult(myAppSettings, Constants.APP_REQUEST_CODE)
                    this@ActivityLibrary.finish()
                })

        dialog.setCancelable(false)
        dialog.show()
    }

    protected fun populateLibrary() {
        NoteStream.instance?.populateLibrary(this)

        val fm = fragmentManager
        val ft = fm.beginTransaction()
        val lastListened = NoteStream.instance?.library?.lastListened
        for (playable in lastListened ?: emptyList<Playable>()) {
            val fragment = FragmentPlayableTile.newInstance(playable, lastListened!!)
            playlistItems.add(fragment)
            ft.add(R.id.layoutLastListened, fragment, "tile-" + fragmentCounter++)
        }
        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        active = true

        if (!lastListenedAdded.isEmpty()) {
            val fm = fragmentManager
            val ft = fm.beginTransaction()
            for (fragmentPlayableTile in lastListenedAdded) {
                playlistItems.add(fragmentPlayableTile)
                ft.add(R.id.layoutLastListened, fragmentPlayableTile, "tile-" + fragmentCounter++)
            }
            lastListenedAdded.clear()
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
        if (playlist == NoteStream.instance?.library?.lastListened) {
            playlistItems
                    .map { it.playable }
                    .filter { it != null && it == playable }
                    .forEach { return }

            val fragment = FragmentPlayableTile.newInstance(playable,
                    NoteStream.instance?.library?.lastListened!!)

            if (active) {
                val fm = fragmentManager
                val ft = fm.beginTransaction()
                playlistItems.add(fragment)
                ft.add(R.id.layoutLastListened, fragment, "tile-" + fragmentCounter++)
                ft.commit()
            } else {
                lastListenedAdded.add(fragment)
            }
        }
    }

    override fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist) {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var finished = true
        for (n in permissions.indices) {
            if (grantResults[n] != PackageManager.PERMISSION_GRANTED) {
                finished = false

            }
        }

        if (finished) {
            populateLibrary()
        } else {
            showRequiredPermissionsDialogue()
        }
    }
}
