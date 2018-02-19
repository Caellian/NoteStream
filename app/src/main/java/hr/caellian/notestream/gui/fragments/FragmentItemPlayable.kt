package hr.caellian.notestream.gui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.gui.ActivityPlayer
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.gui.PlayablePopupMenu
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 16/06/17.
 */

class FragmentItemPlayable : FragmentPlayableMediator() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.playable_item, container, false)
        defaultTextColor = view.findViewById<TextView>(R.id.labelSongTitle).currentTextColor

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val playable = playable

        if (playable is PlayableRemote && !playable.available) {
            NoteStream.registerAvailabilityListener(this)
            this.view.visibility = View.GONE
        } else {
            updateView()
        }
    }

    override fun updateView() {
        val playlist = playlist
        val playable = playable ?: return

        view?.findViewById<TextView>(R.id.labelSongTitle)?.text = playable.title
        view?.findViewById<TextView>(R.id.labelSongAuthor)?.text = playable.author

        view?.findViewById<TextView>(R.id.labelSongTitle)?.setOnClickListener(View.OnClickListener {
            if (playable is PlayableRemote && !playable.available) {
                return@OnClickListener
            }

            val psb = NoteStream.instance?.psb
            if (psb?.currentPlayable != playable) {
                psb?.playAt(playlist, playable)
            } else {
                psb.setPlaylist(playlist)
            }

            val intent = Intent(view!!.context, ActivityPlayer::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.putExtra(Constants.EXTRA_PLAYLIST, playlist.id)
            intent.putExtra(Constants.EXTRA_PLAYABLE, playable.id)
            startActivity(intent)
        })

        view?.findViewById<Button>(R.id.buttonSongOptions)?.setOnClickListener(object : View.OnClickListener {
            internal var playablePopupMenu = PlayablePopupMenu(view!!.context, view!!, playable)
            override fun onClick(v: View) {
                playablePopupMenu.show()
            }
        })

        NoteStream.registerControlListener(this)
        NoteStream.registerLibraryListener(this)
    }

    fun filter(filter: String): Boolean {
        val playable = playable
        if (playable == null) {
            view?.visibility = View.GONE
            return false
        }
        if (playable.info.title?.toLowerCase()?.contains(filter.toLowerCase())?.not() == true
                && playable.info.author?.toLowerCase()?.contains(filter.toLowerCase())?.not() == true) {
            view?.visibility = View.GONE
            return false
        }
        view?.visibility = View.VISIBLE
        return true
    }

    companion object {
        fun newInstance(playable: Playable, playlist: Playlist): FragmentItemPlayable {
            val fragment = FragmentItemPlayable()

            val args = Bundle()
            args.putString(Constants.EXTRA_PLAYLIST, playlist.id)
            args.putString(Constants.EXTRA_PLAYABLE, playable.id)
            fragment.arguments = args
            return fragment
        }
    }
}
