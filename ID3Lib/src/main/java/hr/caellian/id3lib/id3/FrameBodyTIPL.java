package hr.caellian.id3lib.id3;

import hr.caellian.id3lib.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * &nbsp;&nbsp; The 'Involved people list' is very similar to the musician credits<br> &nbsp;&nbsp; list, but maps
 * between functions, like producer, and names.</p>
 *
 * @author Eric Farng
 * @version $Revision: 1.4 $
 */
public class FrameBodyTIPL extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTIPL object.
     */
    public FrameBodyTIPL() {
        super();
    }

    /**
     * Creates a new FrameBodyTIPL object.
     */
    public FrameBodyTIPL(final FrameBodyTIPL body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIPL object.
     */
    public FrameBodyTIPL(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIPL object.
     */
    public FrameBodyTIPL(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TIPL";
    }
}