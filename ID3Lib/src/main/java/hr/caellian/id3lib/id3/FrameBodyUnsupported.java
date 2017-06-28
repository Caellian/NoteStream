package hr.caellian.id3lib.id3;

import hr.caellian.id3lib.InvalidTagException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * This frame is used if the frame identifier is not recognized. the contents of the frame are read as a byte stream and
 * kept so they can be saved when the file is written again
 *
 * @author Eric Farng
 * @version $Revision: 1.4 $
 */
public class FrameBodyUnsupported extends AbstractID3v2FrameBody {

    private String identifier = "";
    private byte[] value;

    /**
     * Creates a new FrameBodyUnsupported object.
     */
    public FrameBodyUnsupported(final byte[] value) {
        this.value = value;
    }

    /**
     * Creates a new FrameBodyUnsupported object.
     */
    public FrameBodyUnsupported(final FrameBodyUnsupported copyObject) {
        super(copyObject);
        this.identifier = copyObject.identifier;
        this.value = copyObject.value.clone();
    }

    /**
     * Creates a new FrameBodyUnsupported object.
     */
    public FrameBodyUnsupported(final RandomAccessFile file) throws IOException, InvalidTagException {
        this.read(file);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getSize() {
        int size = 0;
        if (this.value != null) {
            size += this.value.length;
        }
        return size;
    }

    public boolean isSubsetOf(final Object object) {
        if (!(object instanceof FrameBodyUnsupported)) {
            return false;
        }
        final FrameBodyUnsupported frameBodyUnsupported = (FrameBodyUnsupported) object;
        final String subset = new String(this.value);
        final String superset = new String(frameBodyUnsupported.value);
        return superset.contains(subset) && super.isSubsetOf(object);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof FrameBodyUnsupported)) {
            return false;
        }
        final FrameBodyUnsupported frameBodyUnsupported = (FrameBodyUnsupported) obj;
        return this.identifier.equals(frameBodyUnsupported.identifier) &&
                Arrays.equals(this.value, frameBodyUnsupported.value) && super.equals(obj);
    }

    protected void setupObjectList() {
//        throw new UnsupportedOperationException();
    }

    public void read(final RandomAccessFile file) throws IOException, InvalidTagException {
        final int size;
        final byte[] buffer;
        if (has6ByteHeader()) {
            // go back and read the 3 byte unsupported identifier;
            file.seek(file.getFilePointer() - 3);
            buffer = new byte[3];
            file.read(buffer);
            this.identifier = new String(buffer, 0, 3);
        } else {
            // go back and read the 4 byte unsupported identifier;
            file.seek(file.getFilePointer() - 4);
            buffer = new byte[4];
            file.read(buffer);
            this.identifier = new String(buffer);
        }
        size = readHeader(file);

        // read the data
        this.value = new byte[size];
        file.read(this.value);
    }

    public String toString() {
        return "??" + getIdentifier() + " : " + (new String(this.value));
    }

    public void write(final RandomAccessFile file) throws IOException {
        writeHeader(file, this.getSize());
        file.write(this.value);
    }
}