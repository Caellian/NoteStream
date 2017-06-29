package hr.caellian.notestream.data;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;

import hr.caellian.id3lib.MP3File;
import hr.caellian.id3lib.TagException;
import hr.caellian.id3lib.id3.AbstractID3v2;
import hr.caellian.id3lib.id3.FrameBodyPIC;
import hr.caellian.id3lib.id3.ID3v2_4;
import hr.caellian.id3lib.id3.ID3v2_4Frame;
import hr.caellian.id3lib.lyrics3.AbstractLyrics3;
import hr.caellian.id3lib.lyrics3.Lyrics3v2;
import hr.caellian.notestream.NoteStream;
import hr.caellian.notestream.R;
import hr.caellian.notestream.data.playable.Playable;
import hr.caellian.notestream.data.playable.PlayableLocal;
import hr.caellian.notestream.data.playable.PlayableSource;
import hr.caellian.notestream.database.PlaylistOpenHelper;
import hr.caellian.notestream.lib.Constants;
import hr.caellian.notestream.util.Util;

/**
 * Created by caellyan on 24/06/17.
 */

public class MutableMediaMetadata extends HashMap<String, Object> {
    private static final String TAG = MutableMediaMetadata.class.getSimpleName();

    // These should only be used internally by database management system:
    public static final String ID = "TRACK_ID";
    public static final String SOURCE = "TRACK_SOURCE";
    public static final String PATH = "TRACK_PATH";

    // ID3v2/Lyrics3 supported tags:
    public static final String TITLE = "TITLE";
    public static final String AUTHOR = "AUTHOR";
    public static final String ALBUM = "ALBUM";
    public static final String YEAR = "YEAR";
    public static final String TRACK = "TRACK";
    public static final String GENRE = "GENRE";
    public static final String LYRICS = "LYRICS";

    // Internal data not supported by any current tag containers:
    public static final String START = "START";
    public static final String END = "END";
    public static final String LENGTH = "LENGTH";

    // Cover image data not directly supported by tag containers:
    public static final String COVER_DATA = "COVER_DATA";

    protected Bitmap trackCover;

    protected transient Playable parent;

    public static final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
    static {
        properties.put(ID, Constants.SQL_TYPE_TEXT);
        properties.put(SOURCE, Constants.SQL_TYPE_TEXT);
        properties.put(PATH, Constants.SQL_TYPE_TEXT);

        properties.put(TITLE, Constants.SQL_TYPE_TEXT);
        properties.put(AUTHOR, Constants.SQL_TYPE_TEXT);
        properties.put(ALBUM, Constants.SQL_TYPE_TEXT);
        properties.put(YEAR, Constants.SQL_TYPE_INTEGER);
        properties.put(TRACK, Constants.SQL_TYPE_INTEGER);
        properties.put(GENRE, Constants.SQL_TYPE_TEXT);
        properties.put(LYRICS, Constants.SQL_TYPE_TEXT);

        properties.put(START, Constants.SQL_TYPE_INTEGER);
        properties.put(END, Constants.SQL_TYPE_INTEGER);
        properties.put(LENGTH, Constants.SQL_TYPE_INTEGER);
        properties.put(COVER_DATA, Constants.SQL_TYPE_BLOB);
    }

    public MutableMediaMetadata(Playable playable) {
        super();
        put(ID, playable.getID());
        put(SOURCE, playable.getPlayableSource().getID());
        put(PATH, playable.getPath());
        trackCover = Util.drawableToBitmap(R.drawable.ic_song, 512, 512);
        this.parent = playable;
    }

    public void setFromDatabase(PlaylistOpenHelper poh, Cursor c) {
        HashMap<String, Integer> columnTable = poh.getTableColumns();

        if (parent instanceof PlayableLocal) {
            setFromSource(parent.getPath());
        }

        for (Entry<String, Integer> entry : columnTable.entrySet()) {
            if (!properties.containsKey(entry.getKey())) continue;
            switch (properties.get(entry.getKey())) {
                case Constants.SQL_TYPE_INTEGER:
                    put(entry.getKey(), c.getInt(entry.getValue()));
                    break;
                case Constants.SQL_TYPE_REAL:
                    put(entry.getKey(), c.getFloat(entry.getValue()));
                    break;
                case Constants.SQL_TYPE_TEXT:
                    put(entry.getKey(), c.getString(entry.getValue()));
                    break;
                case Constants.SQL_TYPE_BLOB:
                    put(entry.getKey(), c.getBlob(entry.getValue()));
                    break;
                default:
                    Log.i(TAG, "setFromDatabase: unused database entry: " + entry.getKey());
            }
        }
    }

