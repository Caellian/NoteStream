package hr.caellian.notestream.gui.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import hr.caellian.notestream.NoteStream

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

abstract class FragmentPlayableMediator(val orientation: Int = LinearLayout.HORIZONTAL) : Fragment(), Playable.ControlListener, Library.LibraryListener, PlayableRemote.AvailabilityListener {

    var argumentPlaylist: Playlist? = null
        get() {
            if (field == null) {
                field = Playlist.get(arguments.getString(Constants.EXTRA_PLAYLIST))
            }
            return field
        }

    var argumentPlayable: Playable? = null
        get() {
            if (field == null) {
                field = argumentPlaylist?.getPlayable(arguments.getString(Constants.EXTRA_PLAYABLE))
            }
            return field
        }

    lateinit var contentViewFrame: FrameLayout
    lateinit var contentView: View
    lateinit var loadingView: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: FrameLayout = inflater.inflate(R.layout.content_loadable, container, false) as FrameLayout

        contentViewFrame = view.findViewById(R.id.contentView)
        contentView = inflateContentView(inflater, contentViewFrame, savedInstanceState)
        contentViewFrame.addView(contentView)
        contentViewFrame.visibility = View.GONE
        updateView(contentView)

        loadingView = view.findViewById(R.id.loadingView)
        loadingView.orientation = orientation

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (argumentPlayable is PlayableRemote && (argumentPlayable as PlayableRemote?)?.available != true) {
            NoteStream.registerAvailabilityListener(this)
        } else {
            updateView(contentView)
        }
    }

    protected abstract fun inflateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    protected abstract fun updateView(rootView: View)

    override fun onPlayStatusChanged(playing: Boolean) {}
    override fun onShuffleStateChanged(currentState: Boolean) {}
    override fun onRepeatStateChanged(currentState: RepeatState) {}

    override fun onPlayableAddedToPlaylist(playable: Playable, playlist: Playlist) {
        if (playable == argumentPlayable && playlist == argumentPlaylist) {
            if (playable is PlayableRemote && !playable.available) {
                return
            }

            updateView(contentView)
            loadingView.visibility = View.GONE
            contentViewFrame.visibility = View.VISIBLE
        }
    }

    override fun onPlayableRemovedFromPlaylist(playable: Playable, playlist: Playlist) {
        if (playable == argumentPlayable && playlist == argumentPlaylist) {
            view.visibility = View.GONE
        }
    }

    override fun onAvailableStateChanged(playableID: String, state: Boolean) {
        if (playableID == argumentPlayable?.id) {
            if (state) {
                updateView(contentView)
                loadingView.visibility = View.GONE
                contentViewFrame.visibility = View.VISIBLE
            } else {
                contentViewFrame.visibility = View.GONE
                loadingView.visibility = View.VISIBLE
            }
        }
    }
}
