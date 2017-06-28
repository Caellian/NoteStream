package hr.caellian.id3lib.id3;

import hr.caellian.id3lib.InvalidTagException;
import hr.caellian.id3lib.object.ObjectNumberHashMap;
import hr.caellian.id3lib.object.ObjectStringDateTime;

import java.io.RandomAccessFile;

/**
 * &nbsp;&nbsp; The 'Recording time' frame contains a timestamp describing when the<br>
 * <p/>
 * &nbsp;&nbsp; audio was recorded. Timestamp format is described in the ID3v2<br> &nbsp;&nbsp; structure document
 * [ID3v2-strct].</p>
 *
 * @author Eric Farng
 * @version $Revision: 1.4 $
 */
public class FrameBodyTDRC extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTDRC object.
     */
    public FrameBodyTDRC() {
        super();
    }

    /**
     * Creates a new FrameBodyTDRC object.
     */
    public FrameBodyTDRC(final FrameBodyTDRC body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTDRC object.
     */
    public FrameBodyTDRC(final byte textEncoding, final String text) {
        setObject(ObjectNumberHashMap.TEXT_ENCODING, textEncoding);
        setObject("Date Time", text);
    }

    /**
     * Creates a new FrameBodyTDRC object.
     */
    public FrameBodyTDRC(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TDRC";
    }

    public void setText(final String text) {
        setObject("Date Time", text);
    }

    public String getText() {
        return (String) getObject("Date Time");
    }

    protected void setupObjectList() {
        appendToObjectList(new ObjectNumberHashMap(ObjectNumberHashMap.TEXT_ENCODING, 1));
        appendToObjectList(new ObjectStringDateTime("Date Time"));
    }
}