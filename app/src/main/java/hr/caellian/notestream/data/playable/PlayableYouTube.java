package hr.caellian.notestream.data.playable;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.MutableMediaMetadata;
import hr.caellian.notestream.data.youtube.VideoMeta;
import hr.caellian.notestream.data.youtube.YouTubeExtractor;
import hr.caellian.notestream.data.youtube.YouTubeFile;

/**
 * Created by caellyan on 22/06/17.
 */

public class PlayableYouTube extends PlayableDownloadable {
    public static final String TAG = PlayableYouTube.class.getSimpleName();

    private static final String LOCATION = NoteStream.getInstance().getString(R.string.location_youtube);
    private static final String ID_PREFIX = "playable-local-";

    private static final HashMap<String, String> yturls = new HashMap<>();

    protected final String id;
    protected String youtubeID;
    protected String downloadURL;
    protected String extension;
    protected boolean valid = false;
    protected transient MutableMediaMetadata metadata;

    public PlayableYouTube(final String youtubeID) {
        this.id = getId(youtubeID);
        this.youtubeID = youtubeID;

        String youtubeLink = "http://youtube.com/watch?v=" + youtubeID;

        new YouTubeExtractor(NoteStream.getInstance()) {
            @Override
            public void onExtractionComplete(SparseArray<YouTubeFile> ytFiles, VideoMeta videoMeta) {
                Log.d(TAG, "Extraction complete!");
                if (ytFiles != null) {
                    Log.d(TAG, "ytf != null");
                    int itag = 140;
                    downloadURL = ytFiles.get(itag).getUrl();
                    yturls.put(youtubeID, downloadURL);
                    extension = ytFiles.get(itag).getFormat().getExt();
                    Log.d(TAG, "urlset: " + downloadURL);
                    final MutableMediaMetadata metadata = getMetadata();
                    metadata.setTitle(videoMeta.getTitle());
                    metadata.setAuthor(videoMeta.getAuthor());
                    metadata.setLength((int) videoMeta.getVideoLength() * 1000);
                    new AsyncTask<VideoMeta, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(VideoMeta... videoMeta) {

                            Log.d(TAG, "setcoverstart");
                            VideoMeta meta = videoMeta[0];
                            URL url;
                            try {
                                url = new URL(meta.getMaxResImageUrl());
                            } catch (MalformedURLException e) {
                                return false;
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            InputStream is = null;
                            try {
                                is = url.openStream();
                                byte[] byteChunk = new byte[4096];
                                int n;

                                while ( (n = is.read(byteChunk)) > 0 ) {
                                    baos.write(byteChunk, 0, n);
                                }
                            } catch (IOException e) {
                                System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
                                return false;
                            } finally {
                                if (is != null) {
                                    try {
                                        is.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            metadata.setCoverData(baos.toByteArray());

                            Log.d(TAG, "setcoverend");
                            return true;
                        }
                    }.execute(videoMeta);
                }
            }
        }.extract(youtubeLink, true, true);
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

    public String getYouTubeID() {
        return youtubeID;
    }

    @Override
    public String getLocation() {
        return LOCATION;
    }

    @Override
    public boolean prepare(final MediaPlayer mp) {
        try {
            Log.d(TAG, "prepare: " + downloadURL);
            Log.d(TAG, "other: " + yturls.get(youtubeID));
            if (downloadURL != null) {
                mp.setDataSource(yturls.get(youtubeID));
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.prepare();
                mp.seekTo(getMetadata().getStart());
            }
        } catch (IOException e) {
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

    public ArrayList<PlayableYouTube> getSuggestions() {
        ArrayList<PlayableYouTube> result = new ArrayList<>();
        // TODO: Implement method
        return result;
    }

    @Override
    public boolean download() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int writeExternal = NoteStream.getInstance().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        } else {
            downloadFunction();
        }
        Uri uri = Uri.parse(yturls.get(youtubeID));
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(getMetadata().getTitle());

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getMetadata().getTitle() + "." + extension);

        DownloadManager manager = (DownloadManager) NoteStream.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        return true;
    }

    private void downloadFunction() {

    }

    public static String getId(String youtubeID) {
        return ID_PREFIX + youtubeID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof PlayableYouTube)) return false;
        PlayableYouTube other = (PlayableYouTube) obj;

        return getYouTubeID().equals(other.getYouTubeID());
    }
}
