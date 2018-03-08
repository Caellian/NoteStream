package hr.caellian.notestream.gui.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
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

    private var defaultTextColor: Int = 0

    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_playable_item, container, false)
        defaultTextColor = view.findViewById<TextView>(R.id.labelSongTitle)?.currentTextColor ?: 0

        return view
    }

    override fun updateView(rootView: View) {
        val playlist = argumentPlaylist ?: return
        val playable = argumentPlayable ?: return

        rootView.findViewById<TextView>(R.id.labelSongTitle)?.text = playable.title
        rootView.findViewById<TextView>(R.id.labelTileDescription)?.text = playable.author

        rootView.setOnClickListener(View.OnClickListener {
            if (playable is PlayableRemote && !playable.available) {
                return@OnClickListener
            }

            val psb = NoteStream.instance?.psb
            if (psb?.currentPlayable != playable) {
                psb?.playAt(playlist, playable)
            } else {
                psb.setPlaylist(playlist)
            }

            val intent = Intent(rootView.context, ActivityPlayer::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.putExtra(Constants.EXTRA_PLAYLIST, playlist.id)
            intent.putExtra(Constants.EXTRA_PLAYABLE, playable.id)
            startActivity(intent)
        })

        rootView.findViewById<Button>(R.id.buttonSongOptions)?.setOnClickListener(object : View.OnClickListener {
            internal var playablePopupMenu = PlayablePopupMenu(rootView.context, view!!, playable)
            override fun onClick(v: View) {
                playablePopupMenu.show()
            }
        })

        NoteStream.registerControlListener(this)
        NoteStream.registerLibraryListener(this)
    }

    override fun onPlayableChanged(current: Playable?) {
        if (current == argumentPlayable) {
            view?.findViewById<TextView>(R.id.labelSongTitle)?.setTextColor(ContextCompat.getColor(view!!.context, R.color.colorAccent))
        } else {
            view?.findViewById<TextView>(R.id.labelSongTitle)?.setTextColor(defaultTextColor)
        }
    }

    fun filter(filter: String) {
        Filter(filter).execute(this)
    }

    class Filter(private val filter: String): AsyncTask<FragmentItemPlayable, Unit, Unit>() {
        override fun doInBackground(vararg params: FragmentItemPlayable?) {
            if (params[0] == null || params[0]?.argumentPlayable == null) {
                params[0]?.view?.visibility = View.GONE
                return
            }

            if (params[0]!!.argumentPlayable is PlayableRemote) {
                if ((params[0]!!.argumentPlayable as PlayableRemote?)?.available == true) {
                    params[0]?.loadingView?.visibility = View.GONE
                    params[0]?.contentViewFrame?.visibility = View.VISIBLE
                } else {
                    params[0]?.contentViewFrame?.visibility = View.GONE
                    params[0]?.loadingView?.visibility = View.VISIBLE
                }
                return
            }

            if (params[0]?.argumentPlayable?.info?.title?.toLowerCase()?.contains(filter.toLowerCase()) == false
                    && params[0]?.argumentPlayable?.info?.author?.toLowerCase()?.contains(filter.toLowerCase()) == false) {
                params[0]?.view?.visibility = View.GONE
                return
            }

            params[0]?.loadingView?.visibility = View.GONE
            params[0]?.contentViewFrame?.visibility = View.VISIBLE
            params[0]?.view?.visibility = View.VISIBLE
            return
        }
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
