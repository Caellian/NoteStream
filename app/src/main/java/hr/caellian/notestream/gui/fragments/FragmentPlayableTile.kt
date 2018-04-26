/*
 * Copyright (C) 2018 Tin Svagelj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
import hr.caellian.notestream.NoteStream.PlayerServiceListener
import hr.caellian.notestream.R
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.gui.ActivityPlayer
import hr.caellian.notestream.gui.PlayablePopupMenu
import hr.caellian.notestream.lib.Constants

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
        rootView.findViewById<TextView>(R.id.labelSongAuthor)?.text = playable.author

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

        rootView.setOnLongClickListener {
            val playablePopupMenu = PlayablePopupMenu(it.context, it, playable)
            playablePopupMenu.show()
            true
        }

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
