package hr.caellian.id3lib.id3;

import hr.caellian.id3lib.InvalidTagException;

import java.io.RandomAccessFile;

/**
 * &nbsp;&nbsp; The 'Official audio source webpage' frame is a URL pointing at the<br> &nbsp;&nbsp; official webpage for
 * the source of the audio file, e.g. a movie.</p>
 *
 * @author Eric Farng
 * @version $Revision: 1.4 $
 */
public class FrameBodyWOAS extends AbstractFrameBodyUrlLink {

    /**
     * Creates a new FrameBodyWOAS object.
     */
    public FrameBodyWOAS() {
        super();
    }

    /**
     * Creates a new FrameBodyWOAS object.
     */
    public FrameBodyWOAS(final String urlLink) {
        super(urlLink);
    }

    /**
     * Creates a new FrameBodyWOAS object.
     */
    public FrameBodyWOAS(final FrameBodyWOAS body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyWOAS object.
     */
    public FrameBodyWOAS(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "WOAS";
    }
}