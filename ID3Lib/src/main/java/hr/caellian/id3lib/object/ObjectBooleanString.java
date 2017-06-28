package hr.caellian.id3lib.object;

/**
 * ID3v2 and Lyrics3v2 tags have individual fields <code>AbstractMP3Fragment</code>s Then each fragment is broken down
 * in to individual <code>AbstractMP3Object</code>s
 *
 * @author Eric Farng
 * @version $Revision: 1.5 $
 */
public class ObjectBooleanString extends AbstractMP3Object {

    /**
     * Creates a new ObjectBooleanString object.
     */
    public ObjectBooleanString(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Creates a new ObjectBooleanString object.
     */
    public ObjectBooleanString(final ObjectBooleanString object) {
        super(object);
    }

    public int getSize() {
        return 1;
    }

    public boolean equals(final Object obj) {
        return obj instanceof ObjectBooleanString && super.equals(obj);
    }

    public void readString(final String str, final int offset) {
        if (str == null) {
            throw new NullPointerException("String is null");
        }
        if ((offset < 0) || (offset >= str.length())) {
            throw new IndexOutOfBoundsException("Offset to image string is out of bounds: offset = " +
                                                offset +
                                                ", string.length()" +
                                                str.length());
        }
        final char ch = str.charAt(offset);
        this.value = ch != '0';
    }

    public String toString() {
        return "" + this.value;
    }

    public String writeString() {
        if (this.value == null) {
            // default false
            return "0";
        }
        return (Boolean) this.value ? "1" : "0";
    }
}