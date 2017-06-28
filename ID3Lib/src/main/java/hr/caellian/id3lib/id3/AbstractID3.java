package hr.caellian.id3lib.id3;

import hr.caellian.id3lib.AbstractMP3Tag;

/**
 * Superclass for all ID3 tags
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public abstract class AbstractID3 extends AbstractMP3Tag {

    /**
     * Creates a new AbstractID3 object.
     */
    protected AbstractID3() {
        super();
    }

    /**
     * Creates a new AbstractID3 object.
     */
    protected AbstractID3(final AbstractID3 copyObject) {
        super(copyObject);
    }
}