package hr.caellian.id3lib.id3;

import hr.caellian.id3lib.InvalidTagException;
import hr.caellian.id3lib.object.ObjectStringSizeTerminated;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * All frames starting with "U" are the same structurally and subclass from here
 *
 * @author Eric Farng
 * @version $Revision: 1.5 $
 */
public abstract class AbstractFrameBodyUrlLink extends AbstractID3v2FrameBody {

    /**
     * Creates a new FrameBodyUrlLink object.
     */
    protected AbstractFrameBodyUrlLink() {
        super();
    }

    /**
     * Creates a new AbstractFrameBodyUrlLink object.
     */
    protected AbstractFrameBodyUrlLink(final AbstractFrameBodyUrlLink body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyUrlLink object.
     */
    protected AbstractFrameBodyUrlLink(final String urlLink) {
        super();
        setObject("URL Link", urlLink);
    }

    /**
     * Creates a new FrameBodyUrlLink object.
     */
    protected AbstractFrameBodyUrlLink(final RandomAccessFile file) throws IOException, InvalidTagException {
        super();
        read(file);
    }

    public String getBriefDescription() {
        return getUrlLink();
    }

    public void setUrlLink(final String urlLink) {
        setObject("URL Link", urlLink);
    }

    public String getUrlLink() {
        return (String) getObject("URL Link");
    }

    protected void setupObjectList() {
        appendToObjectList(new ObjectStringSizeTerminated("URL Link"));
    }
}