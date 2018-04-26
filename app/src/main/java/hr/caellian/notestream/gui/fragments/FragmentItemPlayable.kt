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

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playable.PlayableRemote
import hr.caellian.notestream.data.playlist.PlaylistIterator
import hr.caellian.notestream.gui.ActivityPlayer
import hr.caellian.notestream.gui.PlayablePopupMenu
import hr.caellian.notestream.lib.Constants

class FragmentItemPlayable : FragmentPlayableMediator() {

    private var defaultTextColor: Int = 0

    override fun inflateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_playable_item, container, false)
        defaultTextColor = view.findViewById<TextView>(R.id.labelSongTitle)?.currentTextColor ?: 0

        return view
    }

    override fun updateView(rootView: View) {
        val playlist = argumentPlaylist ?: return
        val iterator = argumentIterator ?: return
        val playable = argumentPlayable ?: return

        rootView.findViewById<TextView>(R.id.labelSongTitle)?.text = playable.title
        rootView.findViewById<TextView>(R.id.labelSongAuthor)?.text = playable.author

        rootView.setOnClickListener(View.OnClickListener {
            if (playable is PlayableRemote && !playable.available) {
                return@OnClickListener
            }

            val psb = NoteStream.instance.psb
            if (psb?.currentPlayable != playable) {
                psb?.playAt(iterator, playable)
            } else {
                psb.setIterator(iterator)
            }

            val intent = Intent(rootView.context, ActivityPlayer::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.putExtra(Constants.EXTRA_PLAYLIST, playlist.id)
            intent.putExtra(Constants.EXTRA_PLAYABLE, playable.id)
            intent.putExtra(Constants.EXTRA_ITERATOR, iterator.id)
            intent.putExtra(Constants.EXTRA_ITERATOR_ASCENDING, iterator.ascending)
            startActivity(intent)
        })

        rootView.findViewById<Button>(R.id.buttonSongOptions).setOnClickListener {
            val playablePopupMenu = PlayablePopupMenu(it.context, it, playable)
            playablePopupMenu.show()
        }

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
        if (argumentPlayable == null) {
            val anim = ObjectAnimator.ofInt(view, "layout_height", view.height, 0)
            anim.setDuration(1000).addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    view?.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    view?.isEnabled = true
                }

                override fun onAnimationStart(animation: Animator?) {
                    view?.isEnabled = false
                }
            })
            return
        }

        if (argumentPlayable is PlayableRemote) {
            if ((argumentPlayable as PlayableRemote?)?.available == true) {
                loadingView.visibility = View.GONE
                contentViewFrame.visibility = View.VISIBLE
            } else {
                contentViewFrame.visibility = View.GONE
                loadingView.visibility = View.VISIBLE
            }
            return
        }

        if (argumentPlayable?.title?.toLowerCase()?.contains(filter.toLowerCase()) == false
                && argumentPlayable?.author?.toLowerCase()?.contains(filter.toLowerCase()) == false) {
            view?.visibility = View.GONE
            return
        }

        loadingView.visibility = View.GONE
        contentViewFrame.visibility = View.VISIBLE
        view?.visibility = View.VISIBLE
        return
    }

    companion object {
        fun newInstance(playable: Playable, playlistIterator: PlaylistIterator): FragmentItemPlayable {
            val fragment = FragmentItemPlayable()

            val args = Bundle()
            args.putString(Constants.EXTRA_PLAYLIST, playlistIterator.playlist?.id
                    ?: Constants.PLAYLIST_EMPTY_ID)
            args.putString(Constants.EXTRA_ITERATOR, playlistIterator.id)
            args.putBoolean(Constants.EXTRA_ITERATOR_ASCENDING, playlistIterator.ascending)
            args.putString(Constants.EXTRA_PLAYABLE, playable.id)
            fragment.arguments = args
            return fragment
        }
    }
}
