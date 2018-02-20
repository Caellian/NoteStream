package hr.caellian.notestream.data

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.PowerManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.KeyEvent

import java.util.Timer
import java.util.TimerTask

import hr.caellian.notestream.gui.NoteStreamNotification
import hr.caellian.notestream.NoteStream
import hr.caellian.notestream.data.playable.Playable
import hr.caellian.notestream.lib.Constants
import hr.caellian.notestream.util.RepeatState

class PlayerService : Service(), MediaPlayer.OnCompletionListener {
    internal var queueSize = 0

    private var ms: MediaSession? = null

    internal var playing: Boolean = false

    internal var notification: NoteStreamNotification? = null

    internal var progressHandler: Handler? = null

    private var psl: PhoneStateListener = object : PhoneStateListener() {
        var previousState = false

        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> previousState = psb?.pause()?.not() ?: false
                TelephonyManager.CALL_STATE_IDLE -> if (previousState) psb?.play()
                TelephonyManager.CALL_STATE_OFFHOOK -> previousState = psb?.pause()?.not() ?: false
            }
            super.onCallStateChanged(state, incomingNumber)
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        if (repeatState != RepeatState.ONE) {
            if (pl.getCurrentPlayable() === pl.lastPlayable && repeatState != RepeatState.ALL) {
                psb!!.pause()
            } else {
                psb!!.switchNext()
            }
        }
    }

    private class ProgressHandler(internal var mp: MediaPlayer) : Handler() {

        override fun handleMessage(msg: Message) {
            if (mp.currentPosition > pl.getCurrentPlayable()?.info?.end ?: pl.getCurrentPlayable()?.info?.length!! && mp.currentPosition < pl.getCurrentPlayable()!!.info.end + 1000) {
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

                    ms = MediaSession(this, Constants.MEDIA_SESSION_TAG)
                    ms?.setCallback(NSMediaSessionCallback())
                    ms?.isActive = true

                    progressTimer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (playing) {
                                progressHandler?.obtainMessage(1)?.sendToTarget()
                            }
                        }
                    }, 1000, 1000)

                    notification = NoteStreamNotification(this, this)
                }
                else -> {
                    mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
                    mp.setOnCompletionListener(this)
                    progressHandler = ProgressHandler(mp)
                    val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                    tm?.listen(psl, PhoneStateListener.LISTEN_CALL_STATE)
                    ms = MediaSession(this@PlayerService, Constants.MEDIA_SESSION_TAG)
                    ms?.setCallback(NSMediaSessionCallback())
                    ms?.isActive = true
                    progressTimer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if (playing) {
                                progressHandler?.obtainMessage(1)?.sendToTarget()
                            }
                        }
                    }, 1000, 1000)
                    notification = NoteStreamNotification(this, this)
                }
            }
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        tm?.listen(psl, PhoneStateListener.LISTEN_NONE)

        mp.release()
        ms?.release()
        progressTimer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        psb = PlayerServiceBinder()
        return psb
    }

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
            get() = if (!pl.isEmpty) {
                pl.getCurrentPlayable()
            } else null

        var progress: Int
            get() = mp.currentPosition
            set(progress) {
                currentPlayable!!.skipTo(mp, progress)
            }

        val isEmpty: Boolean
            get() = pl.isEmpty

        val isPlaying: Boolean
            get() = playing

        fun shufflePlay(playlist: Playlist?): Playlist {
            val old = pl
            if (playlist != null && !playlist.isEmpty) {
                mp.stop()
                pl.clear()
                pl.add(playlist)
                pl.skipTo(old.getCurrentPlayable()!!)
                pl.shuffle = true
                pl.getCurrentPlayable()!!.prepare(mp)

                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(currentPlayable!!)
                }
                play()
            }
            return old
        }

        fun playPlaylist(playlist: Playlist?): Playlist {
            val old = pl
            if (playlist != null && !playlist.isEmpty) {
                pl.clear()
                pl.add(playlist)
                pl.skipTo(playlist.getCurrentPlayable()!!)
                pl.getCurrentPlayable()!!.prepare(mp)

                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(currentPlayable!!)
                }
                play()
            }
            return old
        }

        fun playAt(playlist: Playlist?, playable: Playable): Playlist {
            val old = pl
            if (playlist != null) {
                pl.clear()
                pl.add(playlist)
                pl.skipTo(playable)
                pl.getCurrentPlayable()!!.prepare(mp)

                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(currentPlayable!!)
                }
                play()
            }
            return old
        }

        fun play(playable: Playable): Playlist {
            return playPlaylist(Playlist.get(Constants.PLAYLIST_TEMPORARY_PREFIX + playable.id, data = listOf(playable)))
        }

        fun playNext(playable: Playable) {
            pl.addNext(playable)
        }

        fun addToQueue(playable: Playable) {
            pl.addRelative(++queueSize, playable)
        }

        fun play(): Boolean {
            val old = playing
            if (!pl.isEmpty) {
                playing = true
                mp.start()

                NoteStream.instance?.library?.lastListened?.add(currentPlayable)

                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayStatusChanged(true)
                }
                if (!NoteStream.CONTROL_LISTENERS.contains(notification!!)) NoteStream.registerControlListener(notification!!)
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

        fun setPlaylist(playlist: Playlist?): Playlist {
            val old = pl
            if (playlist != null && !playlist.isEmpty) {
                var currentPlayable: Playable? = null
                if (!pl.isEmpty) {
                    currentPlayable = pl.getCurrentPlayable()
                }
                pl.clear()
                pl.add(playlist)
                if (currentPlayable != null) {
                    pl.skipTo(currentPlayable)
                } else {
                    currentPlayable = playlist.getCurrentPlayable() ?: playlist.firstPlayable
                    pl.skipTo(currentPlayable)
                    currentPlayable.prepare(mp)
                    for (controlListener in NoteStream.CONTROL_LISTENERS) {
                        controlListener.onPlayableChanged(currentPlayable)
                    }
                }
            }
            return old
        }

        fun switchPrevious(): Boolean {
            if (mp.currentPosition > 3000) {
                mp.seekTo(0)
                return false
            } else {
                if (queueSize > 0) queueSize++
                if (pl.isEmpty) return false
                pl.switchPrevious()!!.prepare(mp)
                if (isPlaying) play()
                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(currentPlayable!!)
                }
                return true
            }
        }

        fun switchNext(): Boolean {
            if (queueSize > 0) queueSize--
            if (pl.isEmpty) return false
            pl.switchNext()!!.prepare(mp)
            if (isPlaying) play()
            for (controlListener in NoteStream.CONTROL_LISTENERS) {
                controlListener.onPlayableChanged(currentPlayable!!)
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
            if (!pl.isEmpty) {
                val old = pl.shuffle
                if (old != shuffle) queueSize = 0
                pl.shuffle = shuffle
                for (controlListener in NoteStream.CONTROL_LISTENERS) {
                    controlListener.onShuffleStateChanged(shuffle)
                }
                return old
            }
            return false
        }

        fun doShuffle(): Boolean {
            return pl.shuffle
        }
    }

    companion object {
        // http://www.tutorialsface.com/2015/08/android-custom-notification-tutorial/

        const val DEFAULT_PROGRESS_CHANGE = 5000

        private val progressTimer = Timer()
        internal var pl = Playlist.get(Constants.PLAYLIST_TEMPORARY_PREFIX + "currentlyPlayed")
        internal var mp = MediaPlayer()
        internal var repeatState = RepeatState.NONE

        internal var psb: PlayerServiceBinder? = null
    }
}
