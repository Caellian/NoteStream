package hr.caellian.notestream.data.playable

import android.media.MediaPlayer

import java.io.Serializable

import hr.caellian.notestream.data.PlayableInfo
import hr.caellian.notestream.util.RepeatState

/**
 * Created by caellyan on 16/04/17.
 */

interface Playable : Serializable {
    val info: PlayableInfo

    val id: String
    val playableSource: PlayableSource
    val path: String
    val location: String

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
