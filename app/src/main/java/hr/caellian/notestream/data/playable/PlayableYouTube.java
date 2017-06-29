package hr.caellian.notestream.data.playable;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

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
import hr.caellian.notestream.data.youtube.YouTubeFetcher;
import hr.caellian.notestream.data.youtube.YouTubeFile;

/**
 * Created by caellyan on 22/06/17.
 */

public class PlayableYouTube extends PlayableDownloadable {
    public static final String TAG = PlayableYouTube.class.getSimpleName();

    private static final String LOCATION = NoteStream.getInstance().getString(R.string.location_youtube);
    private static final String ID_PREFIX = "playable-youtube-";

    private static final HashMap<String, String> youtubeURLMap = new HashMap<>();

    protected final String id;
    protected String youtubeID;
    protected String extension;
    protected transient MutableMediaMetadata metadata;

    public PlayableYouTube(final String youtubeID) {
        this.id = getId(youtubeID);
        this.youtubeID = youtubeID;

        new YouTubeExtractor(youtubeID) {
            @Override
            public void onPostExecute(SparseArray<YouTubeFile> files, VideoMeta videoMeta) {
                if (files != null) {
                    int itag = 140;
                    youtubeURLMap.put(youtubeID, files.get(itag).getUrl());
                    extension = files.get(itag).getFormat().getExt();
                    final MutableMediaMetadata metadata = getMetadata();
                    metadata.setTitle(videoMeta.getTitle());
                    metadata.setAuthor(videoMeta.getAuthor());
                    metadata.setLength((int) videoMeta.getVideoLength() * 1000);
                    metadata.setEnd(metadata.getLength());

                    new AsyncTask<VideoMeta, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(VideoMeta... videoMeta) {
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
                                Log.w(TAG,"Failed while reading bytes from " + url.toExternalForm(), e);
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

                            metadata.setCover(baos.toByteArray());
                            return true;
                        }
                    }.execute(videoMeta);
                    setAvailable(true);
                }
            }
        }.execute();
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
        return PlayableSource.YOUTUBE;
    }

    @Override
    public String getID() {
        return this.id;
    }

    public String getPath() {
        return youtubeID;
    }

    @Override
    public String getLocation() {
        return LOCATION;
    }

    @Override
    public boolean prepare(final MediaPlayer mp) {
        mp.reset();
        try {
            if (youtubeURLMap.get(youtubeID) != null) {
                mp.setDataSource(youtubeURLMap.get(youtubeID));
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
        mp.seekTo(Math.max(0, Math.min(ms, getMetadata().getLength())));
        return true;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public ArrayList<PlayableYouTube> getSuggestions() {
        ArrayList<PlayableYouTube> result = new ArrayList<>();

        for (String id : YouTubeFetcher.getSuggestionsFor(youtubeID)) {
            result.add(new PlayableYouTube(id));
        }

        return result;
    }

    @Override
    public boolean download() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int writeExternal = NoteStream.getInstance().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writeExternal != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(NoteStream.getInstance(), "Write External Storage permission not granted!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        Uri uri = Uri.parse(youtubeURLMap.get(youtubeID));
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(getMetadata().getTitle());

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getMetadata().getTitle() + "." + extension);

        DownloadManager manager = (DownloadManager) NoteStream.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        return true;
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

        return getPath().equals(other.getPath());
    }
}
