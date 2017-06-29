package hr.caellian.notestream.data.playable;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.MutableMediaMetadata;

/**
 * Created by caellyan on 16/06/17.
 */

public class PlayableLocal implements Playable {
    public static final String TAG = PlayableLocal.class.getSimpleName();

    private static final String LOCATION = NoteStream.getInstance().getString(R.string.location_local);
    private static final String ID_PREFIX = "playable-local-";

    protected String id;
    protected String path;
    protected transient MutableMediaMetadata metadata;

    public PlayableLocal(String path) {
        this.id = getId(path);
        this.path = path;
    }

    @Override
    public MutableMediaMetadata getMetadata() {
        if (metadata != null) {
            return metadata;
        } else {
            return metadata = new MutableMediaMetadata(this);
        }
    }

    @Override
    public PlayableSource getPlayableSource() {
        return PlayableSource.LOCAL;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String getLocation() {
        return LOCATION;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean prepare(MediaPlayer mp) {
            mp.reset();
            try {
                mp.setDataSource(getPath());
                mp.prepare();
                mp.seekTo(this.getMetadata().getStart());
            } catch (IllegalStateException | IllegalArgumentException | IOException e) {
                Log.w(TAG, "prepare: 'setDataSource' or 'prepare' failed!", e);
                return false;
            }
        return true;
    }

    @Override
    public boolean skipTo(MediaPlayer mp, int ms) {
        mp.seekTo(Math.max(0, Math.min(ms, getMetadata().getLength())));
        return true;
    }

    public static String getId(String path) {
        return ID_PREFIX + path.replaceAll("[^A-Za-z0-9]+","");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof PlayableLocal)) return false;
        PlayableLocal other = (PlayableLocal) obj;

        return getPath().equals(other.getPath());
    }
}
