package hr.caellian.notestream.gui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.NoteStream.*
import hr.caellian.notestream.R
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.gui.ActivityPlayer
import hr.caellian.notestream.data.Playlist
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.gui.PlayablePopupMenu
import hr.caellian.notestream.lib.Constants

/**
 * Created by caellyan on 21/06/17.
 */

class FragmentPlayableTile : FragmentPlayableMediator(LinearLayout.VERTICAL) {

    private var defaultTextColor: Int = 0

    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_tile, container, false)
        defaultTextColor = view.findViewById<TextView>(R.id.labelTileTitle)?.currentTextColor ?: 0

        return view
    }

    override fun updateView(rootView: View) {
        val playlist = argumentPlaylist ?: return
        val playable = argumentPlayable ?: return

        rootView.findViewById<ImageView>(R.id.imageTileBackground)?.setImageBitmap(playable.info.cover)
        rootView.findViewById<TextView>(R.id.labelTileTitle)?.text = playable.title
        rootView.findViewById<TextView>(R.id.labelTileDescription)?.text = playable.author

        rootView.setOnClickListener(View.OnClickListener {
            if (playable is PlayableRemote && !playable.available) {
                return@OnClickListener
            }

            NoteStream.registerPlayerServiceListener(object : PlayerServiceListener() {
                override fun onPlayerServiceConnected(psb: PlayerService.PlayerServiceBinder) {
                    if (psb.currentPlayable != playable) {
                        psb.playAt(playlist, playable)
                    } else {
                        psb.setPlaylist(playlist)
                    }
                }
            })

            val intent = Intent(rootView.context, ActivityPlayer::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.putExtra(Constants.EXTRA_PLAYLIST, playlist.id)
            intent.putExtra(Constants.EXTRA_PLAYABLE, playable.id)
            startActivity(intent)
        })

        rootView.setOnLongClickListener(object : View.OnLongClickListener {
            internal var playablePopupMenu = PlayablePopupMenu(view!!.context, view, playable)

            override fun onLongClick(v: View): Boolean {
                playablePopupMenu.show()
                return true
            }
        })

        NoteStream.registerControlListener(this)
        NoteStream.registerLibraryListener(this)
    }

    override fun onPlayableChanged(current: Playable?) {
        if (current == argumentPlayable) {
            view?.findViewById<TextView>(R.id.labelTileTitle)?.setTextColor(ContextCompat.getColor(view!!.context, R.color.colorAccent))
        } else {
            view?.findViewById<TextView>(R.id.labelTileTitle)?.setTextColor(defaultTextColor)
        }
    }

    companion object {
        fun create(playlist: Playlist, playable: Playable): FragmentPlayableTile {
            val fragment = FragmentPlayableTile()

            val args = Bundle()
            args.putString(Constants.EXTRA_PLAYLIST, playlist.id)
            args.putString(Constants.EXTRA_PLAYABLE, playable.id)
            fragment.arguments = args
            return fragment
        }
    }
}
