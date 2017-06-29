package hr.caellian.notestream.data;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import hr.caellian.notestream.gui.NoteStreamNotification;
import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.RepeatState;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener{
    // https://github.com/smedic/Android-YouTube-Background-Player/blob/master/app/src/main/java/com/smedic/tubtub/BackgroundAudioService.java
    // http://www.helloandroid.com/tutorials/musicdroid-audio-player-part-ii
    // http://www.tutorialsface.com/2015/08/android-custom-notification-tutorial/

    public static final int DEFAULT_PROGRESS_CHANGE = 5000;

    private static Timer progressTimer = new Timer();

    int queueSize = 0;
    static Playlist pl = Playlist.get(Playlist.TEMPORARY_PREFIX + "currentlyPlayed");
    static MediaPlayer mp = new MediaPlayer();
    MediaSession ms;
    static RepeatState repeatState = RepeatState.NONE;

    boolean playing;

    static PlayerServiceBinder psb = null;

    NoteStreamNotification notification;

    Handler progressHandler;

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (repeatState != RepeatState.ONE) {
            if (pl.getCurrentPlayable() == pl.getLastPlayable() && repeatState != RepeatState.ALL) {
                psb.pause();
            } else {
                psb.switchNext();
            }
        }
    }

    private static class ProgressHandler extends Handler {
        MediaPlayer mp;

        public ProgressHandler(MediaPlayer mediaPlayer) {
            super();
            mp = mediaPlayer;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mp.getCurrentPosition() > pl.getCurrentPlayable().getMetadata().getEnd() &&
                    mp.getCurrentPosition() < pl.getCurrentPlayable().getMetadata().getEnd() + 1000) {
                psb.switchNext();
            } else {
                for (Playable.ProgressListener progressListener : NoteStream.PROGRESS_LISTENERS) {
                    progressListener.onProgressChanged(mp.getCurrentPosition());
                }
            }
        }
    }

    protected PhoneStateListener psl = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                psb.pause();
            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                psb.play();
            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                psb.pause();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() == null) intent.setAction(Constants.ACTION_INIT);

            switch (intent.getAction()) {
                case Constants.ACTION_PLAY:
                    psb.play();
                    break;
                case Constants.ACTION_PAUSE:
                    psb.pause();
                    break;
                case Constants.ACTION_TOGGLE_PLAY:
                    psb.togglePlay();
                    break;
                case Constants.ACTION_STOP:
                    if (notification != null) {
                        notification.cancelNotification();
                    }
                    psb.stop();
                    break;
                case Constants.ACTION_SWITCH_PREVIOUS:
                    psb.switchPrevious();
                    break;
                case Constants.ACTION_SWITCH_NEXT:
                    psb.switchNext();
                    break;
                case Constants.ACTION_TOGGLE_REPEAT:
                    psb.toggleRepeat();
                    break;
                case Constants.ACTION_TOGGLE_SHUFFLE:
                    psb.setShuffle(!psb.doShuffle());
                    break;
                case Constants.ACTION_INIT:
                default:
                    mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);

                    mp.setOnCompletionListener(this);
                    progressHandler = new ProgressHandler(mp);

                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    if(tm != null) {
                        tm.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);
                    }

                    ms = new MediaSession(PlayerService.this, Constants.MEDIA_SESSION_TAG);
                    ms.setCallback(new NSMediaSessionCallback());
                    ms.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
                    ms.setActive(true);

                    progressTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (playing) {
                                progressHandler.obtainMessage(1).sendToTarget();
                            }
                        }
                    }, 1000, 1000);

                    notification = new NoteStreamNotification(this, this);
            }
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(tm != null) {
            tm.listen(psl, PhoneStateListener.LISTEN_NONE);
        }

        mp.release();
        ms.release();
        progressTimer.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return psb = new PlayerServiceBinder();
    }

    public class NSMediaSessionCallback extends MediaSession.Callback {

        @Override
        public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
            String intentAction = mediaButtonIntent.getAction();
            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                final KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event != null) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        // RELEASE

                        switch (event.getKeyCode()) {
                            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                            case KeyEvent.KEYCODE_MEDIA_STEP_FORWARD:
                                psb.setProgress(Math.min(psb.getProgress() + PlayerService.DEFAULT_PROGRESS_CHANGE,
                                        psb.getCurrentPlayable().getMetadata().getLength()));
                                break;
                            case KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD:
                                psb.setProgress(Math.max(psb.getProgress() - PlayerService.DEFAULT_PROGRESS_CHANGE, 0));
                                break;
                            case KeyEvent.KEYCODE_MEDIA_STOP:
                                psb.stop();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                                psb.play();
                                break;
                            case KeyEvent.KEYCODE_HEADSETHOOK:
                            case KeyEvent.KEYCODE_SPACE:
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                                psb.pause();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                if (playing) {
                                    psb.pause();
                                } else {
                                    psb.play();
                                }
                                break;
                            case KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD:
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                psb.switchNext();
                                break;
                            case KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD:
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                psb.switchPrevious();
                                break;
                        }
                    }
                }
            }
            return super.onMediaButtonEvent(mediaButtonIntent);
        }
    }

    public class PlayerServiceBinder extends Binder {

        public Playlist shufflePlay(Playlist playlist) {
            Playlist old = pl;
            if (playlist != null && !playlist.isEmpty()) {
                mp.stop();
                pl.clear();
                pl.add(playlist);
                pl.skipTo(old.getCurrentPlayable());
                pl.setShuffle(true);
                pl.getCurrentPlayable().prepare(mp);

                for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(getCurrentPlayable());
                }
                play();
            }
            return old;
        }

        public Playlist playPlaylist(Playlist playlist) {
            Playlist old = pl;
            if (playlist != null && !playlist.isEmpty()) {
                pl.clear();
                pl.add(playlist);
                pl.skipTo(playlist.getCurrentPlayable());
                pl.getCurrentPlayable().prepare(mp);

                for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(getCurrentPlayable());
                }
                play();
            }
            return old;
        }

        public Playlist playAt(Playlist playlist, Playable playable) {
            Playlist old = pl;
            if (playlist != null) {
                pl.clear();
                pl.add(playlist);
                pl.skipTo(playable);
                pl.getCurrentPlayable().prepare(mp);

                for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(getCurrentPlayable());
                }
                play();
            }
            return old;
        }

        public Playlist play(final Playable playable) {
            return playPlaylist(Playlist.get(Playlist.TEMPORARY_PREFIX + playable.getID(), Collections.singletonList(playable)));
        }

        public void playNext(Playable playable) {
            pl.addNext(playable);
        }

        public void addToQueue(Playable playable) {
            pl.addRelative(++queueSize, playable);
        }

        public boolean play() {
            boolean old = playing;
            if (!pl.isEmpty()) {
                playing = true;
                mp.start();

                NoteStream.getInstance().library.lastListened.add(getCurrentPlayable());

                for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayStatusChanged(true);
                }
                if (!NoteStream.CONTROL_LISTENERS.contains(notification)) NoteStream.registerControlListener(notification);
            }
            return old;
        }

        public boolean togglePlay() {
            if (playing) {
                return pause();
            } else {
                return play();
            }
        }

        public boolean pause() {
            boolean old = !playing;
            playing = false;
            mp.pause();
            for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                controlListener.onPlayStatusChanged(false);
            }
            return old;
        }

        public void stop() {
            for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                controlListener.onPlayStatusChanged(false);
            }
            playing = false;
            mp.stop();
        }

        public Playlist setPlaylist(Playlist playlist) {
            Playlist old = pl;
            if (playlist != null && !playlist.isEmpty()) {
                Playable currentPlayable = null;
                if (!pl.isEmpty()) {
                    currentPlayable = pl.getCurrentPlayable();
                }
                pl.clear();
                pl.add(playlist);
                if (currentPlayable != null) {
                    pl.skipTo(currentPlayable);
                } else {
                    pl.skipTo(playlist.getCurrentPlayable());
                    pl.getCurrentPlayable().prepare(mp);
                    for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                        controlListener.onPlayableChanged(getCurrentPlayable());
                    }
                }
            }
            return old;
        }

        public boolean switchPrevious() {
            if (mp.getCurrentPosition() > 3000) {
                mp.seekTo(0);
                return false;
            } else {
                if (queueSize > 0) queueSize++;
                if (pl == null || pl.isEmpty()) return false;
                pl.switchPrevious().prepare(mp);
                if(isPlaying()) play();
                for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(getCurrentPlayable());
                }
                return true;
            }
        }

        public boolean switchNext() {
            if (queueSize > 0) queueSize--;
            if (pl == null || pl.isEmpty()) return false;
            pl.switchNext().prepare(mp);
            if(isPlaying()) play();
            for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                controlListener.onPlayableChanged(getCurrentPlayable());
            }
            return true;
        }

        public void toggleRepeat() {
            repeatState = repeatState.next();
            switch (repeatState) {
                case NONE:
                case ALL:
                    mp.setLooping(false);
                    break;
                case ONE:
                    mp.setLooping(true);
                    break;
            }

            for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                controlListener.onRepeatStateChanged(repeatState);
            }
        }

        public RepeatState getRepeatState() {
            return repeatState;
        }

        public boolean setShuffle(boolean shuffle) {
            if (!pl.isEmpty()) {
                boolean old = pl.doShuffle();
                if (old != shuffle) queueSize = 0;
                pl.setShuffle(shuffle);
                for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                    controlListener.onShuffleStateChanged(shuffle);
                }
                return old;
            }
            return false;
        }

        public boolean doShuffle() {
            return pl.doShuffle();
        }


        public Playable getCurrentPlayable() {
            if (!pl.isEmpty()) {
                return pl.getCurrentPlayable();
            }
            return null;
        }

        public void setProgress(int progress) {
            getCurrentPlayable().skipTo(mp, progress);
        }

        public int getProgress() {
            return mp.getCurrentPosition();
        }

        public boolean isEmpty() {
            return pl == null || pl.isEmpty();
        }

        public boolean isPlaying() {
            return playing;
        }
    }
}
