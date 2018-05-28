/*
 * The MIT License (MIT)
 * NoteStream, android music player and streamer
 * Copyright (c) 2018 Tin Švagelj <tin.svagelj.email@gmail.com> a.k.a. Caellian
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package hr.caellian.notestream.data

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.*
import android.support.annotation.RequiresApi
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.KeyEvent
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.data.playlist.Playlist
import hr.caellian.notestream.data.playlist.PlaylistIterator
import hr.caellian.notestream.gui.Notification
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.RepeatState
import java.util.*

class PlayerService : Service(), MediaPlayer.OnCompletionListener {
    internal var queueSize = 0

    private var ms: MediaSession? = null

    internal var playing: Boolean = false

    internal var notification: Notification? = null

    internal var progressHandler: Handler? = null

    private var psl: PhoneStateListener = object : PhoneStateListener() {
        var previousState = false

        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> if (previousState) psb?.play()
                else -> previousState = psb?.pause()?.not() ?: false
            }
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        if (repeatState != RepeatState.ONE) {
            if (iterator?.hasNext() == false && repeatState != RepeatState.ALL) {
                psb!!.pause()
            } else {
                psb!!.switchNext()
            }
        }
    }

    private class ProgressHandler(internal var mp: MediaPlayer) : Handler() {

        override fun handleMessage(msg: Message) {
            if (mp.currentPosition > iterator?.current()?.info?.end ?: iterator?.current()?.info?.length ?: 0 && mp.currentPosition < iterator?.current()?.info?.end ?: 0 + 1000) {
                psb!!.switchNext()
            } else {
                for (progressListener in NoteStream.PROGRESS_LISTENERS) {
                    progressListener.onProgressChanged(mp.currentPosition)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == null) intent.action = Constants.ACTION_INIT

            when (intent.action) {
                Constants.ACTION_PLAY -> psb!!.play()
                Constants.ACTION_PAUSE -> psb!!.pause()
                Constants.ACTION_TOGGLE_PLAY -> psb!!.togglePlay()
                Constants.ACTION_STOP -> {
                    if (notification != null) {
                        notification!!.cancelNotification()
                    }
                    psb!!.stop()
                }
                Constants.ACTION_SWITCH_PREVIOUS -> psb!!.switchPrevious()
                Constants.ACTION_SWITCH_NEXT -> psb!!.switchNext()
                Constants.ACTION_TOGGLE_REPEAT -> psb!!.toggleRepeat()
                Constants.ACTION_TOGGLE_SHUFFLE -> psb!!.setShuffle(!psb!!.doShuffle())
                Constants.ACTION_INIT -> {
                    mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)

                    mp.setOnCompletionListener(this)
                    progressHandler = ProgressHandler(mp)

                    val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                    tm?.listen(psl, PhoneStateListener.LISTEN_CALL_STATE)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ms = MediaSession(this, Constants.MEDIA_SESSION_TAG)
                        ms?.setCallback(NSMediaSessionCallback())
                        ms?.isActive = true
                    }

                    progressTimer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (playing) {
                                progressHandler?.obtainMessage(1)?.sendToTarget()
                            }
                        }
                    }, 1000, 1000)

                    notification = Notification(this, this)
                }
                else -> {
                    mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
                    mp.setOnCompletionListener(this)
                    progressHandler = ProgressHandler(mp)
                    val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                    tm?.listen(psl, PhoneStateListener.LISTEN_CALL_STATE)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ms = MediaSession(this@PlayerService, Constants.MEDIA_SESSION_TAG)
                        ms?.setCallback(NSMediaSessionCallback())
                        ms?.isActive = true
                    }

                    progressTimer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (playing) {
                                progressHandler?.obtainMessage(1)?.sendToTarget()
                            }
                        }
                    }, 1000, 1000)
                    notification = Notification(this, this)
                }
            }
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        tm?.listen(psl, PhoneStateListener.LISTEN_NONE)

        mp.release()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ms?.release()
        }
        progressTimer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        psb = PlayerServiceBinder()
        return psb
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    inner class NSMediaSessionCallback : MediaSession.Callback() {

        override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
            val intentAction = mediaButtonIntent.action
            if (Intent.ACTION_MEDIA_BUTTON == intentAction) {
                val event = mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                if (event != null) {
                    if (event.action == KeyEvent.ACTION_UP) {
                        // RELEASE

                        when (event.keyCode) {
                            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD, KeyEvent.KEYCODE_MEDIA_STEP_FORWARD -> psb!!.progress = Math.min(psb!!.progress + PlayerService.DEFAULT_PROGRESS_CHANGE,
                                    psb!!.currentPlayable!!.info.length)
                            KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD -> psb!!.progress = Math.max(psb!!.progress - PlayerService.DEFAULT_PROGRESS_CHANGE, 0)
                            KeyEvent.KEYCODE_MEDIA_STOP -> psb!!.stop()
                            KeyEvent.KEYCODE_MEDIA_PLAY -> psb!!.play()
                            KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_MEDIA_PAUSE -> psb!!.pause()
                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> if (playing) {
                                psb!!.pause()
                            } else {
                                psb!!.play()
                            }
                            KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD, KeyEvent.KEYCODE_MEDIA_NEXT -> psb!!.switchNext()
                            KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD, KeyEvent.KEYCODE_MEDIA_PREVIOUS -> psb!!.switchPrevious()
                        }
                    }
                }
            }
            return super.onMediaButtonEvent(mediaButtonIntent)
        }
    }

    inner class PlayerServiceBinder : Binder() {
        var repeatState: RepeatState = RepeatState.NONE

        val currentPlayable: Playable?
            get() = iterator?.current()

        var progress: Int
            get() = mp.currentPosition
            set(progress) {
                currentPlayable?.skipTo(mp, progress)
            }

        val isEmpty: Boolean
            get() = iterator?.isEmpty ?: true

        val isPlaying: Boolean
            get() = playing

        fun shufflePlay(playlist: Playlist?): Playlist? {
            val old = iterator?.playlist
            if (playlist != null && !playlist.isEmpty) {
                mp.stop()
                iterator = PlaylistIterator.Random(playlist)
                iterator?.current()?.prepare(mp)

                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(currentPlayable)
                }
                play()
            }
            return old
        }

        fun playPlaylist(playlist: Playlist?): Playlist? {
            val old = iterator?.playlist
            if (playlist != null && !playlist.isEmpty) {
                iterator?.reassign(playlist)
                iterator?.current()?.prepare(mp)

                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(currentPlayable)
                }
                play()
            }
            return old
        }

        fun playAt(plIterator: PlaylistIterator, playable: Playable): Playlist? {
            val old = iterator?.playlist
            iterator = plIterator
            iterator?.switchTo(playable)?.current()?.prepare(mp)

            for (controlListener in NoteStream.CONTROL_LISTENERS) {
                controlListener.onPlayableChanged(currentPlayable)
            }
            play()
            return old
        }

        fun playAt(playlist: Playlist?, playable: Playable?): Playlist? {
            val old = iterator?.playlist
            if (playlist != null) {
                iterator?.reassign(playlist)
                iterator?.switchTo(playable)?.current()?.prepare(mp)

                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(currentPlayable)
                }
                play()
            }
            return old
        }

        fun play(playable: Playable): Playlist? {
            return playPlaylist(Playlist.get(Constants.PLAYLIST_TEMPORARY_PREFIX + playable.id, data = listOf(playable)))
        }

        fun playNext(playable: Playable) {
            iterator?.add(playable, iterator!!.current + 1)
        }

        fun playNext(playlist: Playlist) {
            iterator?.addAll(playlist.playlist, iterator!!.current + 1)
        }

        fun addToQueue(playable: Playable) {
//            iterator?.queue(playable, iterator.current + ++queueSize)
        }

        fun play(): Boolean {
            val old = playing
            if (iterator?.isEmpty == false) {
                playing = true
                mp.start()

                currentPlayable?.let {
                    NoteStream.instance.data.lastListened.add(it)
                }

                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayStatusChanged(true)
                }

                notification?.let {
                    NoteStream.registerControlListener(it)
                }
            }
            return old
        }

        fun togglePlay(): Boolean {
            return if (playing) {
                pause()
            } else {
                play()
            }
        }

        fun pause(): Boolean {
            val old = !playing
            playing = false
            mp.pause()
            for (controlListener in NoteStream.CONTROL_LISTENERS) {
                controlListener.onPlayStatusChanged(false)
            }
            return old
        }

        fun stop() {
            for (controlListener in NoteStream.CONTROL_LISTENERS) {
                controlListener.onPlayStatusChanged(false)
            }
            playing = false
            mp.stop()
        }

        fun setPlaylist(playlist: Playlist?): Playlist? {
            val old = iterator?.playlist
            if (playlist != null && !playlist.isEmpty) {
                iterator?.reassign(playlist)
            }
            return old
        }

        fun setIterator(playlistIterator: PlaylistIterator): PlaylistIterator? {
            val old = iterator
            iterator = playlistIterator
            return old
        }

        fun switchPrevious(): Boolean {
            if (mp.currentPosition > 3000) {
                mp.seekTo(0)
                return false
            } else {
                if (queueSize > 0) queueSize++
                if (iterator == null || iterator?.isEmpty == true) return false
                iterator?.switchPrevious()?.prepare(mp)
                if (isPlaying) play()
                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(currentPlayable)
                }
                return true
            }
        }

        fun switchNext(): Boolean {
            if (queueSize > 0) queueSize--
            if (iterator == null || iterator?.isEmpty == true) return false
            iterator?.switchNext()?.prepare(mp)
            if (isPlaying) play()
            for (controlListener in NoteStream.CONTROL_LISTENERS) {
                controlListener.onPlayableChanged(currentPlayable)
            }
            return true
        }

        fun toggleRepeat() {
            repeatState = repeatState.next()
            when (repeatState) {
                RepeatState.NONE, RepeatState.ALL -> mp.isLooping = false
                RepeatState.ONE -> mp.isLooping = true
            }

            for (controlListener in NoteStream.CONTROL_LISTENERS) {
                controlListener.onRepeatStateChanged(repeatState)
            }
        }

        fun setShuffle(shuffle: Boolean): Boolean {
            if (iterator?.isEmpty == false) {
                val old = iterator is PlaylistIterator.Random
                if (old != shuffle) queueSize = 0
                iterator = iterator?.setRandom(shuffle)
                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onShuffleStateChanged(shuffle)
                }
                return old
            }
            return false
        }

        fun doShuffle(): Boolean {
            return iterator?.id == Constants.ITERATOR_RANDOM_ID
        }
    }

    companion object {
        // http://www.tutorialsface.com/2015/08/android-custom-notification-tutorial/
        const val DEFAULT_PROGRESS_CHANGE = 10000

        private val progressTimer = Timer()

        var iterator: PlaylistIterator? = null

        internal var mp = MediaPlayer()
        internal var repeatState = RepeatState.NONE

        internal var psb: PlayerServiceBinder? = null
    }
}
