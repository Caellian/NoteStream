package hr.caellian.notestream.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import hr.caellian.id3lib.MP3File;
import hr.caellian.id3lib.TagException;
import hr.caellian.id3lib.id3.AbstractID3v2;
import hr.caellian.id3lib.id3.AbstractID3v2Frame;
import hr.caellian.id3lib.id3.FrameBodyPIC;
import hr.caellian.id3lib.id3.ID3v2_4;
import hr.caellian.id3lib.id3.ID3v2_4Frame;
import hr.caellian.id3lib.lyrics3.AbstractLyrics3;
import hr.caellian.id3lib.lyrics3.Lyrics3v2;
import hr.caellian.notestream.R;
import io.paperdb.Book;
import io.paperdb.Paper;

/**
 * Created by caellyan on 24/06/17.
 */

public class MutableMediaMetadata {
    public static final String TITLE = "media.title";
    public static final String AUTHOR = "media.author";
    public static final String ALBUM = "media.album";
    public static final String YEAR = "media.year";
    public static final String TRACK = "media.track";
    public static final String GENRE = "media.genre";
    public static final String LYRICS = "media.lyrics";

    public static final String START = "media.start";
    public static final String END = "media.end";
    public static final String LENGTH = "media.length";

    public static final String COVER_DATA = "media.coverData";

    public static final String IMPORTED = "media.imported";

    public final String trackId;
    protected final Book book;

    protected Bitmap trackCover;

    private final Context context;

    public MutableMediaMetadata(Context context, String trackId) {
        this.trackId = trackId;
        this.context = context;
        book = Paper.book("track-" + trackId);
        trackCover = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_song);
    }

    public void setFromSource(String sourceLocation) {
        if (!book.read(IMPORTED, false)) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(sourceLocation);

            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            if (author == null) {
                author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            }
            if (title == null) {
                title = sourceLocation.substring(sourceLocation.lastIndexOf("/") + 1, sourceLocation.contains(".") ? sourceLocation.lastIndexOf(".") : sourceLocation.length() - 1);

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
            setYear(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
            setTrack(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
            setGenre(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            setLength(Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));

            try {
                MP3File file = new MP3File(new File(sourceLocation));
                AbstractLyrics3 lyrics3Tag = file.getLyrics3Tag();
                if (lyrics3Tag != null) setLyrics(lyrics3Tag.getSongLyric());
            } catch (IOException | TagException e) {
                e.printStackTrace();
            }

            byte[] data = null;
            // TODO: Fix cover image fetching!
//            try {
//                MP3File file = new MP3File(sourceLocation);
//                List<AbstractID3v2Frame> frames = file.getFrameAcrossTags("PIC");
//                file.getID3v2Tag();
//                ID3v2_4 tag = new ID3v2_4(new RandomAccessFile(sourceLocation, "r"));
//                FrameBodyPIC fb = ((FrameBodyPIC) tag.getFrame("PIC").getBody());
//
//                data = (byte[]) fb.getObject("Picture Data");
//            } catch (IOException | TagException e) {
//                e.printStackTrace();
//            }

            data = mmr.getEmbeddedPicture();
            setCoverData(data);

            if (data != null) {
                // This is an alternative solution:
//                InputStream is = new ByteArrayInputStream(data);
//                trackCover = BitmapFactory.decodeStream(is);
                trackCover = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
            if (trackCover == null){
                trackCover = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_song);
            }

            mmr.release();

            book.write(IMPORTED, true);
        }
    }

    public void applyTo(String fileLocation) {
        try {
            RandomAccessFile raf = new RandomAccessFile(fileLocation, "rw");
            MP3File file = new MP3File(fileLocation);
            // Try to protect existing data our program doesn't understand:
            AbstractID3v2 sourceTag = file.getID3v2Tag() != null ? file.getID3v2Tag() : new ID3v2_4();

            Lyrics3v2 tag = new Lyrics3v2();
            tag.setSongTitle(getTitle());
            tag.setLeadArtist(getAuthor());
            tag.setAlbumTitle(getAlbum());
            tag.setYearReleased(getYear());
            tag.setTrackNumberOnAlbum(getTrack());
            tag.setSongGenre(getGenre());
            tag.setSongLyric(getLyrics());

            final byte textEncoding = 3;
            final String imageFormat = "png";
            final byte pictureType = 3;
            final String description = "Cover image";
            final byte[] data = getCoverData();
            FrameBodyPIC pictureFrameBody = new FrameBodyPIC(textEncoding, imageFormat, pictureType, description, data);
            sourceTag.setFrame(new ID3v2_4Frame(pictureFrameBody));

            sourceTag.append(tag);
            sourceTag.write(raf);
        } catch (IOException | TagException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return book.read(TITLE, "");
    }

    public void setTitle(String newTitle) {
        if (newTitle != null) book.write(TITLE, newTitle);
    }

    public String getAuthor() {
        return book.read(AUTHOR, "");
    }

    public void setAuthor(String newAuthor) {
        if (newAuthor != null) book.write(AUTHOR, newAuthor);
    }

    public String getAlbum() {
        return book.read(ALBUM, "");
    }

    public void setAlbum(String newAlbum) {
        if (newAlbum != null) book.write(ALBUM, newAlbum);
    }

    public String getYear() {
        return book.read(YEAR, "");
    }

    public void setYear(String newYear) {
        if (newYear != null) book.write(YEAR, newYear);
    }

    public String getTrack() {
        return book.read(TRACK, "");
    }

    public void setTrack(String newTrack) {
        if (newTrack != null) book.write(TRACK, newTrack);
    }

    public String getGenre() {
        return book.read(GENRE, "");
    }

    public void setGenre(String newGenre) {
        if (newGenre != null) book.write(GENRE, newGenre);
    }

    public String getLyrics() {
        return book.read(LYRICS, "");
    }

    public void setLyrics(String newLyrics) {
        if (newLyrics != null) book.write(LYRICS, newLyrics);
    }

    public int getStart() {
        return book.read(START, 0);
    }

    public void setStart(int newStart) {
        book.write(START, newStart);
    }

    public int getEnd() {
        return book.read(END, getLength());
    }

    public void setEnd(int newEnd) {
        book.write(END, newEnd);
    }

    public int getLength() {
        return book.read(LENGTH, 0);
    }

    public void setLength(int newLength) {
        book.write(LENGTH, newLength);
    }

    public byte[] getCoverData() {
        return book.read(COVER_DATA, "").getBytes();
    }

    public void setCoverData(byte[] newCoverData) {
        if (newCoverData != null) book.write(COVER_DATA, new String(newCoverData));
    }

    public Bitmap getTrackCover() {
        return trackCover;
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

        return trackId.equals(other.trackId);
    }

    @Override
    public int hashCode() {
        return trackId.hashCode();
    }
}
