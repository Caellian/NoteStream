package hr.caellian.notestream.data.playable;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;

/**
 * Created by caellyan on 24/06/17.
 */

public abstract class PlayableDownloadable implements Playable {
    public abstract boolean download();

    public interface DownloadedListener {
        void onDownloaded();
    }
}
