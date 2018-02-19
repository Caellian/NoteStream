package hr.caellian.notestream.gui.fragments

import android.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView

import hr.caellian.notestream.R
import hr.caellian.notestream.data.Library
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.RepeatState

/**
 * Created by tinsv on 30/06/2017.
 */

abstract class FragmentPlayableMediator : Fragment(), Playable.ControlListener, Library.LibraryListener, PlayableRemote.AvailabilityListener {
    protected var defaultTextColor: Int = 0

    val playlist: Playlist
        get() = Playlist.get(arguments.getString(Constants.EXTRA_PLAYLIST))

    val playable: Playable?
        get() {
            val playlist = Playlist.get(arguments.getString(Constants.EXTRA_PLAYLIST))
            return playlist.getPlayable(arguments.getString(Constants.EXTRA_PLAYABLE))
        }

    protected abstract fun updateView()

    override fun onPlayableChanged(current: Playable?) {
        val playable = playable
        if (current == playable) {
            view?.findViewById<TextView>(R.id.labelSongTitle)?.setTextColor(ContextCompat.getColor(view!!.context, R.color.colorAccent))
        } else {
            view?.findViewById<TextView>(R.id.labelSongTitle)?.setTextColor(defaultTextColor)
        }
    }

    override fun onPlayStatusChanged(playing: Boolean) {

    }

    override fun onShuffleStateChanged(currentState: Boolean) {

    }

    override fun onRepeatStateChanged(currentState: RepeatState) {

    }

    override fun onPlayableAddedToPlaylist(argPlayable: Playable, argPlaylist: Playlist) {
        val playlist = playlist
        val playable = playable

        if (argPlayable == playable && argPlaylist == playlist) {
            view?.visibility = View.VISIBLE
        }
    }

    override fun onPlayableRemovedFromPlaylist(argPlayable: Playable, argPlaylist: Playlist) {
        val playlist = playlist
        val playable = playable

        if (argPlayable == playable && argPlaylist == playlist) {
            view?.visibility = View.GONE
        }
    }

    override fun onAvailableStateChanged(argPlayable: PlayableRemote, state: Boolean) {
        val playable = playable
        if (argPlayable == playable) {
            if (state) {
                view?.visibility = View.VISIBLE
                updateView()
            } else {
                view?.visibility = View.GONE
            }
        }
    }
}
