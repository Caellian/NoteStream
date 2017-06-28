package hr.caellian.id3lib.id3;

import hr.caellian.id3lib.InvalidTagException;
import hr.caellian.id3lib.object.ObjectNumberHashMap;
import hr.caellian.id3lib.object.ObjectStringDateTime;

import java.io.RandomAccessFile;

/**
 * &nbsp;&nbsp; The 'Encoding time' frame contains a timestamp describing when the<br>
 * <p/>
 * &nbsp;&nbsp; audio was encoded. Timestamp format is described in the ID3v2<br> &nbsp;&nbsp; structure document
 * [ID3v2-strct].</p>
 *
 * @author Eric Farng
 * @version $Revision: 1.4 $
 */
public class FrameBodyTDEN extends AbstractFrameBodyTextInformation {

    /**
     * Creates a new FrameBodyTDEN object.
     */
    public FrameBodyTDEN() {
        super();
    }

    /**
     * Creates a new FrameBodyTDEN object.
     */
    public FrameBodyTDEN(final FrameBodyTDEN body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyTDEN object.
     */
    public FrameBodyTDEN(final byte textEncoding, final String text) {
        setObject(ObjectNumberHashMap.TEXT_ENCODING, textEncoding);
        setObject("Date Time", text);
    }

    /**
     * Creates a new FrameBodyTDEN object.
     */
    public FrameBodyTDEN(final RandomAccessFile file) throws java.io.IOException, InvalidTagException {
        super(file);
    }

    public String getIdentifier() {
        return "TDEN";
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