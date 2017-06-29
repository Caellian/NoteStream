package hr.caellian.notestream.data.playable;

import hr.caellian.notestream.NoteStream;

/**
 * Created by tinsv on 29/06/2017.
 */

public abstract class PlayableRemote implements Playable {
    protected boolean available = false;

    protected void setAvailable(boolean available) {
        this.available = available;
        for (AvailabilityListener availabilityListener : NoteStream.AVAILABILITY_LISTENERS) {
            availabilityListener.onAvailableStateChanged(this, available);
        }
    }

    public abstract boolean isAvailable();

    public interface AvailabilityListener {
        void onAvailableStateChanged(PlayableRemote playable, boolean state);
    }
}
