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

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.R
import hr.caellian.notestream.data.PlayerService
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.gui.ActivityPlayer
import hr.caellian.notestream.util.RepeatState
import java.util.*

class FragmentBarPlayer : Fragment(), Playable.ControlListener, Playable.ProgressListener {
    internal var psb = NoteStream.instance?.psb

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bar_player, container, false)

        view.setOnClickListener {
            val intent = Intent(view.context, ActivityPlayer::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        val buttonPrevious = view.findViewById<Button>(R.id.buttonPrevious)
        val buttonNext = view.findViewById<Button>(R.id.buttonNext)

        buttonPrevious.setOnClickListener { psb?.switchPrevious() }

        buttonPrevious.setOnLongClickListener {
            val holdTimer = Timer()
            holdTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (buttonPrevious.isPressed) {
                        psb?.progress = Math.max(psb!!.progress - 5000, 0)
                    } else {
                        holdTimer.cancel()
                    }
                }
            }, 1000, 500)
            true
        }

        buttonNext.setOnClickListener { psb?.switchNext() }

        buttonNext.setOnLongClickListener {
            val holdTimer = Timer()
            holdTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (buttonNext.isPressed) {
                        psb!!.progress = Math.min(psb!!.progress + 5000,
                                psb!!.currentPlayable!!.info.length)
                    } else {
                        holdTimer.cancel()
                    }
                }
            }, 1000, 500)
            true
        }

        view.findViewById<Button>(R.id.buttonTogglePlay).setOnClickListener { psb?.togglePlay() }

        if (psb?.isEmpty != true || psb?.isPlaying != true) {
            view.visibility = View.GONE
        }

        NoteStream.registerControlListener(this)
        NoteStream.registerProgressListener(this)
        NoteStream.registerPlayerServiceListener(object : NoteStream.PlayerServiceListener() {
            override fun onPlayerServiceConnected(psb: PlayerService.PlayerServiceBinder) {
                this@FragmentBarPlayer.psb = psb
                if (!psb.isEmpty && psb.isPlaying) {
                    this@FragmentBarPlayer.onProgressChanged(psb.progress)
                    this@FragmentBarPlayer.onPlayableChanged(psb.currentPlayable)
                    this@FragmentBarPlayer.onPlayStatusChanged(psb.isPlaying)
                    view.visibility = View.VISIBLE
                }
            }

            override fun onPlayerServiceDisconnected() {
                view.visibility = View.GONE
            }
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        NoteStream.CONTROL_LISTENERS += this
        NoteStream.PROGRESS_LISTENERS += this
    }

    override fun onPause() {
        super.onPause()
        NoteStream.CONTROL_LISTENERS -= this
        NoteStream.PROGRESS_LISTENERS -= this
    }

    override fun onProgressChanged(progress: Int) {
        view?.findViewById<ProgressBar>(R.id.songProgressBar)?.progress = progress
    }

    override fun onPlayableChanged(current: Playable?) {
        view?.findViewById<TextView>(R.id.labelSongTitle)?.text = current?.title
        view?.findViewById<TextView>(R.id.labelSongAuthor)?.text = current?.author
        view?.findViewById<ProgressBar>(R.id.songProgressBar)?.max = current?.info?.length ?: 0
        onProgressChanged(0)
        view?.visibility = View.VISIBLE
    }

    override fun onPlayStatusChanged(playing: Boolean) {
        if (!playing) {
            view?.findViewById<Button>(R.id.buttonTogglePlay)?.background = ContextCompat.getDrawable(view.context, R.drawable.ic_play_circle)
        } else {
            view?.findViewById<Button>(R.id.buttonTogglePlay)?.background = ContextCompat.getDrawable(view.context, R.drawable.ic_pause_circle)
        }
    }

    override fun onShuffleStateChanged(currentState: Boolean) {}
    override fun onRepeatStateChanged(currentState: RepeatState) {}
}
