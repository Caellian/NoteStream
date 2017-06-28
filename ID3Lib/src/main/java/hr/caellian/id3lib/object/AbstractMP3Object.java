package hr.caellian.id3lib.object;

import java.util.Arrays;

/**
 * ID3v2 and Lyrics3v2 tags have individual fields <code>AbstractMP3Fragment</code>s Then each fragment is broken down
 * in to individual <code>AbstractMP3Object</code>s
 *
 * @author Eric Farng
 * @version $Revision: 1.4 $
 */
public abstract class AbstractMP3Object {

    protected Object value = null;
    protected String identifier = "";

    /**
     * Creates a new AbstractMP3Object object.
     */
    public AbstractMP3Object() {
        this.value = null;
        this.identifier = "";
    }

    /**
     * Creates a new AbstractMP3Object object.
     */
    public AbstractMP3Object(final AbstractMP3Object copyObject) {
        // no copy constructor in super class
        this.identifier = copyObject.identifier;
        if (copyObject.value == null) {
            this.value = null;
        } else if (copyObject.value instanceof String) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof Boolean) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof Byte) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof Character) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof Double) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof Float) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof Integer) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof Long) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof Short) {
            this.value = copyObject.value;
        } else if (copyObject.value instanceof boolean[]) {
            this.value = ((boolean[]) copyObject.value).clone();
        } else if (copyObject.value instanceof byte[]) {
            this.value = ((byte[]) copyObject.value).clone();
        } else if (copyObject.value instanceof char[]) {
            this.value = ((char[]) copyObject.value).clone();
        } else if (copyObject.value instanceof double[]) {
            this.value = ((double[]) copyObject.value).clone();
        } else if (copyObject.value instanceof float[]) {
            this.value = ((float[]) copyObject.value).clone();
        } else if (copyObject.value instanceof int[]) {
            this.value = ((int[]) copyObject.value).clone();
        } else if (copyObject.value instanceof long[]) {
            this.value = ((long[]) copyObject.value).clone();
        } else if (copyObject.value instanceof short[]) {
            this.value = ((short[]) copyObject.value).clone();
        } else if (copyObject.value instanceof Object[]) {
            this.value = ((Object[]) copyObject.value).clone();
        } else {
            throw new UnsupportedOperationException("Unable to create copy of class " + copyObject.getClass());
        }
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public final void readByteArray(final byte[] arr) {
        readByteArray(arr, 0);
    }

    public final void readString(final String str) {
        readString(str, 0);
    }

    public abstract int getSize();

    public void readByteArray(final byte[] arr, final int offset) {
        readString(new String(arr), offset);
    }

    public void readString(final String str, final int offset) {
        readByteArray(str.substring(offset).getBytes(), 0);
    }

    public abstract String toString();

    public boolean equals(final Object obj) {
        if (!(obj instanceof AbstractMP3Object)) {
            return false;
        }
        final AbstractMP3Object abstractMp3Object = (AbstractMP3Object) obj;
        if (!this.identifier.equals(abstractMp3Object.identifier)) {
            return false;
        }
        if ((this.value == null) && (abstractMp3Object.value == null)) {
            return true;
        } else if ((this.value == null) || (abstractMp3Object.value == null)) {
            return false;
        }

        // boolean[]
        if (this.value instanceof boolean[] && abstractMp3Object.value instanceof boolean[]) {
            if (!Arrays.equals((boolean[]) this.value, (boolean[]) abstractMp3Object.value)) {
                return false;
            }

            // byte[]
        } else if (this.value instanceof byte[] && abstractMp3Object.value instanceof byte[]) {
            if (!Arrays.equals((byte[]) this.value, (byte[]) abstractMp3Object.value)) {
                return false;
            }

            // char[]
        } else if (this.value instanceof char[] && abstractMp3Object.value instanceof char[]) {
            if (!Arrays.equals((char[]) this.value, (char[]) abstractMp3Object.value)) {
                return false;
            }

            // double[]
        } else if (this.value instanceof double[] && abstractMp3Object.value instanceof double[]) {
            if (!Arrays.equals((double[]) this.value, (double[]) abstractMp3Object.value)) {
                return false;
            }

            // float[]
        } else if (this.value instanceof float[] && abstractMp3Object.value instanceof float[]) {
            if (!Arrays.equals((float[]) this.value, (float[]) abstractMp3Object.value)) {
                return false;
            }

            // int[]
        } else if (this.value instanceof int[] && abstractMp3Object.value instanceof int[]) {
            if (!Arrays.equals((int[]) this.value, (int[]) abstractMp3Object.value)) {
                return false;
            }

            // long[]
        } else if (this.value instanceof long[] && abstractMp3Object.value instanceof long[]) {
            if (!Arrays.equals((long[]) this.value, (long[]) abstractMp3Object.value)) {
                return false;
            }

            // Object[]
        } else if (this.value instanceof Object[] && abstractMp3Object.value instanceof Object[]) {
            if (!Arrays.equals((Object[]) this.value, (Object[]) abstractMp3Object.value)) {
                return false;
            }

            // short[]
        } else if (this.value instanceof short[] && abstractMp3Object.value instanceof short[]) {
            if (!Arrays.equals((short[]) this.value, (short[]) abstractMp3Object.value)) {
                return false;
            }
        } else if (!this.value.equals(abstractMp3Object.value)) {
            return false;
        }
        return true;
    }

    public byte[] writeByteArray() {
        return writeString().getBytes();
    }

    public String writeString() {
        return new String(writeByteArray());
    }
}