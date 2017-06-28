package hr.caellian.id3lib.object;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * ID3v2 and Lyrics3v2 tags have individual fields <code>AbstractMP3Fragment</code>s Then each fragment is broken down
 * in to individual <code>AbstractMP3Object</code>s
 *
 * @author Eric Farng
 * @version $Revision: 1.5 $
 */
public class ObjectLyrics3Line extends AbstractMP3Object {

    private LinkedList timeStamp = new LinkedList();
    private String lyric = "";

    /**
     * Creates a new ObjectLyrics3Line object.
     */
    public ObjectLyrics3Line(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Creates a new ObjectLyrics3Line object.
     */
    public ObjectLyrics3Line(final ObjectLyrics3Line copyObject) {
        super(copyObject);
        this.lyric = copyObject.lyric;
        ObjectLyrics3TimeStamp newTimeStamp;
        for (int i = 0; i < copyObject.timeStamp.size(); i++) {
            newTimeStamp = new ObjectLyrics3TimeStamp((ObjectLyrics3TimeStamp) copyObject.timeStamp.get(i));
            this.timeStamp.add(newTimeStamp);
        }
    }

    public void setLyric(final String lyric) {
        this.lyric = lyric;
    }

    public void setLyric(final ObjectID3v2LyricLine line) {
        this.lyric = line.getText();
    }

    public String getLyric() {
        return this.lyric;
    }

    public int getSize() {
        int size = 0;
        for (int i = 0; i < this.timeStamp.size(); i++) {
            size += ((ObjectLyrics3TimeStamp) this.timeStamp.get(i)).getSize();
        }
        return size + this.lyric.length();
    }

    public void setTimeStamp(final ObjectLyrics3TimeStamp time) {
        this.timeStamp.clear();
        this.timeStamp.add(time);
    }

    public Iterator getTimeStamp() {
        return this.timeStamp.iterator();
    }

    public void addLyric(final String newLyric) {
        this.lyric += newLyric;
    }

    public void addLyric(final ObjectID3v2LyricLine line) {
        this.lyric += line.getText();
    }

    public void addTimeStamp(final ObjectLyrics3TimeStamp time) {
        this.timeStamp.add(time);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ObjectLyrics3Line)) {
            return false;
        }
        final ObjectLyrics3Line objectLyrics3Line = (ObjectLyrics3Line) obj;
        return this.lyric.equals(objectLyrics3Line.lyric) &&
                this.timeStamp.equals(objectLyrics3Line.timeStamp) && super.equals(obj);
    }

    public boolean hasTimeStamp() {
        return !this.timeStamp.isEmpty();
    }

    public void readString(final String lineString, int offset) {
        if (lineString == null) {
            throw new NullPointerException("Image is null");
        }
        if ((offset < 0) || (offset >= lineString.length())) {
            throw new IndexOutOfBoundsException("Offset to line is out of bounds: offset = " +
                                                offset +
                                                ", line.length()" +
                                                lineString.length());
        }
        int delim;
        ObjectLyrics3TimeStamp time;
        this.timeStamp = new LinkedList();
        delim = lineString.indexOf("[", offset);
        while (delim >= 0) {
            offset = lineString.indexOf("]", delim) + 1;
            time = new ObjectLyrics3TimeStamp("Time Stamp");
            time.readString(lineString.substring(delim, offset));
            this.timeStamp.add(time);
            delim = lineString.indexOf("[", offset);
        }
        this.lyric = lineString.substring(offset);
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < this.timeStamp.size(); i++) {
            str += this.timeStamp.get(i).toString();
        }
        return "timeStamp = " + str + ", lyric = " + this.lyric + "\n";
    }

    public String writeString() {
        String str = "";
        ObjectLyrics3TimeStamp time;
        for (int i = 0; i < this.timeStamp.size(); i++) {
            time = (ObjectLyrics3TimeStamp) this.timeStamp.get(i);
            str += time.writeString();
        }
        return str + this.lyric;
    }
}