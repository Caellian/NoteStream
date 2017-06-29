package hr.caellian.notestream.data.playable;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import java.io.Serializable;

import hr.caellian.notestream.data.MutableMediaMetadata;
import hr.caellian.notestream.util.RepeatState;

/**
 * Created by caellyan on 16/04/17.
 */

public interface Playable extends Serializable {
    MutableMediaMetadata getMetadata();

    String getID();

    PlayableSource getPlayableSource();

    String getPath();

    String getLocation();

    boolean prepare(MediaPlayer mp);

    boolean skipTo(MediaPlayer mp, int ms);

    interface ProgressListener {
        /**
         * Called every second while a playable is being played.
         * @param progress current progress in milliseconds.
         */
        void onProgressChanged(int progress);
    }

    interface ControlListener {
        /**
         * Called when a new playable has been selected.
         * @param current currently selected playable.
         */
        void onPlayableChanged(@NonNull Playable current);

        /**
         * Called on play/pause button click.
         * @param playing {@code true} if playable has been started, {@code false} if it has been paused/stopped.
         */
        void onPlayStatusChanged(boolean playing);

        /**
         * Called on shuffle state change.
         * @param currentState current shuffle state - {@code true} if shuffle is turned on, {@code false} otherwise.
         */
        void onShuffleStateChanged(boolean currentState);

        /**
         * Called on repeat state change.
         * @param currentState current repeat state.
         */
        void onRepeatStateChanged(@NonNull RepeatState currentState);
    }
}
