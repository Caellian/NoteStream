package hr.caellian.id3lib.id3;

import hr.caellian.id3lib.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * &nbsp;&nbsp; The 'File owner/licensee' frame contains the name of the owner or<br>
 * <p/>
 * &nbsp;&nbsp; licensee of the file and it's contents.</p>
 *
 * @author Eric Farng
 * @version $Revision: 1.4 $
 */
public class FrameBodyTOWN extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTOWN object.
     */
    public FrameBodyTOWN() {
        super();
    }

    /**
     * Creates a new FrameBodyTOWN object.
     */
    public FrameBodyTOWN(final FrameBodyTOWN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOWN object.
     */
    public FrameBodyTOWN(final byte textEncoding, final String text) {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOWN object.
     */
    public FrameBodyTOWN(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TOWN";
    }
}