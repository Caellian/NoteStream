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

package hr.caellian.notestream.data.playable

import android.media.MediaPlayer
import hr.caellian.notestream.data.PlayableInfo
import hr.caellian.notestream.util.RepeatState
import java.io.Serializable

interface Playable : Serializable {
    val info: PlayableInfo

    val id: String
    val playableSource: PlayableSource
    val path: String

    var title: String
    var author: String

    fun prepare(mp: MediaPlayer): Boolean

    fun skipTo(mp: MediaPlayer, ms: Int): Boolean

    interface ProgressListener {
        /**
         * Called every second while a argumentPlayable is being played.
         * @param progress current progress in milliseconds.
         */
        fun onProgressChanged(progress: Int)
    }

    interface ControlListener {
        /**
         * Called when a new argumentPlayable has been selected.
         * @param current currently selected argumentPlayable.
         */
        fun onPlayableChanged(current: Playable?)

        /**
         * Called on play/pause button click.
         * @param playing `true` if argumentPlayable has been started, `false` if it has been paused/stopped.
         */
        fun onPlayStatusChanged(playing: Boolean)

        /**
         * Called on shuffle state change.
         * @param currentState current shuffle state - `true` if shuffle is turned on, `false` otherwise.
         */
        fun onShuffleStateChanged(currentState: Boolean)

        /**
         * Called on repeat state change.
         * @param currentState current repeat state.
         */
        fun onRepeatStateChanged(currentState: RepeatState)
    }
}
