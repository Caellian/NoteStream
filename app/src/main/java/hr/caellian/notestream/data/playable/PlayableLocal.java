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
            return metadata = new MutableMediaMetadata(NoteStream.getInstance(), getPlayableId().substring(getPlayableId().indexOf("-") + 1));
        }
    }

    @Override
    public String getPlayableId() {
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
            } catch (IllegalArgumentException | IOException e) {
                Log.w(TAG, "prepare: 'setDataSource' or 'prepare' failed!", e);
                return false;
            }
        return true;
    }

    @Override
    public boolean skipTo(MediaPlayer mp, int ms) {
        if (ms >= 0 && getMetadata().getLength() < ms){
            mp.seekTo(ms);
            return true;
        }
        return false;
    }

    public static String getId(String path) {
        return ID_PREFIX + path.substring(path.lastIndexOf("/"), path.contains(".") ? path.lastIndexOf(".") : path.length() - 1).replaceAll("[^A-Za-z0-9]+","");
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
