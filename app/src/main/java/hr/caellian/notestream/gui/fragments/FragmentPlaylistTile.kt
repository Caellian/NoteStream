package hr.caellian.notestream.gui.fragments

import android.content.Intent
import android.os.Bundle

import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.gui.ActivityPlaylist
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 26/06/17.
 */

class FragmentPlaylistTile : FragmentTile() {
    override fun onClickListener(data: String) {
        val intent = Intent(view.context, ActivityPlaylist::class.java)
        intent.putExtra(Constants.EXTRA_PLAYLIST, data)
        startActivity(intent)
    }

    companion object {
        fun create(playlist: Playlist, iconID: Int, tiledDisplay: Boolean = true): FragmentPlaylistTile {
            val fragment = FragmentPlaylistTile()

            val args = Bundle()
            args.putBoolean(Constants.EXTRA_TILES, tiledDisplay)
            args.putInt(Constants.EXTRA_ICON, iconID)
            args.putString(Constants.EXTRA_LABEL, playlist.label)
            args.putString(Constants.EXTRA_LABEL_DESCRIPTION, playlist.author)
            args.putString(Constants.EXTRA_DATA, playlist.id)
            fragment.arguments = args
            return fragment
        }
    }
}