    public void setFromSource(String sourceLocation) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(sourceLocation);

            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            if (author == null) {
                author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            }
            if (title == null) {
                title = sourceLocation.substring(sourceLocation.lastIndexOf("/") + 1, sourceLocation.contains(".") ? sourceLocation.lastIndexOf(".") : sourceLocation.length() - 1);

                // TODO: This is a bit unpredictable and can be wrong (rarely, but can).
                if (author == null && title.contains("-")) {
                    author = title.substring(0, title.lastIndexOf("-")).trim();
                    title = title.substring(title.lastIndexOf("-") + 1).trim();
                }
            }
            if (author == null) {
                author = "Unknown";
            }

            setTitle(title);
            setAuthor(author);
            setAlbum(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            String year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            if (year != null) setYear(Integer.parseInt(year.split("[^0-9]")[0]));
            String track = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
            if (track != null) setTrack(Integer.parseInt(track.split("[^0-9]")[0]));
            setGenre(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            setLength(Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
            setEnd(getLength());

            try {
                // TODO: Fix access permission for RandomAccessFile - replace it?
                MP3File file = new MP3File(sourceLocation);
                AbstractLyrics3 lyrics3Tag = file.getLyrics3Tag();
                if (lyrics3Tag != null) setLyrics(lyrics3Tag.getSongLyric());
            } catch (IOException | TagException e) {
                Log.w(TAG, "Unable to set lyrics!", e);
            } catch (UnsupportedOperationException e) {
                Log.w(TAG, "UnsupportedOperationException: ", e);
            }

            setCover(mmr.getEmbeddedPicture());
            mmr.release();
        } catch (IllegalArgumentException e) {
            mmr.release();
        }
    }

    public void applyTo(String fileLocation) {
        try {
            RandomAccessFile raf = new RandomAccessFile(fileLocation, "rw");
            MP3File file = new MP3File(fileLocation);
            // Try to protect any existing data our program doesn't understand:
            AbstractID3v2 sourceTag = file.getID3v2Tag() != null ? file.getID3v2Tag() : new ID3v2_4();

            Lyrics3v2 tag = new Lyrics3v2();
            tag.setSongTitle(getTitle());
            tag.setLeadArtist(getAuthor());
            tag.setAlbumTitle(getAlbum());
            tag.setYearReleased(String.valueOf(getYear()));
            tag.setTrackNumberOnAlbum(String.valueOf(getTrack()));
            tag.setSongGenre(getGenre());
            tag.setSongLyric(getLyrics());

            final byte textEncoding = 3;
            final String imageFormat = "png";
            final byte pictureType = 3;
            final String description = "Cover image";

            Bitmap cover = getCover();
            int size = cover.getByteCount();
            ByteBuffer buffer = ByteBuffer.allocate(size);
            cover.copyPixelsToBuffer(buffer);
            final byte[] data = buffer.array();

            FrameBodyPIC pictureFrameBody = new FrameBodyPIC(textEncoding, imageFormat, pictureType, description, data);
            sourceTag.setFrame(new ID3v2_4Frame(pictureFrameBody));

            sourceTag.append(tag);
            sourceTag.write(raf);
        } catch (IOException | TagException e) {
            Log.w(TAG, "Unable to store metadata to file!", e);
        }
    }

    public Playable getParent() {
        if (parent != null) {
            return parent;
        } else {
            try {
                //noinspection ConstantConditions
                return parent = PlayableSource.getByID((String) get(SOURCE)).construct((String) get(PATH));
            } catch (Exception e) {
                Log.w(TAG, "Couldn't reconstruct parent! Returning null...", e);
                return null;
            }
        }
    }

    public String getTitle() {
        return (String) (get(TITLE) != null ? get(TITLE) : "");
    }

    public void setTitle(String newTitle) {
        if (newTitle != null) put(TITLE, newTitle);
    }

    public String getAuthor() {
        return (String) (get(AUTHOR) != null ? get(AUTHOR) : "");
    }

    public void setAuthor(String newAuthor) {
        String old = get(AUTHOR) != null ? ((String) get(AUTHOR)).toLowerCase() : null;
        Library library = NoteStream.getInstance().library;
        if (library != null) {
            if (old != null && library.artists.containsKey(old)) {
                library.artists.get(old).remove(getParent());
            }
            if (!library.artists.containsKey(newAuthor)) {
                library.artists.put(newAuthor, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.ARTIST_PREFIX + newAuthor));
            }
            library.artists.get(newAuthor).add(getParent());
        }
        if (newAuthor != null) put(AUTHOR, newAuthor);
    }

