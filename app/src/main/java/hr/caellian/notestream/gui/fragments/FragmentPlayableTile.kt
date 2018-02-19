package hr.caellian.notestream.gui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.gui.ActivityPlayer
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.gui.PlayablePopupMenu
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 21/06/17.
 */

class FragmentPlayableTile : FragmentPlayableMediator() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.playable_tile, container, false)
        defaultTextColor = view?.findViewById<TextView>(R.id.labelSongTitle)?.currentTextColor ?: 0

        val playable = playable

        if (playable is PlayableRemote && !playable.available) {
            NoteStream.registerAvailabilityListener(this)
            view.visibility = View.GONE
        } else {
            updateView()
        }

        return view
    }

    override fun updateView() {
        val playlist = playlist
        val playable = playable ?: return

        view?.findViewById<ImageView>(R.id.tileBG)?.setImageBitmap(playable.info.cover)
        view?.findViewById<TextView>(R.id.labelSongTitle)?.text = playable.info.title
        view?.findViewById<TextView>(R.id.labelSongAuthor)?.text = playable.info.author

        view!!.setOnClickListener(View.OnClickListener {
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

        view!!.setOnLongClickListener(object : View.OnLongClickListener {
            internal var playablePopupMenu = PlayablePopupMenu(view!!.context, view, playable)

            override fun onLongClick(v: View): Boolean {
                playablePopupMenu.show()
                return true
            }
        })

        NoteStream.registerControlListener(this)
        NoteStream.registerLibraryListener(this)
    }

    companion object {

        fun newInstance(playable: Playable, playlist: Playlist): FragmentPlayableTile {
            val fragment = FragmentPlayableTile()

            val args = Bundle()
            args.putString(Constants.EXTRA_PLAYLIST, playlist.id)
            args.putString(Constants.EXTRA_PLAYABLE, playable.id)
            fragment.arguments = args
            return fragment
        }
    }
}
