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
import android.support.annotation.Nullable;

import java.util.Arrays;
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

    private static Timer progressTimer = new Timer();

    int queueSize = 0;
    static Playlist pl = new Playlist("currentlyPlayed").clear();
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

        // THIS
//        MediaSessionCompat msc = new MediaSessionCompat(this, Constants.MEDIA_SESSION_TAG);
//        MediaControllerCompat mcc = new MediaControllerCompat(this, msc);
//        MediaControllerCompat.setMediaController(null, mcc);

        // OR THIS OR WHATEVER
//        ms = new MediaSession(this, Constants.MEDIA_SESSION_TAG);
//
//        Intent psbrIntent = new Intent(this, PlayerServiceBroadcastReceiver.class);
//        PendingIntent psbrPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), Constants.APP_PSBR_CODE, psbrIntent, 0);
//        ms.setMediaButtonReceiver(psbrPendingIntent);
//        ms.setFlags(FLAG_HANDLES_MEDIA_BUTTONS);
//
//        ms.setActive(true);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
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

//    public class TEST extends MediaButtonReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//            if (event.getAction() != KeyEvent.ACTION_DOWN) return;
//
//            switch (event.getKeyCode()) {
//                case KeyEvent.KEYCODE_MEDIA_STOP:
//                    psb.stop();
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_PLAY:
//                    psb.play();
//                    break;
//                case KeyEvent.KEYCODE_HEADSETHOOK:
//                case KeyEvent.KEYCODE_MEDIA_PAUSE:
//                    psb.pause();
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
//                    if (playing) {
//                        psb.pause();
//                    } else {
//                        psb.play();
//                    }
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_NEXT:
//                    psb.switchNext();
//                    break;
//                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                    psb.switchPrevious();
//                    break;
//            }
//        }
//    }

    public class PlayerServiceBinder extends Binder {

        public void shufflePlay(Playlist playlist) {
            if (playlist != null && !playlist.isEmpty()) {
                mp.stop();
                pl.clear();
                pl.add(playlist);
                pl.skipTo(playlist.getCurrentPlayable());
                pl.setShuffle(true);
                pl.getCurrentPlayable().prepare(mp);

                for (Playable.ControlListener controlListener : NoteStream.CONTROL_LISTENERS) {
                    controlListener.onPlayableChanged(getCurrentPlayable());
                }
                play();
            }
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
            return playPlaylist(new Playlist("temporary-" + playable.getMetadata().trackId, Arrays.asList(playable)));
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


        public int getCurrentPosition() {
            return mp.getCurrentPosition();
        }

        public void setProgress(int progress) {
            getCurrentPlayable().skipTo(mp, progress);
        }

        public int getProgress() {
            return mp.getCurrentPosition();
        }

        public boolean isEmpty() {
            return pl == null;
        }

        public boolean isPlaying() {
            return playing;
        }
    }
}
