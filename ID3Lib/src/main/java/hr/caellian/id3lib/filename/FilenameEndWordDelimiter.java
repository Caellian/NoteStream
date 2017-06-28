package hr.caellian.id3lib.filename;

/**
 * This class is a delimiter which remains a part of the token, located at the end.
 *
 * @author Eric Farng
 * @version $Revision: 1.2 $
 */
public class FilenameEndWordDelimiter extends FilenameDelimiter {

    /**
     * Creates a new FilenameEndWordDelimiter object.
     */
    public FilenameEndWordDelimiter() {
        super();
    }

    /**
     * Creates a new FilenameEndWordDelimiter object.
     */
    public FilenameEndWordDelimiter(final FilenameEndWordDelimiter delimiter) {
        super(delimiter);
    }

    /**
     * Reconstruct the filename that is represented by this composite.
     *
     * @return the filename that is represented by this composite.
     */
    public String composeFilename() {
        final StringBuilder stringBuffer = new StringBuilder(128);
        if (getBeforeComposite() != null) {
            stringBuffer.append(getBeforeComposite().composeFilename());
            stringBuffer.append(' ');
        }
        if (getAfterComposite() != null) {
            stringBuffer.append(getAfterComposite().composeFilename());
        }
        return stringBuffer.toString().trim();
    }
}