    public String getAlbum() {
        return (String) (get(ALBUM) != null ? get(ALBUM) : "Unknown");
    }

    public void setAlbum(String newAlbum) {
        String old = get(ALBUM) != null ? ((String) get(ALBUM)).toLowerCase() : null;
        Library library = NoteStream.getInstance().library;
        if (library != null) {
            if (old != null && library.albums.containsKey(old)) {
                library.albums.get(old).remove(getParent());
            }
            if (!library.albums.containsKey(newAlbum)) {
                library.albums.put(newAlbum, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.ALBUM_PREFIX + newAlbum));
            }
            library.albums.get(newAlbum).add(getParent());
        }
        if (newAlbum != null) put(ALBUM, newAlbum);
    }

    public int getYear() {
        return (int) (get(YEAR) != null ? get(YEAR) : 0);
    }

    public void setYear(int newYear) {
        put(YEAR, newYear);
    }

    public int getTrack() {
        return (int) (get(TRACK) != null ? get(TRACK) : 0);
    }

    public void setTrack(int newTrack) {
        put(TRACK, newTrack);
    }

    public String getGenre() {
        return (String) (get(GENRE) != null ? get(GENRE) : "");
    }

    public void setGenre(String newGenre) {
        String old = get(GENRE) != null ? ((String) get(GENRE)).toLowerCase() : null;
        Library library = NoteStream.getInstance().library;
        if (library != null) {
            if (old != null && library.genres.containsKey(old)) {
                library.genres.get(old).remove(getParent());
            }
            if (!library.genres.containsKey(newGenre)) {
                library.genres.put(newGenre, Playlist.get(Playlist.TEMPORARY_PREFIX + Playlist.GENRE_PREFIX + newGenre));
            }
            library.genres.get(newGenre).add(getParent());
        }
        if (newGenre != null) put(GENRE, newGenre);
    }

    public String getLyrics() {
        return (String) (get(LYRICS) != null ? get(LYRICS) : "No lyrics available.");
    }

    public void setLyrics(String newLyrics) {
        if (newLyrics != null) put(LYRICS, newLyrics);
    }

    public int getStart() {
        return (int) (get(START) != null ? get(START) : 0);
    }

    public void setStart(int newStart) {
        put(START, newStart);
    }

    public int getEnd() {
        return (int) (get(END) != null ? get(END) : getLength());
    }

    public void setEnd(int newEnd) {
        put(END, newEnd);
    }

    public int getLength() {
        return (int) (get(LENGTH) != null ? get(LENGTH) : 0);
    }

    public void setLength(int newLength) {
        put(LENGTH, newLength);
    }

    public Bitmap getCover() {
        if (trackCover == null) {
            if (parent instanceof PlayableLocal) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource((String) get(PATH));
                setCover(mmr.getEmbeddedPicture());
            } else {
                setCover((Bitmap) null);
            }
        }
        return trackCover;
    }

    public void setCover(byte[] data) {
        if (data != null && data.length != 0) {
            put(COVER_DATA, data);
            setCover(BitmapFactory.decodeByteArray(data, 0, data.length));
        } else {
            setCover((Bitmap) null);
        }
    }

    public void setCover(Bitmap newCover) {
        if (newCover != null) {
            trackCover = newCover;
        } else if (trackCover == null) {
            trackCover = Util.drawableToBitmap(R.drawable.ic_song, 512, 512);
        }
    }

    @Override
    public Object put(String key, Object value) {
        return super.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != MutableMediaMetadata.class) {
            return false;
        }

        MutableMediaMetadata other = (MutableMediaMetadata) o;

        return get(ID).equals((other.get(ID)));
    }

    @Override
    public int hashCode() {
        return get(ID).hashCode();
    }

    public interface DataChangeListener {
        void onDataChanged(String trackID, String key, Object newData);
    }
}
