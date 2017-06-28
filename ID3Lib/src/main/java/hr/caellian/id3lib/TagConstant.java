package hr.caellian.id3lib;

import android.util.LongSparseArray;

import java.util.HashMap;
import java.util.Map;

/**
 * This contains all ID3 frame descriptions and Lyric3 field description. It also has bit masks for all the flags in the
 * MP3 Header.
 *
 * @author Eric Farng
 * @version $Revision: 1.3 $
 */
public class TagConstant {

    // Bit numbering starts with the most significat bit as 7
    /**
     * defined for convenience
     */
    public static final int BIT7 = 0x80;
    /**
     * defined for convenience
     */
    public static final int BIT6 = 0x40;
    /**
     * defined for convenience
     */
    public static final int BIT5 = 0x20;
    /**
     * defined for convenience
     */
    public static final int BIT4 = 0x10;
    /**
     * defined for convenience
     */
    public static final int BIT3 = 0x08;
    /**
     * defined for convenience
     */
    public static final int BIT2 = 0x04;
    /**
     * defined for convenience
     */
    public static final int BIT1 = 0x02;
    /**
     * defined for convenience
     */
    public static final int BIT0 = 0x01;
    /**
     * System seperators
     */
    public static final String SEPERATOR_LINE = System.getProperty("line.separator");
    public static final String SEPERATOR_FILE = System.getProperty("file.separator");
    public static final String SEPERATOR_PATH = System.getProperty("path.separator");
    /**
     * MP3 save mode lowest numbered index
     */
    public static final int MP3_FILE_SAVE_FIRST = 1;
    /**
     * MP3 save mode matching <code>write</code> method
     */
    public static final int MP3_FILE_SAVE_WRITE = 1;
    /**
     * MP3 save mode matching <code>overwrite</code> method
     */
    public static final int MP3_FILE_SAVE_OVERWRITE = 2;
    /**
     * MP3 save mode matching <code>append</code> method
     */
    public static final int MP3_FILE_SAVE_APPEND = 3;
    /**
     * MP3 save mode highest numbered index
     */
    public static final int MP3_FILE_SAVE_LAST = 3;
    /**
     * ID3v2.2 Header bit mask
     */
    public static final int MASK_V22_UNSYNCHRONIZATION = BIT7;
    /**
     * ID3v2.2 Header bit mask
     */
    public static final int MASK_V22_COMPRESSION = BIT7;
    /**
     * ID3v2.2 BUF Frame bit mask
     */
    public static final int MASK_V22_EMBEDDED_INFO_FLAG = BIT1;
    /**
     * ID3v2.3 Header bit mask
     */
    public static final int MASK_V23_UNSYNCHRONIZATION = BIT7;
    /**
     * ID3v2.3 Header bit mask
     */
    public static final int MASK_V23_EXTENDED_HEADER = BIT6;
    /**
     * ID3v2.3 Header bit mask
     */
    public static final int MASK_V23_EXPERIMENTAL = BIT5;
    /**
     * ID3v2.3 Extended Header bit mask
     */
    public static final int MASK_V23_CRC_DATA_PRESENT = BIT7;
    /**
     * ID3v2.3 Frame bit mask
     */
    public static final int MASK_V23_TAG_ALTER_PRESERVATION = BIT7;
    /**
     * ID3v2.3 Frame bit mask
     */
    public static final int MASK_V23_FILE_ALTER_PRESERVATION = BIT6;
    /**
     * ID3v2.3 Frame bit mask
     */
    public static final int MASK_V23_READ_ONLY = BIT5;
    /**
     * ID3v2.3 Frame bit mask
     */
    public static final int MASK_V23_COMPRESSION = BIT7;
    /**
     * ID3v2.3 Frame bit mask
     */
    public static final int MASK_V23_ENCRYPTION = BIT6;
    /**
     * ID3v2.3 Frame bit mask
     */
    public static final int MASK_V23_GROUPING_IDENTITY = BIT5;
    /**
     * ID3v2.3 RBUF frame bit mask
     */
    public static final int MASK_V23_EMBEDDED_INFO_FLAG = BIT1;
    /**
     * ID3v2.4 Header bit mask
     */
    public static final int MASK_V24_UNSYNCHRONIZATION = BIT7;
    /**
     * ID3v2.4 Header bit mask
     */
    public static final int MASK_V24_EXTENDED_HEADER = BIT6;
    /**
     * ID3v2.4 Header bit mask
     */
    public static final int MASK_V24_EXPERIMENTAL = BIT5;
    /**
     * ID3v2.4 Header bit mask
     */
    public static final int MASK_V24_FOOTER_PRESENT = BIT4;
    /**
     * ID3v2.4 Extended header bit mask
     */
    public static final int MASK_V24_TAG_UPDATE = BIT6;
    /**
     * ID3v2.4 Extended header bit mask
     */
    public static final int MASK_V24_CRC_DATA_PRESENT = BIT5;
    /**
     * ID3v2.4 Extended header bit mask
     */
    public static final int MASK_V24_TAG_RESTRICTIONS = BIT4;
    /**
     * ID3v2.4 Extended header bit mask
     */
    public static final int MASK_V24_TAG_SIZE_RESTRICTIONS = (byte) BIT7 | BIT6;
    /**
     * ID3v2.4 Extended header bit mask
     */
    public static final int MASK_V24_TEXT_ENCODING_RESTRICTIONS = BIT5;
    /**
     * ID3v2.4 Extended header bit mask
     */
    public static final int MASK_V24_TEXT_FIELD_SIZE_RESTRICTIONS = BIT4 | BIT3;
    /**
     * ID3v2.4 Extended header bit mask
     */
    public static final int MASK_V24_IMAGE_ENCODING = BIT2;
    /**
     * ID3v2.4 Extended header bit mask
     */
    public static final int MASK_V24_IMAGE_SIZE_RESTRICTIONS = BIT2 | BIT1;

    /*
     * ID3v2.4 Header Footer are the same as the header flags. WHY?!?! move the
     * flags from thier position in 2.3??????????
     */
    /**
     * ID3v2.4 Header Footer bit mask
     */
    public static final int MASK_V24_TAG_ALTER_PRESERVATION = BIT6;
    /**
     * ID3v2.4 Header Footer bit mask
     */
    public static final int MASK_V24_FILE_ALTER_PRESERVATION = BIT5;
    /**
     * ID3v2.4 Header Footer bit mask
     */
    public static final int MASK_V24_READ_ONLY = BIT4;
    /**
     * ID3v2.4 Header Footer bit mask
     */
    public static final int MASK_V24_GROUPING_IDENTITY = BIT6;
    /**
     * ID3v2.4 Header Footer bit mask
     */
    public static final int MASK_V24_COMPRESSION = BIT4;
    /**
     * ID3v2.4 Header Footer bit mask
     */
    public static final int MASK_V24_ENCRYPTION = BIT3;
    /**
     * ID3v2.4 Header Footer bit mask
     */
    public static final int MASK_V24_FRAME_UNSYNCHRONIZATION = BIT2;
    /**
     * ID3v2.4 Header Footer bit mask
     */
    public static final int MASK_V24_DATA_LENGTH_INDICATOR = BIT1;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_ID = BIT3;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_VERSION = BIT4 | BIT3;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_LAYER = BIT2 | BIT1;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_PROTECTION = BIT0;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_BITRATE = BIT7 | BIT6 | BIT5 | BIT4;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_FREQUENCY = BIT3 + BIT2;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_PADDING = BIT1;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_PRIVACY = BIT0;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_MODE = BIT7 | BIT6;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_MODE_EXTENSION = BIT5 | BIT4;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_COPY = BIT3;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_HOME = BIT2;
    /**
     * MP3 Frame Header bit mask
     */
    public static final int MASK_MP3_EMPHASIS = BIT1 | BIT0;
    /**
     * <code>HashMap</code> translating the three letter ID into a human understandable string
     */
    public static final HashMap<String, String> id3v2_2FrameIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating the four letter ID into a human understandable string
     */
    public static final HashMap<String, String> id3v2_3FrameIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating the four letter ID into a human understandable string
     */
    public static final HashMap<String, String> id3v2_4FrameIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating the predefined string into the three letter ID
     */
    public static final HashMap<String, String> id3v2_2FrameStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating the predefined string into the four letter ID
     */
    public static final HashMap<String, String> id3v2_3FrameStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating the predefined string into the four letter ID
     */
    public static final HashMap<String, String> id3v2_4FrameStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating the predefined string into the three letter ID
     */
    public static final HashMap<String, String> lyrics3v2FieldIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating the predefined string into the three letter ID
     */
    public static final HashMap<String, String> lyrics3v2FieldStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating the three letter ID3v2.2 ID to the corresponding ID3v2.4 ID
     */
    public static final HashMap<String, String> id3v2_3ToId3v2_4 = new HashMap<>();
    /**
     * <code>HashMap</code> translating the three letter ID3v2.2 ID to the corresponding ID3v2.3 ID
     */
    public static final HashMap<String, String> id3v2_2ToId3v2_3 = new HashMap<>();
    /**
     * <code>HashMap</code> translating the three letter ID3v2.2 ID to the corresponding ID3v2.4 ID
     */
    public static final HashMap<String, String> id3v2_4ToId3v2_3 = new HashMap<>();
    /**
     * <code>HashMap</code> translating the three letter ID3v2.2 ID to the corresponding ID3v2.3 ID
     */
    public static final HashMap<String, String> id3v2_3ToId3v2_2 = new HashMap<>();
    /**
     * <code>HashMap</code> translating the ID3v1 genre bit into a human readable string
     */
    public static final HashMap<Long, String> genreIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating the predefined genre string into the ID3v1 genre bit
     */
    public static final HashMap<String, Long> genreStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating the language ID to a human readable string. [ISO-639-2] ISO/FDIS 639-2
     */
    public static final HashMap<String, String> languageIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating the predefined language string into the ID. [ISO-639-2] ISO/FDIS 639-2
     */
    public static final HashMap<String, String> languageStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating the bitrate read in from the MP3 Header into a base-10 integer
     */
    public static final HashMap<Long, Long> bitrate = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<Long, String> textEncodingIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<String, Long> textEncodingStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<Long, String> interpolationMethodIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<String, Long> interpolationMethodStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<Long, String> pictureTypeIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<String, Long> pictureTypeStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<Long, String> timeStampFormatIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<String, Long> timeStampFormatStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<Long, String> typeOfEventIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<String, Long> typeOfEventStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<Long, String> typeOfChannelIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<String, Long> typeOfChannelStringToId = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<Long, String> recievedAsIdToString = new HashMap<>();
    /**
     * <code>HashMap</code> translating table found in ID3 tags
     */
    public static final HashMap<String, Long> recievedAsStringToId = new HashMap<>();

    static {
        id3v2_2FrameIdToString.put("BUF", "Recommended buffer size");
        id3v2_2FrameIdToString.put("CNT", "Play counter");
        id3v2_2FrameIdToString.put("COM", "Comments");
        id3v2_2FrameIdToString.put("CRA", "Audio encryption");
        id3v2_2FrameIdToString.put("CRM", "Encrypted meta frame");
        id3v2_2FrameIdToString.put("ETC", "Event timing codes");
        id3v2_2FrameIdToString.put("EQU", "Equalization");
        id3v2_2FrameIdToString.put("GEO", "General encapsulated object");
        id3v2_2FrameIdToString.put("IPL", "Involved people list");
        id3v2_2FrameIdToString.put("LNK", "Linked information");
        id3v2_2FrameIdToString.put("MCI", "Music CD Identifier");
        id3v2_2FrameIdToString.put("MLL", "MPEG location lookup table");
        id3v2_2FrameIdToString.put("PIC", "Attached picture");
        id3v2_2FrameIdToString.put("POP", "Popularimeter");
        id3v2_2FrameIdToString.put("REV", "Reverb");
        id3v2_2FrameIdToString.put("RVA", "Relative volume adjustment");
        id3v2_2FrameIdToString.put("SLT", "Synchronized lyric/text");
        id3v2_2FrameIdToString.put("STC", "Synced tempo codes");
        id3v2_2FrameIdToString.put("TAL", "Text: Album/Movie/Show title");
        id3v2_2FrameIdToString.put("TBP", "Text: BPM (Beats Per Minute)");
        id3v2_2FrameIdToString.put("TCM", "Text: Composer");
        id3v2_2FrameIdToString.put("TCO", "Text: Content type");
        id3v2_2FrameIdToString.put("TCR", "Text: Copyright message");
        id3v2_2FrameIdToString.put("TDA", "Text: Date");
        id3v2_2FrameIdToString.put("TDY", "Text: Playlist delay");
        id3v2_2FrameIdToString.put("TEN", "Text: Encoded by");
        id3v2_2FrameIdToString.put("TFT", "Text: File type");
        id3v2_2FrameIdToString.put("TIM", "Text: Time");
        id3v2_2FrameIdToString.put("TKE", "Text: Initial key");
        id3v2_2FrameIdToString.put("TLA", "Text: Language(s)");
        id3v2_2FrameIdToString.put("TLE", "Text: Length");
        id3v2_2FrameIdToString.put("TMT", "Text: Media type");
        id3v2_2FrameIdToString.put("TOA", "Text: Original artist(s)/performer(s)");
        id3v2_2FrameIdToString.put("TOF", "Text: Original filename");
        id3v2_2FrameIdToString.put("TOL", "Text: Original Lyricist(s)/text writer(s)");
        id3v2_2FrameIdToString.put("TOR", "Text: Original release year");
        id3v2_2FrameIdToString.put("TOT", "Text: Original album/Movie/Show title");
        id3v2_2FrameIdToString.put("TP1", "Text: Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group");
        id3v2_2FrameIdToString.put("TP2", "Text: Band/Orchestra/Accompaniment");
        id3v2_2FrameIdToString.put("TP3", "Text: Conductor/Performer refinement");
        id3v2_2FrameIdToString.put("TP4", "Text: Interpreted, remixed, or otherwise modified by");
        id3v2_2FrameIdToString.put("TPA", "Text: Part of a set");
        id3v2_2FrameIdToString.put("TPB", "Text: Publisher");
        id3v2_2FrameIdToString.put("TRC", "Text: ISRC (International Standard Recording Code)");
        id3v2_2FrameIdToString.put("TRD", "Text: Recording dates");
        id3v2_2FrameIdToString.put("TRK", "Text: Track number/Position in set");
        id3v2_2FrameIdToString.put("TSI", "Text: Size");
        id3v2_2FrameIdToString.put("TSS", "Text: Software/hardware and settings used for encoding");
        id3v2_2FrameIdToString.put("TT1", "Text: Content group description");
        id3v2_2FrameIdToString.put("TT2", "Text: Title/Songname/Content description");
        id3v2_2FrameIdToString.put("TT3", "Text: Subtitle/Description refinement");
        id3v2_2FrameIdToString.put("TXT", "Text: Lyricist/text writer");
        id3v2_2FrameIdToString.put("TXX", "User defined text information frame");
        id3v2_2FrameIdToString.put("TYE", "Text: Year");
        id3v2_2FrameIdToString.put("UFI", "Unique file identifier");
        id3v2_2FrameIdToString.put("ULT", "Unsychronized lyric/text transcription");
        id3v2_2FrameIdToString.put("WAF", "URL: Official audio file webpage");
        id3v2_2FrameIdToString.put("WAR", "URL: Official artist/performer webpage");
        id3v2_2FrameIdToString.put("WAS", "URL: Official audio source webpage");
        id3v2_2FrameIdToString.put("WCM", "URL: Commercial information");
        id3v2_2FrameIdToString.put("WCP", "URL: Copyright/Legal information");
        id3v2_2FrameIdToString.put("WPB", "URL: Publishers official webpage");
        id3v2_2FrameIdToString.put("WXX", "User defined URL link frame");
        for (Map.Entry<String, String> entry : id3v2_2FrameIdToString.entrySet()) {
            id3v2_2FrameStringToId.put(entry.getValue(), entry.getKey());
        }
        id3v2_3FrameIdToString.put("AENC", "Audio encryption");
        id3v2_3FrameIdToString.put("APIC", "Attached picture");
        id3v2_3FrameIdToString.put("COMM", "Comments");
        id3v2_3FrameIdToString.put("COMR", "Commercial frame");
        id3v2_3FrameIdToString.put("ENCR", "Encryption method registration");
        id3v2_3FrameIdToString.put("EQUA", "Equalization");
        id3v2_3FrameIdToString.put("ETCO", "Event timing codes");
        id3v2_3FrameIdToString.put("GEOB", "General encapsulated object");
        id3v2_3FrameIdToString.put("GRID", "Group identification registration");
        id3v2_3FrameIdToString.put("IPLS", "Involved people list");
        id3v2_3FrameIdToString.put("LINK", "Linked information");
        id3v2_3FrameIdToString.put("MCDI", "Music CD identifier");
        id3v2_3FrameIdToString.put("MLLT", "MPEG location lookup table");
        id3v2_3FrameIdToString.put("OWNE", "Ownership frame");
        id3v2_3FrameIdToString.put("PRIV", "Private frame");
        id3v2_3FrameIdToString.put("PCNT", "Play counter");
        id3v2_3FrameIdToString.put("POPM", "Popularimeter");
        id3v2_3FrameIdToString.put("POSS", "Position synchronisation frame");
        id3v2_3FrameIdToString.put("RBUF", "Recommended buffer size");
        id3v2_3FrameIdToString.put("RVAD", "Relative volume adjustment");
        id3v2_3FrameIdToString.put("RVRB", "Reverb");
        id3v2_3FrameIdToString.put("SYLT", "Synchronized lyric/text");
        id3v2_3FrameIdToString.put("SYTC", "Synchronized tempo codes");
        id3v2_3FrameIdToString.put("TALB", "Text: Album/Movie/Show title");
        id3v2_3FrameIdToString.put("TBPM", "Text: BPM (beats per minute)");
        id3v2_3FrameIdToString.put("TCOM", "Text: Composer");
        id3v2_3FrameIdToString.put("TCON", "Text: Content type");
        id3v2_3FrameIdToString.put("TCOP", "Text: Copyright message");
        id3v2_3FrameIdToString.put("TDAT", "Text: Date");
        id3v2_3FrameIdToString.put("TDLY", "Text: Playlist delay");
        id3v2_3FrameIdToString.put("TENC", "Text: Encoded by");
        id3v2_3FrameIdToString.put("TEXT", "Text: Lyricist/Text writer");
        id3v2_3FrameIdToString.put("TFLT", "Text: File type");
        id3v2_3FrameIdToString.put("TIME", "Text: Time");
        id3v2_3FrameIdToString.put("TIT1", "Text: Content group description");
        id3v2_3FrameIdToString.put("TIT2", "Text: Title/songname/content description");
        id3v2_3FrameIdToString.put("TIT3", "Text: Subtitle/Description refinement");
        id3v2_3FrameIdToString.put("TKEY", "Text: Initial key");
        id3v2_3FrameIdToString.put("TLAN", "Text: Language(s)");
        id3v2_3FrameIdToString.put("TLEN", "Text: Length");
        id3v2_3FrameIdToString.put("TMED", "Text: Media type");
        id3v2_3FrameIdToString.put("TOAL", "Text: Original album/movie/show title");
        id3v2_3FrameIdToString.put("TOFN", "Text: Original filename");
        id3v2_3FrameIdToString.put("TOLY", "Text: Original lyricist(s)/text writer(s)");
        id3v2_3FrameIdToString.put("TOPE", "Text: Original artist(s)/performer(s)");
        id3v2_3FrameIdToString.put("TORY", "Text: Original release year");
        id3v2_3FrameIdToString.put("TOWN", "Text: File owner/licensee");
        id3v2_3FrameIdToString.put("TPE1", "Text: Lead performer(s)/Soloist(s)");
        id3v2_3FrameIdToString.put("TPE2", "Text: Band/orchestra/accompaniment");
        id3v2_3FrameIdToString.put("TPE3", "Text: Conductor/performer refinement");
        id3v2_3FrameIdToString.put("TPE4", "Text: Interpreted, remixed, or otherwise modified by");
        id3v2_3FrameIdToString.put("TPOS", "Text: Part of a set");
        id3v2_3FrameIdToString.put("TPUB", "Text: Publisher");
        id3v2_3FrameIdToString.put("TRCK", "Text: Track number/Position in set");
        id3v2_3FrameIdToString.put("TRDA", "Text: Recording dates");
        id3v2_3FrameIdToString.put("TRSN", "Text: Internet radio station name");
        id3v2_3FrameIdToString.put("TRSO", "Text: Internet radio station owner");
        id3v2_3FrameIdToString.put("TSIZ", "Text: Size");
        id3v2_3FrameIdToString.put("TSRC", "Text: ISRC (international standard recording code)");
        id3v2_3FrameIdToString.put("TSSE", "Text: Software/Hardware and settings used for encoding");
        id3v2_3FrameIdToString.put("TYER", "Text: Year");
        id3v2_3FrameIdToString.put("TXXX", "User defined text information frame");
        id3v2_3FrameIdToString.put("UFID", "Unique file identifier");
        id3v2_3FrameIdToString.put("USER", "Terms of use");
        id3v2_3FrameIdToString.put("USLT", "Unsychronized lyric/text transcription");
        id3v2_3FrameIdToString.put("WCOM", "URL: Commercial information");
        id3v2_3FrameIdToString.put("WCOP", "URL: Copyright/Legal information");
        id3v2_3FrameIdToString.put("WOAF", "URL: Official audio file webpage");
        id3v2_3FrameIdToString.put("WOAR", "URL: Official artist/performer webpage");
        id3v2_3FrameIdToString.put("WOAS", "URL: Official audio source webpage");
        id3v2_3FrameIdToString.put("WORS", "URL: Official internet radio station homepage");
        id3v2_3FrameIdToString.put("WPAY", "URL: Payment");
        id3v2_3FrameIdToString.put("WPUB", "URL: Publishers official webpage");
        id3v2_3FrameIdToString.put("WXXX", "User defined URL link frame");
        for (Map.Entry<String, String> entry : id3v2_3FrameIdToString.entrySet()) {
            id3v2_3FrameStringToId.put(entry.getValue(), entry.getKey());
        }
        id3v2_4FrameIdToString.put("AENC", "Audio encryption");
        id3v2_4FrameIdToString.put("APIC", "Attached picture");
        id3v2_4FrameIdToString.put("ASPI", "Audio seek point index");
        id3v2_4FrameIdToString.put("COMM", "Comments");
        id3v2_4FrameIdToString.put("COMR", "Commercial frame");
        id3v2_4FrameIdToString.put("ENCR", "Encryption method registration");
        id3v2_4FrameIdToString.put("EQU2", "Equalisation (2)");
        id3v2_4FrameIdToString.put("ETCO", "Event timing codes");
        id3v2_4FrameIdToString.put("GEOB", "General encapsulated object");
        id3v2_4FrameIdToString.put("GRID", "Group identification registration");
        id3v2_4FrameIdToString.put("LINK", "Linked information");
        id3v2_4FrameIdToString.put("MCDI", "Music CD identifier");
        id3v2_4FrameIdToString.put("MLLT", "MPEG location lookup table");
        id3v2_4FrameIdToString.put("OWNE", "Ownership frame");
        id3v2_4FrameIdToString.put("PRIV", "Private frame");
        id3v2_4FrameIdToString.put("PCNT", "Play counter");
        id3v2_4FrameIdToString.put("POPM", "Popularimeter");
        id3v2_4FrameIdToString.put("POSS", "Position synchronisation frame");
        id3v2_4FrameIdToString.put("RBUF", "Recommended buffer size");
        id3v2_4FrameIdToString.put("RVA2", "Relative volume adjustment (2)");
        id3v2_4FrameIdToString.put("RVRB", "Reverb");
        id3v2_4FrameIdToString.put("SEEK", "Seek frame");
        id3v2_4FrameIdToString.put("SIGN", "Signature frame");
        id3v2_4FrameIdToString.put("SYLT", "Synchronised lyric/text");
        id3v2_4FrameIdToString.put("SYTC", "Synchronised tempo codes");
        id3v2_4FrameIdToString.put("TALB", "Text: Album/Movie/Show title");
        id3v2_4FrameIdToString.put("TBPM", "Text: BPM (beats per minute)");
        id3v2_4FrameIdToString.put("TCOM", "Text: Composer");
        id3v2_4FrameIdToString.put("TCON", "Text: Content type (genre)");
        id3v2_4FrameIdToString.put("TCOP", "Text: Copyright message");
        id3v2_4FrameIdToString.put("TDEN", "Text: Encoding time");
        id3v2_4FrameIdToString.put("TDLY", "Text: Playlist delay");
        id3v2_4FrameIdToString.put("TDOR", "Text: Original release time");
        id3v2_4FrameIdToString.put("TDRC", "Text: Recording time");
        id3v2_4FrameIdToString.put("TDRL", "Text: Release time");
        id3v2_4FrameIdToString.put("TDTG", "Text: Tagging time");
        id3v2_4FrameIdToString.put("TENC", "Text: Encoded by");
        id3v2_4FrameIdToString.put("TEXT", "Text: Lyricist/Text writer");
        id3v2_4FrameIdToString.put("TFLT", "Text: File type");
        id3v2_4FrameIdToString.put("TIPL", "Text: Involved people list");
        id3v2_4FrameIdToString.put("TIT1", "Text: Content group description");
        id3v2_4FrameIdToString.put("TIT2", "Text: Title/songname/content description");
        id3v2_4FrameIdToString.put("TIT3", "Text: Subtitle/Description refinement");
        id3v2_4FrameIdToString.put("TKEY", "Text: Initial key");
        id3v2_4FrameIdToString.put("TLAN", "Text: Language(s)");
        id3v2_4FrameIdToString.put("TLEN", "Text: Length");
        id3v2_4FrameIdToString.put("TMCL", "Text: Musician credits list");
        id3v2_4FrameIdToString.put("TMED", "Text: Media type");
        id3v2_4FrameIdToString.put("TMOO", "Text: Mood");
        id3v2_4FrameIdToString.put("TOAL", "Text: Original album/movie/show title");
        id3v2_4FrameIdToString.put("TOFN", "Text: Original filename");
        id3v2_4FrameIdToString.put("TOLY", "Text: Original lyricist(s)/text writer(s)");
        id3v2_4FrameIdToString.put("TOPE", "Text: Original artist(s)/performer(s)");
        id3v2_4FrameIdToString.put("TOWN", "Text: File owner/licensee");
        id3v2_4FrameIdToString.put("TPE1", "Text: Lead performer(s)/Soloist(s)");
        id3v2_4FrameIdToString.put("TPE2", "Text: Band/orchestra/accompaniment");
        id3v2_4FrameIdToString.put("TPE3", "Text: Conductor/performer refinement");
        id3v2_4FrameIdToString.put("TPE4", "Text: Interpreted, remixed, or otherwise modified by");
        id3v2_4FrameIdToString.put("TPOS", "Text: Part of a set");
        id3v2_4FrameIdToString.put("TPRO", "Text: Produced notice");
        id3v2_4FrameIdToString.put("TPUB", "Text: Publisher");
        id3v2_4FrameIdToString.put("TRCK", "Text: Track number/Position in set");
        id3v2_4FrameIdToString.put("TRSN", "Text: Internet radio station name");
        id3v2_4FrameIdToString.put("TRSO", "Text: Internet radio station owner");
        id3v2_4FrameIdToString.put("TSOA", "Text: Album sort order");
        id3v2_4FrameIdToString.put("TSOP", "Text: Performer sort order");
        id3v2_4FrameIdToString.put("TSOT", "Text: Title sort order");
        id3v2_4FrameIdToString.put("TSRC", "Text: ISRC (international standard recording code)");
        id3v2_4FrameIdToString.put("TSSE", "Text: Software/Hardware and settings used for encoding");
        id3v2_4FrameIdToString.put("TSST", "Text: Set subtitle");
        id3v2_4FrameIdToString.put("TXXX", "User defined text information frame");
        id3v2_4FrameIdToString.put("UFID", "Unique file identifier");
        id3v2_4FrameIdToString.put("USER", "Terms of use");
        id3v2_4FrameIdToString.put("USLT", "Unsynchronised lyric/text transcription");
        id3v2_4FrameIdToString.put("WCOM", "URL: Commercial information");
        id3v2_4FrameIdToString.put("WCOP", "URL: Copyright/Legal information");
        id3v2_4FrameIdToString.put("WOAF", "URL: Official audio file webpage");
        id3v2_4FrameIdToString.put("WOAR", "URL: Official artist/performer webpage");
        id3v2_4FrameIdToString.put("WOAS", "URL: Official audio source webpage");
        id3v2_4FrameIdToString.put("WORS", "URL: Official Internet radio station homepage");
        id3v2_4FrameIdToString.put("WPAY", "URL: Payment");
        id3v2_4FrameIdToString.put("WPUB", "URL: Publishers official webpage");
        id3v2_4FrameIdToString.put("WXXX", "User defined URL link frame");
        for (Map.Entry<String, String> entry : id3v2_4FrameIdToString.entrySet()) {
            id3v2_4FrameStringToId.put(entry.getValue(), entry.getKey());
        }
        lyrics3v2FieldIdToString.put("IND", "Indications field");
        lyrics3v2FieldIdToString.put("LYR", "Lyrics multi line text");
        lyrics3v2FieldIdToString.put("INF", "Additional information multi line text");
        lyrics3v2FieldIdToString.put("AUT", "Lyrics/Music Author name");
        lyrics3v2FieldIdToString.put("EAL", "Extended Album name");
        lyrics3v2FieldIdToString.put("EAR", "Extended Artist name");
        lyrics3v2FieldIdToString.put("ETT", "Extended Track Title");
        lyrics3v2FieldIdToString.put("IMG", "Link to an image files");
        for (Map.Entry<String, String> entry : lyrics3v2FieldIdToString.entrySet()) {
            lyrics3v2FieldStringToId.put(entry.getValue(), entry.getKey());
        }
        id3v2_2ToId3v2_3.put("BUF", "RBUF");
        id3v2_2ToId3v2_3.put("CNT", "PCNT");
        id3v2_2ToId3v2_3.put("COM", "COMM");
        id3v2_2ToId3v2_3.put("CRA", "AENC");

        //id3v2_2ToId3v2_4.put("CRM", "CRM"); // removed in ID3v2.3
        id3v2_2ToId3v2_3.put("ETC", "ETCO");
        id3v2_2ToId3v2_3.put("EQU", "EQUA"); // changed from EQUA to EQU2 in

        // ID3v2.4
        id3v2_2ToId3v2_3.put("GEO", "GEOB"); // Deprecated in ID3v2.4
        id3v2_2ToId3v2_3.put("IPL", "IPLS");
        id3v2_2ToId3v2_3.put("LNK", "LINK");
        id3v2_2ToId3v2_3.put("MCI", "MCDI");
        id3v2_2ToId3v2_3.put("MLL", "MLLT");
        id3v2_2ToId3v2_3.put("PIC", "APIC"); // the APIC spec is different from

        // PIC
        id3v2_2ToId3v2_3.put("POP", "POPM");
        id3v2_2ToId3v2_3.put("REV", "RVRB");
        id3v2_2ToId3v2_3.put("RVA", "RVAD"); // changed from RVAD to RVA2 in

        // ID3v2.4
        id3v2_2ToId3v2_3.put("SLT", "SYLT");
        id3v2_2ToId3v2_3.put("STC", "SYTC");
        id3v2_2ToId3v2_3.put("TAL", "TALB");
        id3v2_2ToId3v2_3.put("TBP", "TBPM");
        id3v2_2ToId3v2_3.put("TCM", "TCOM");
        id3v2_2ToId3v2_3.put("TCO", "TCON");
        id3v2_2ToId3v2_3.put("TCR", "TCOP");
        id3v2_2ToId3v2_3.put("TDA", "TDAT"); // Deprecated in ID3v2.4
        id3v2_2ToId3v2_3.put("TDY", "TDLY");
        id3v2_2ToId3v2_3.put("TEN", "TENC");
        id3v2_2ToId3v2_3.put("TFT", "TFLT");
        id3v2_2ToId3v2_3.put("TIM", "TIME"); // Deprecated in ID3v2.4
        id3v2_2ToId3v2_3.put("TKE", "TKEY");
        id3v2_2ToId3v2_3.put("TLA", "TLAN");
        id3v2_2ToId3v2_3.put("TLE", "TLEN");
        id3v2_2ToId3v2_3.put("TMT", "TMED");
        id3v2_2ToId3v2_3.put("TOA", "TOPE");
        id3v2_2ToId3v2_3.put("TOF", "TOFN");
        id3v2_2ToId3v2_3.put("TOL", "TOLY");
        id3v2_2ToId3v2_3.put("TOR", "TORY"); // Deprecated in ID3v2.4
        id3v2_2ToId3v2_3.put("TOT", "TOAL");
        id3v2_2ToId3v2_3.put("TP1", "TPE1");
        id3v2_2ToId3v2_3.put("TP2", "TPE2");
        id3v2_2ToId3v2_3.put("TP3", "TPE3");
        id3v2_2ToId3v2_3.put("TP4", "TPE4");
        id3v2_2ToId3v2_3.put("TPA", "TPOS");
        id3v2_2ToId3v2_3.put("TPB", "TPUB");
        id3v2_2ToId3v2_3.put("TRC", "TSRC");
        id3v2_2ToId3v2_3.put("TRD", "TRDA"); // Deprecated in ID3v2.4
        id3v2_2ToId3v2_3.put("TRK", "TRCK");
        id3v2_2ToId3v2_3.put("TSI", "TSIZ"); // Deprecated in ID3v2.4
        id3v2_2ToId3v2_3.put("TSS", "TSSE");
        id3v2_2ToId3v2_3.put("TT1", "TIT1");
        id3v2_2ToId3v2_3.put("TT2", "TIT2");
        id3v2_2ToId3v2_3.put("TT3", "TIT3");
        id3v2_2ToId3v2_3.put("TXT", "TEXT");
        id3v2_2ToId3v2_3.put("TXX", "TXXX");
        id3v2_2ToId3v2_3.put("TYE", "TYER"); // Deprecated in ID3v2.4
        id3v2_2ToId3v2_3.put("UFI", "UFID");
        id3v2_2ToId3v2_3.put("ULT", "USLT");
        id3v2_2ToId3v2_3.put("WAF", "WOAF");
        id3v2_2ToId3v2_3.put("WAR", "WOAR");
        id3v2_2ToId3v2_3.put("WAS", "WOAS");
        id3v2_2ToId3v2_3.put("WCM", "WCOM");
        id3v2_2ToId3v2_3.put("WCP", "WCOP");
        id3v2_2ToId3v2_3.put("WPB", "WPUB");
        id3v2_2ToId3v2_3.put("WXX", "WXXX");
        for (Map.Entry<String, String> entry : id3v2_2ToId3v2_3.entrySet()) {
            id3v2_3ToId3v2_2.put(entry.getValue(), entry.getKey());
        }
        id3v2_3ToId3v2_4.put("EQUA", "EQU2"); // changed from EQUA to EQU2 in

        // ID3v2.4
        id3v2_3ToId3v2_4.put("GEOB", null); // Deprecated in ID3v2.4
        id3v2_3ToId3v2_4.put("RVAD", "RVA2"); // changed from RVAD to RVA2 in

        // ID3v2.4
        id3v2_3ToId3v2_4.put("TDAT", null); // Deprecated in ID3v2.4
        id3v2_3ToId3v2_4.put("TIME", null); // Deprecated in ID3v2.4
        id3v2_3ToId3v2_4.put("TORY", null); // Deprecated in ID3v2.4
        id3v2_3ToId3v2_4.put("TRDA", null); // Deprecated in ID3v2.4
        id3v2_3ToId3v2_4.put("TSIZ", null); // Deprecated in ID3v2.4
        id3v2_3ToId3v2_4.put("TYER", null); // Deprecated in ID3v2.4
        for (Map.Entry<String, String> entry : id3v2_3ToId3v2_4.entrySet()) {
            id3v2_4ToId3v2_3.put(entry.getValue(), entry.getKey());
        }
        genreIdToString.put(0L, "Blues");
        genreIdToString.put(1L, "Classic Rock");
        genreIdToString.put(2L, "Country");
        genreIdToString.put(3L, "Dance");
        genreIdToString.put(4L, "Disco");
        genreIdToString.put(5L, "Funk");
        genreIdToString.put(6L, "Grunge");
        genreIdToString.put(7L, "Hip-Hop");
        genreIdToString.put(8L, "Jazz");
        genreIdToString.put(9L, "Metal");
        genreIdToString.put(10L, "New Age");
        genreIdToString.put(11L, "Oldies");
        genreIdToString.put(12L, "Other");
        genreIdToString.put(13L, "Pop");
        genreIdToString.put(14L, "R&B");
        genreIdToString.put(15L, "Rap");
        genreIdToString.put(16L, "Reggae");
        genreIdToString.put(17L, "Rock");
        genreIdToString.put(18L, "Techno");
        genreIdToString.put(19L, "Industrial");
        genreIdToString.put(20L, "Alternative");
        genreIdToString.put(21L, "Ska");
        genreIdToString.put(22L, "Death Metal");
        genreIdToString.put(23L, "Pranks");
        genreIdToString.put(24L, "Soundtrack");
        genreIdToString.put(25L, "Euro-Techno");
        genreIdToString.put(26L, "Ambient");
        genreIdToString.put(27L, "Trip-Hop");
        genreIdToString.put(28L, "Vocal");
        genreIdToString.put(29L, "Jazz+Funk");
        genreIdToString.put(30L, "Fusion");
        genreIdToString.put(31L, "Trance");
        genreIdToString.put(32L, "Classical");
        genreIdToString.put(33L, "Instrumental");
        genreIdToString.put(34L, "Acid");
        genreIdToString.put(35L, "House");
        genreIdToString.put(36L, "Game");
        genreIdToString.put(37L, "Sound Clip");
        genreIdToString.put(38L, "Gospel");
        genreIdToString.put(39L, "Noise");
        genreIdToString.put(40L, "AlternRock");
        genreIdToString.put(41L, "Bass");
        genreIdToString.put(42L, "Soul");
        genreIdToString.put(43L, "Punk");
        genreIdToString.put(44L, "Space");
        genreIdToString.put(45L, "Meditative");
        genreIdToString.put(46L, "Instrumental Pop");
        genreIdToString.put(47L, "Instrumental Rock");
        genreIdToString.put(48L, "Ethnic");
        genreIdToString.put(49L, "Gothic");
        genreIdToString.put(50L, "Darkwave");
        genreIdToString.put(51L, "Techno-Industrial");
        genreIdToString.put(52L, "Electronic");
        genreIdToString.put(53L, "Pop-Folk");
        genreIdToString.put(54L, "Eurodance");
        genreIdToString.put(55L, "Dream");
        genreIdToString.put(56L, "Southern Rock");
        genreIdToString.put(57L, "Comedy");
        genreIdToString.put(58L, "Cult");
        genreIdToString.put(59L, "Gangsta");
        genreIdToString.put(60L, "Top 40");
        genreIdToString.put(61L, "Christian Rap");
        genreIdToString.put(62L, "Pop/Funk");
        genreIdToString.put(63L, "Jungle");
        genreIdToString.put(64L, "Native American");
        genreIdToString.put(65L, "Cabaret");
        genreIdToString.put(66L, "New Wave");
        genreIdToString.put(67L, "Psychadelic");
        genreIdToString.put(68L, "Rave");
        genreIdToString.put(69L, "Showtunes");
        genreIdToString.put(70L, "Trailer");
        genreIdToString.put(71L, "Lo-Fi");
        genreIdToString.put(72L, "Tribal");
        genreIdToString.put(73L, "Acid Punk");
        genreIdToString.put(74L, "Acid Jazz");
        genreIdToString.put(75L, "Polka");
        genreIdToString.put(76L, "Retro");
        genreIdToString.put(77L, "Musical");
        genreIdToString.put(78L, "Rock & Roll");
        genreIdToString.put(79L, "Hard Rock");
        genreIdToString.put(80L, "Folk");
        genreIdToString.put(81L, "Folk-Rock");
        genreIdToString.put(82L, "National Folk");
        genreIdToString.put(83L, "Swing");
        genreIdToString.put(84L, "Fast Fusion");
        genreIdToString.put(85L, "Bebob");
        genreIdToString.put(86L, "Latin");
        genreIdToString.put(87L, "Revival");
        genreIdToString.put(88L, "Celtic");
        genreIdToString.put(89L, "Bluegrass");
        genreIdToString.put(90L, "Avantgarde");
        genreIdToString.put(91L, "Gothic Rock");
        genreIdToString.put(92L, "Progressive Rock");
        genreIdToString.put(93L, "Psychedelic Rock");
        genreIdToString.put(94L, "Symphonic Rock");
        genreIdToString.put(95L, "Slow Rock");
        genreIdToString.put(96L, "Big Band");
        genreIdToString.put(97L, "Chorus");
        genreIdToString.put(98L, "Easy Listening");
        genreIdToString.put(99L, "Acoustic");
        genreIdToString.put(100L, "Humour");
        genreIdToString.put(101L, "Speech");
        genreIdToString.put(102L, "Chanson");
        genreIdToString.put(103L, "Opera");
        genreIdToString.put(104L, "Chamber Music");
        genreIdToString.put(105L, "Sonata");
        genreIdToString.put(106L, "Symphony");
        genreIdToString.put(107L, "Booty Bass");
        genreIdToString.put(108L, "Primus");
        genreIdToString.put(109L, "Porn Groove");
        genreIdToString.put(110L, "Satire");
        genreIdToString.put(111L, "Slow Jam");
        genreIdToString.put(112L, "Club");
        genreIdToString.put(113L, "Tango");
        genreIdToString.put(114L, "Samba");
        genreIdToString.put(115L, "Folklore");
        genreIdToString.put(116L, "Ballad");
        genreIdToString.put(117L, "Power Ballad");
        genreIdToString.put(118L, "Rhythmic Soul");
        genreIdToString.put(119L, "Freestyle");
        genreIdToString.put(120L, "Duet");
        genreIdToString.put(121L, "Punk Rock");
        genreIdToString.put(122L, "Drum Solo");
        genreIdToString.put(123L, "Acapella");
        genreIdToString.put(124L, "Euro-House");
        genreIdToString.put(125L, "Dance Hall");

        // NoteStream custom
        genreIdToString.put(126L, "Romantic");
        genreIdToString.put(127L, "K-Pop");
        genreIdToString.put(128L, "Indie");
        genreIdToString.put(129L, "Hippie");
        genreIdToString.put(130L, "Excercise Hall");
        genreIdToString.put(131L, "Festive");
        genreIdToString.put(132L, "Children's Music");

        // ID's are typed as Integer because the combo box expects it
        for (Map.Entry<Long, String> entry : genreIdToString.entrySet()) {
            genreStringToId.put(entry.getValue(), entry.getKey());
        }

        // MPEG-1, Layer I (E)
        bitrate.put(0x1EL, 32L);
        bitrate.put(0x2EL, 64L);
        bitrate.put(0x3EL, 96L);
        bitrate.put(0x4EL, 128L);
        bitrate.put(0x5EL, 160L);
        bitrate.put(0x6EL, 192L);
        bitrate.put(0x7EL, 224L);
        bitrate.put(0x8EL, 256L);
        bitrate.put(0x9EL, 288L);
        bitrate.put(0xAEL, 320L);
        bitrate.put(0xBEL, 352L);
        bitrate.put(0xCEL, 384L);
        bitrate.put(0xDEL, 416L);
        bitrate.put(0xEEL, 448L);

        // MPEG-1, Layer II (C)
        bitrate.put(0x1CL, 32L);
        bitrate.put(0x2CL, 48L);
        bitrate.put(0x3CL, 56L);
        bitrate.put(0x4CL, 64L);
        bitrate.put(0x5CL, 80L);
        bitrate.put(0x6CL, 96L);
        bitrate.put(0x7CL, 112L);
        bitrate.put(0x8CL, 128L);
        bitrate.put(0x9CL, 160L);
        bitrate.put(0xACL, 192L);
        bitrate.put(0xBCL, 224L);
        bitrate.put(0xCCL, 256L);
        bitrate.put(0xDCL, 320L);
        bitrate.put(0xECL, 384L);

        // MPEG-1, Layer III (A)
        bitrate.put(0x1AL, 32L);
        bitrate.put(0x2AL, 40L);
        bitrate.put(0x3AL, 48L);
        bitrate.put(0x4AL, 56L);
        bitrate.put(0x5AL, 64L);
        bitrate.put(0x6AL, 80L);
        bitrate.put(0x7AL, 96L);
        bitrate.put(0x8AL, 112L);
        bitrate.put(0x9AL, 128L);
        bitrate.put(0xAAL, 160L);
        bitrate.put(0xBAL, 192L);
        bitrate.put(0xCAL, 224L);
        bitrate.put(0xDAL, 256L);
        bitrate.put(0xEAL, 320L);

        // MPEG-2, Layer I (6)
        bitrate.put(0x16L, 32L);
        bitrate.put(0x26L, 48L);
        bitrate.put(0x36L, 56L);
        bitrate.put(0x46L, 64L);
        bitrate.put(0x56L, 80L);
        bitrate.put(0x66L, 96L);
        bitrate.put(0x76L, 112L);
        bitrate.put(0x86L, 128L);
        bitrate.put(0x96L, 144L);
        bitrate.put(0xA6L, 160L);
        bitrate.put(0xB6L, 176L);
        bitrate.put(0xC6L, 192L);
        bitrate.put(0xD6L, 224L);
        bitrate.put(0xE6L, 256L);

        // MPEG-2, Layer II (4)
        bitrate.put(0x14L, 8L);
        bitrate.put(0x24L, 16L);
        bitrate.put(0x34L, 24L);
        bitrate.put(0x44L, 32L);
        bitrate.put(0x54L, 40L);
        bitrate.put(0x64L, 48L);
        bitrate.put(0x74L, 56L);
        bitrate.put(0x84L, 64L);
        bitrate.put(0x94L, 80L);
        bitrate.put(0xA4L, 96L);
        bitrate.put(0xB4L, 112L);
        bitrate.put(0xC4L, 128L);
        bitrate.put(0xD4L, 144L);
        bitrate.put(0xE4L, 160L);

        // MPEG-2, Layer III (2)
        bitrate.put(0x12L, 8L);
        bitrate.put(0x22L, 16L);
        bitrate.put(0x32L, 24L);
        bitrate.put(0x42L, 32L);
        bitrate.put(0x52L, 40L);
        bitrate.put(0x62L, 48L);
        bitrate.put(0x72L, 56L);
        bitrate.put(0x82L, 64L);
        bitrate.put(0x92L, 80L);
        bitrate.put(0xA2L, 96L);
        bitrate.put(0xB2L, 112L);
        bitrate.put(0xC2L, 128L);
        bitrate.put(0xD2L, 144L);
        bitrate.put(0xE2L, 160L);
        languageIdToString.put("aar", "Afar");
        languageIdToString.put("abk", "Abkhazian");
        languageIdToString.put("ace", "Achinese");
        languageIdToString.put("ach", "Acoli");
        languageIdToString.put("ada", "Adangme");
        languageIdToString.put("afa", "Afro-Asiatic (Other)");
        languageIdToString.put("afh", "Afrihili");
        languageIdToString.put("afr", "Afrikaans");
        languageIdToString.put("aka", "Akan");
        languageIdToString.put("akk", "Akkadian");
        languageIdToString.put("alb", "Albanian");
        languageIdToString.put("ale", "Aleut");
        languageIdToString.put("alg", "Algonquian languages");
        languageIdToString.put("amh", "Amharic");
        languageIdToString.put("ang", "English, Old (ca.450-1100)");
        languageIdToString.put("apa", "Apache languages");
        languageIdToString.put("ara", "Arabic");
        languageIdToString.put("arc", "Aramaic");
        languageIdToString.put("arm", "Armenian");
        languageIdToString.put("arn", "Araucanian");
        languageIdToString.put("arp", "Arapaho");
        languageIdToString.put("art", "Artificial (Other)");
        languageIdToString.put("arw", "Arawak");
        languageIdToString.put("asm", "Assamese");
        languageIdToString.put("ast", "Asturian; Bable");
        languageIdToString.put("ath", "Athapascan languages");
        languageIdToString.put("aus", "Australian languages");
        languageIdToString.put("ava", "Avaric");
        languageIdToString.put("ave", "Avestan");
        languageIdToString.put("awa", "Awadhi");
        languageIdToString.put("aym", "Aymara");
        languageIdToString.put("aze", "Azerbaijani");
        languageIdToString.put("bad", "Banda");
        languageIdToString.put("bai", "Bamileke languages");
        languageIdToString.put("bak", "Bashkir");
        languageIdToString.put("bal", "Baluchi");
        languageIdToString.put("bam", "Bambara");
        languageIdToString.put("ban", "Balinese");
        languageIdToString.put("baq", "Basque");
        languageIdToString.put("bas", "Basa");
        languageIdToString.put("bat", "Baltic (Other)");
        languageIdToString.put("bej", "Beja");
        languageIdToString.put("bel", "Belarusian");
        languageIdToString.put("bem", "Bemba");
        languageIdToString.put("ben", "Bengali");
        languageIdToString.put("ber", "Berber (Other)");
        languageIdToString.put("bho", "Bhojpuri");
        languageIdToString.put("bih", "Bihari");
        languageIdToString.put("bik", "Bikol");
        languageIdToString.put("bin", "Bini");
        languageIdToString.put("bis", "Bislama");
        languageIdToString.put("bla", "Siksika");
        languageIdToString.put("bnt", "Bantu (Other)");
        languageIdToString.put("bod", "Tibetan");
        languageIdToString.put("bos", "Bosnian");
        languageIdToString.put("bra", "Braj");
        languageIdToString.put("bre", "Breton");
        languageIdToString.put("btk", "Batak (Indonesia)");
        languageIdToString.put("bua", "Buriat");
        languageIdToString.put("bug", "Buginese");
        languageIdToString.put("bul", "Bulgarian");
        languageIdToString.put("bur", "Burmese");
        languageIdToString.put("cad", "Caddo");
        languageIdToString.put("cai", "Central American Indian (Other)");
        languageIdToString.put("car", "Carib");
        languageIdToString.put("cat", "Catalan");
        languageIdToString.put("cau", "Caucasian (Other)");
        languageIdToString.put("ceb", "Cebuano");
        languageIdToString.put("cel", "Celtic (Other)");
        languageIdToString.put("ces", "Czech");
        languageIdToString.put("cha", "Chamorro");
        languageIdToString.put("chb", "Chibcha");
        languageIdToString.put("che", "Chechen");
        languageIdToString.put("chg", "Chagatai");
        languageIdToString.put("chi", "Chinese");
        languageIdToString.put("chk", "Chuukese");
        languageIdToString.put("chm", "Mari");
        languageIdToString.put("chn", "Chinook jargon");
        languageIdToString.put("cho", "Choctaw");
        languageIdToString.put("chp", "Chipewyan");
        languageIdToString.put("chr", "Cherokee");
        languageIdToString.put("chu",
                               "Church Slavic; Old Slavonic; Old Church Slavonic; Church Slavonic; Old Bulgarian");
        languageIdToString.put("chv", "Chuvash");
        languageIdToString.put("chy", "Cheyenne");
        languageIdToString.put("cmc", "Chamic languages");
        languageIdToString.put("cop", "Coptic");
        languageIdToString.put("cor", "Cornish");
        languageIdToString.put("cos", "Corsican");
        languageIdToString.put("cpe", "Creoles and pidgins, English based (Other)");
        languageIdToString.put("cpf", "Creoles and pidgins, French-based (Other)");
        languageIdToString.put("cpp", "Creoles and pidgins,");
        languageIdToString.put("cre", "Cree");
        languageIdToString.put("crp", "Creoles and pidgins (Other)");
        languageIdToString.put("cus", "Cushitic (Other)");
        languageIdToString.put("cym", "Welsh");
        languageIdToString.put("cze", "Czech");
        languageIdToString.put("dak", "Dakota");
        languageIdToString.put("dan", "Danish");
        languageIdToString.put("day", "Dayak");
        languageIdToString.put("del", "Delaware");
        languageIdToString.put("den", "Slave (Athapascan)");
        languageIdToString.put("deu", "German");
        languageIdToString.put("dgr", "Dogrib");
        languageIdToString.put("din", "Dinka");
        languageIdToString.put("div", "Divehi");
        languageIdToString.put("doi", "Dogri");
        languageIdToString.put("dra", "Dravidian (Other)");
        languageIdToString.put("dua", "Duala");
        languageIdToString.put("dum", "Dutch, Middle (ca.1050-1350)");
        languageIdToString.put("dut", "Dutch");
        languageIdToString.put("dyu", "Dyula");
        languageIdToString.put("dzo", "Dzongkha");
        languageIdToString.put("efi", "Efik");
        languageIdToString.put("egy", "Egyptian (Ancient)");
        languageIdToString.put("eka", "Ekajuk");
        languageIdToString.put("ell", "Greek, Modern (1453-)");
        languageIdToString.put("elx", "Elamite");
        languageIdToString.put("eng", "English");
        languageIdToString.put("enm", "English, Middle (1100-1500)");
        languageIdToString.put("epo", "Esperanto");
        languageIdToString.put("est", "Estonian");
        languageIdToString.put("eus", "Basque");
        languageIdToString.put("ewe", "Ewe");
        languageIdToString.put("ewo", "Ewondo");
        languageIdToString.put("fan", "Fang");
        languageIdToString.put("fao", "Faroese");
        languageIdToString.put("fas", "Persian");
        languageIdToString.put("fat", "Fanti");
        languageIdToString.put("fij", "Fijian");
        languageIdToString.put("fin", "Finnish");
        languageIdToString.put("fiu", "Finno-Ugrian (Other)");
        languageIdToString.put("fon", "Fon");
        languageIdToString.put("fra", "French");
        languageIdToString.put("frm", "French, Middle (ca.1400-1800)");
        languageIdToString.put("fro", "French, Old (842-ca.1400)");
        languageIdToString.put("fry", "Frisian");
        languageIdToString.put("ful", "Fulah");
        languageIdToString.put("fur", "Friulian");
        languageIdToString.put("gaa", "Ga");
        languageIdToString.put("gay", "Gayo");
        languageIdToString.put("gba", "Gbaya");
        languageIdToString.put("gem", "Germanic (Other)");
        languageIdToString.put("geo", "Georgian");
        languageIdToString.put("ger", "German");
        languageIdToString.put("gez", "Geez");
        languageIdToString.put("gil", "Gilbertese");
        languageIdToString.put("gla", "Gaelic; Scottish Gaelic");
        languageIdToString.put("gle", "Irish");
        languageIdToString.put("glg", "Gallegan");
        languageIdToString.put("glv", "Manx");
        languageIdToString.put("gmh", "German, Middle High (ca.1050-1500)");
        languageIdToString.put("goh", "German, Old High (ca.750-1050)");
        languageIdToString.put("gon", "Gondi");
        languageIdToString.put("gor", "Gorontalo");
        languageIdToString.put("got", "Gothic");
        languageIdToString.put("grb", "Grebo");
        languageIdToString.put("grc", "Greek, Ancient (to 1453)");
        languageIdToString.put("gre", "Greek, Modern (1453-)");
        languageIdToString.put("grn", "Guarani");
        languageIdToString.put("guj", "Gujarati");
        languageIdToString.put("gwi", "Gwich�in");
        languageIdToString.put("hai", "Haida");
        languageIdToString.put("hau", "Hausa");
        languageIdToString.put("haw", "Hawaiian");
        languageIdToString.put("heb", "Hebrew");
        languageIdToString.put("her", "Herero");
        languageIdToString.put("hil", "Hiligaynon");
        languageIdToString.put("him", "Himachali");
        languageIdToString.put("hin", "Hindi");
        languageIdToString.put("hit", "Hittite");
        languageIdToString.put("hmn", "Hmong");
        languageIdToString.put("hmo", "Hiri Motu");
        languageIdToString.put("hrv", "Croatian");
        languageIdToString.put("hun", "Hungarian");
        languageIdToString.put("hup", "Hupa");
        languageIdToString.put("hye", "Armenian");
        languageIdToString.put("iba", "Iban");
        languageIdToString.put("ibo", "Igbo");
        languageIdToString.put("ice", "Icelandic");
        languageIdToString.put("ido", "Ido");
        languageIdToString.put("ijo", "Ijo");
        languageIdToString.put("iku", "Inuktitut");
        languageIdToString.put("ile", "Interlingue");
        languageIdToString.put("ilo", "Iloko");
        languageIdToString.put("ina", "Interlingua (International Auxiliary)");
        languageIdToString.put("inc", "Indic (Other)");
        languageIdToString.put("ind", "Indonesian");
        languageIdToString.put("ine", "Indo-European (Other)");
        languageIdToString.put("ipk", "Inupiaq");
        languageIdToString.put("ira", "Iranian (Other)");
        languageIdToString.put("iro", "Iroquoian languages");
        languageIdToString.put("isl", "Icelandic");
        languageIdToString.put("ita", "Italian");
        languageIdToString.put("jav", "Javanese");
        languageIdToString.put("jpn", "Japanese");
        languageIdToString.put("jpr", "Judeo-Persian");
        languageIdToString.put("jrb", "Judeo-Arabic");
        languageIdToString.put("kaa", "Kara-Kalpak");
        languageIdToString.put("kab", "Kabyle");
        languageIdToString.put("kac", "Kachin");
        languageIdToString.put("kal", "Kalaallisut");
        languageIdToString.put("kam", "Kamba");
        languageIdToString.put("kan", "Kannada");
        languageIdToString.put("kar", "Karen");
        languageIdToString.put("kas", "Kashmiri");
        languageIdToString.put("kat", "Georgian");
        languageIdToString.put("kau", "Kanuri");
        languageIdToString.put("kaw", "Kawi");
        languageIdToString.put("kaz", "Kazakh");
        languageIdToString.put("kha", "Khasi");
        languageIdToString.put("khi", "Khoisan (Other)");
        languageIdToString.put("khm", "Khmer");
        languageIdToString.put("kho", "Khotanese");
        languageIdToString.put("kik", "Kikuyu; Gikuyu");
        languageIdToString.put("kin", "Kinyarwanda");
        languageIdToString.put("kir", "Kirghiz");
        languageIdToString.put("kmb", "Kimbundu");
        languageIdToString.put("kok", "Konkani");
        languageIdToString.put("kom", "Komi");
        languageIdToString.put("kon", "Kongo");
        languageIdToString.put("kor", "Korean");
        languageIdToString.put("kos", "Kosraean");
        languageIdToString.put("kpe", "Kpelle");
        languageIdToString.put("kro", "Kru");
        languageIdToString.put("kru", "Kurukh");
        languageIdToString.put("kua", "Kuanyama; Kwanyama");
        languageIdToString.put("kum", "Kumyk");
        languageIdToString.put("kur", "Kurdish");
        languageIdToString.put("kut", "Kutenai");
        languageIdToString.put("lad", "Ladino");
        languageIdToString.put("lah", "Lahnda");
        languageIdToString.put("lam", "Lamba");
        languageIdToString.put("lao", "Lao");
        languageIdToString.put("lat", "Latin");
        languageIdToString.put("lav", "Latvian");
        languageIdToString.put("lez", "Lezghian");
        languageIdToString.put("lin", "Lingala");
        languageIdToString.put("lit", "Lithuanian");
        languageIdToString.put("lol", "Mongo");
        languageIdToString.put("loz", "Lozi");
        languageIdToString.put("ltz", "Luxembourgish; Letzeburgesch");
        languageIdToString.put("lua", "Luba-Lulua");
        languageIdToString.put("lub", "Luba-Katanga");
        languageIdToString.put("lug", "Ganda");
        languageIdToString.put("lui", "Luiseno");
        languageIdToString.put("lun", "Lunda");
        languageIdToString.put("luo", "Luo (Kenya and Tanzania)");
        languageIdToString.put("lus", "lushai");
        languageIdToString.put("mac", "Macedonian");
        languageIdToString.put("mad", "Madurese");
        languageIdToString.put("mag", "Magahi");
        languageIdToString.put("mah", "Marshallese");
        languageIdToString.put("mai", "Maithili");
        languageIdToString.put("mak", "Makasar");
        languageIdToString.put("mal", "Malayalam");
        languageIdToString.put("man", "Mandingo");
        languageIdToString.put("mao", "Maori");
        languageIdToString.put("map", "Austronesian (Other)");
        languageIdToString.put("mar", "Marathi");
        languageIdToString.put("mas", "Masai");
        languageIdToString.put("may", "Malay");
        languageIdToString.put("mdr", "Mandar");
        languageIdToString.put("men", "Mende");
        languageIdToString.put("mga", "Irish, Middle (900-1200)");
        languageIdToString.put("mic", "Micmac");
        languageIdToString.put("min", "Minangkabau");
        languageIdToString.put("mis", "Miscellaneous languages");
        languageIdToString.put("mkd", "Macedonian");
        languageIdToString.put("mkh", "Mon-Khmer (Other)");
        languageIdToString.put("mlg", "Malagasy");
        languageIdToString.put("mlt", "Maltese");
        languageIdToString.put("mnc", "Manchu");
        languageIdToString.put("mni", "Manipuri");
        languageIdToString.put("mno", "Manobo languages");
        languageIdToString.put("moh", "Mohawk");
        languageIdToString.put("mol", "Moldavian");
        languageIdToString.put("mon", "Mongolian");
        languageIdToString.put("mos", "Mossi");
        languageIdToString.put("mri", "Maori");
        languageIdToString.put("msa", "Malay");
        languageIdToString.put("mul", "Multiple languages");
        languageIdToString.put("mun", "Munda languages");
        languageIdToString.put("mus", "Creek");
        languageIdToString.put("mwr", "Marwari");
        languageIdToString.put("mya", "Burmese");
        languageIdToString.put("myn", "Mayan languages");
        languageIdToString.put("nah", "Nahuatl");
        languageIdToString.put("nai", "North American Indian");
        languageIdToString.put("nau", "Nauru");
        languageIdToString.put("nav", "Navajo; Navaho");
        languageIdToString.put("nbl", "South Ndebele");
        languageIdToString.put("nde", "North Ndebele");
        languageIdToString.put("ndo", "Ndonga");
        languageIdToString.put("nds", "Low German; Low Saxon; German, Low; Saxon, Low");
        languageIdToString.put("nep", "Nepali");
        languageIdToString.put("new", "Newari");
        languageIdToString.put("nia", "Nias");
        languageIdToString.put("nic", "Niger-Kordofanian (Other)");
        languageIdToString.put("niu", "Niuean");
        languageIdToString.put("nld", "Dutch");
        languageIdToString.put("nno", "Norwegian Nynorsk");
        languageIdToString.put("nob", "Norwegian Bokm�l");
        languageIdToString.put("non", "Norse, Old");
        languageIdToString.put("nor", "Norwegian");
        languageIdToString.put("nso", "Sotho, Northern");
        languageIdToString.put("nub", "Nubian languages");
        languageIdToString.put("nya", "Chichewa; Chewa; Nyanja");
        languageIdToString.put("nym", "Nyamwezi");
        languageIdToString.put("nyn", "Nyankole");
        languageIdToString.put("nyo", "Nyoro");
        languageIdToString.put("nzi", "Nzima");
        languageIdToString.put("oci", "Occitan (post 1500); Proven�al");
        languageIdToString.put("oji", "Ojibwa");
        languageIdToString.put("ori", "Oriya");
        languageIdToString.put("orm", "Oromo");
        languageIdToString.put("osa", "Osage");
        languageIdToString.put("oss", "Ossetian; Ossetic");
        languageIdToString.put("ota", "Turkish, Ottoman (1500-1928)");
        languageIdToString.put("oto", "Otomian languages");
        languageIdToString.put("paa", "Papuan (Other)");
        languageIdToString.put("pag", "Pangasinan");
        languageIdToString.put("pal", "Pahlavi");
        languageIdToString.put("pam", "Pampanga");
        languageIdToString.put("pan", "Panjabi");
        languageIdToString.put("pap", "Papiamento");
        languageIdToString.put("pau", "Palauan");
        languageIdToString.put("peo", "Persian, Old (ca.600-400 B.C.)");
        languageIdToString.put("per", "Persian");
        languageIdToString.put("per", "Persian");
        languageIdToString.put("phi", "Philippine (Other)");
        languageIdToString.put("phn", "Phoenician");
        languageIdToString.put("pli", "Pali");
        languageIdToString.put("pol", "Polish");
        languageIdToString.put("pon", "Pohnpeian");
        languageIdToString.put("por", "Portuguese");
        languageIdToString.put("pra", "Prakrit languages");
        languageIdToString.put("pro", "Proven�al, Old (to 1500)");
        languageIdToString.put("pus", "Pushto");
        languageIdToString.put("que", "Quechua");
        languageIdToString.put("raj", "Rajasthani");
        languageIdToString.put("rap", "Rapanui");
        languageIdToString.put("rar", "Rarotongan");
        languageIdToString.put("roa", "Romance (Other)");
        languageIdToString.put("roh", "Raeto-Romance");
        languageIdToString.put("rom", "Romany");
        languageIdToString.put("ron", "Romanian");
        languageIdToString.put("rum", "Romanian");
        languageIdToString.put("run", "Rundi");
        languageIdToString.put("rus", "Russian");
        languageIdToString.put("sad", "Sandawe");
        languageIdToString.put("sag", "Sango");
        languageIdToString.put("sah", "Yakut");
        languageIdToString.put("sai", "South American Indian (Other)");
        languageIdToString.put("sal", "Salishan languages");
        languageIdToString.put("sam", "Samaritan Aramaic");
        languageIdToString.put("san", "Sanskrit");
        languageIdToString.put("sas", "Sasak");
        languageIdToString.put("sat", "Santali");
        languageIdToString.put("scc", "Serbian");
        languageIdToString.put("sco", "Scots");
        languageIdToString.put("scr", "Croatian");
        languageIdToString.put("sel", "Selkup");
        languageIdToString.put("sem", "Semitic (Other)");
        languageIdToString.put("sga", "Irish, Old (to 900)");
        languageIdToString.put("sgn", "Sign languages");
        languageIdToString.put("shn", "Shan");
        languageIdToString.put("sid", "Sidamo");
        languageIdToString.put("sin", "Sinhales");
        languageIdToString.put("sio", "Siouan languages");
        languageIdToString.put("sit", "Sino-Tibetan (Other)");
        languageIdToString.put("sla", "Slavic (Other)");
        languageIdToString.put("slk", "Slovak");
        languageIdToString.put("slo", "Slovak");
        languageIdToString.put("slv", "Slovenian");
        languageIdToString.put("sma", "Southern Sami");
        languageIdToString.put("sme", "Northern Sami");
        languageIdToString.put("smi", "Sami languages (Other)");
        languageIdToString.put("smj", "Lule Sami");
        languageIdToString.put("smn", "Inari Sami");
        languageIdToString.put("smo", "Samoan");
        languageIdToString.put("sms", "Skolt Sami");
        languageIdToString.put("sna", "Shona");
        languageIdToString.put("snd", "Sindhi");
        languageIdToString.put("snk", "Soninke");
        languageIdToString.put("sog", "Sogdian");
        languageIdToString.put("som", "Somali");
        languageIdToString.put("son", "Songhai");
        languageIdToString.put("sot", "Sotho, Southern");
        languageIdToString.put("spa", "Spanish; Castilia");
        languageIdToString.put("sqi", "Albanian");
        languageIdToString.put("srd", "Sardinian");
        languageIdToString.put("srp", "Serbian");
        languageIdToString.put("srr", "Serer");
        languageIdToString.put("ssa", "Nilo-Saharan (Other)");
        languageIdToString.put("sus", "Susu");
        languageIdToString.put("sux", "Sumerian");
        languageIdToString.put("swa", "Swahili");
        languageIdToString.put("swe", "Swedish");
        languageIdToString.put("syr", "Syriac");
        languageIdToString.put("tah", "Tahitian");
        languageIdToString.put("tai", "Tai (Other)");
        languageIdToString.put("tam", "Tamil");
        languageIdToString.put("tat", "Tatar");
        languageIdToString.put("tel", "Telugu");
        languageIdToString.put("tem", "Timne");
        languageIdToString.put("ter", "Tereno");
        languageIdToString.put("tet", "Tetum");
        languageIdToString.put("tgk", "Tajik");
        languageIdToString.put("tgl", "Tagalog");
        languageIdToString.put("tha", "Thai");
        languageIdToString.put("tib", "Tibetan");
        languageIdToString.put("tig", "Tigre");
        languageIdToString.put("tir", "Tigrinya");
        languageIdToString.put("tiv", "Tiv");
        languageIdToString.put("tkl", "Tokelau");
        languageIdToString.put("tli", "Tlingit");
        languageIdToString.put("tmh", "Tamashek");
        languageIdToString.put("tog", "Tonga (Nyasa)");
        languageIdToString.put("ton", "Tonga (Tonga Islands)");
        languageIdToString.put("tpi", "Tok Pisin");
        languageIdToString.put("tsi", "Tsimshian");
        languageIdToString.put("tsn", "Tswana");
        languageIdToString.put("tso", "Tsonga");
        languageIdToString.put("tuk", "Turkmen");
        languageIdToString.put("tum", "Tumbuka");
        languageIdToString.put("tup", "Tupi languages");
        languageIdToString.put("tur", "Turkish");
        languageIdToString.put("tut", "Altaic (Other)");
        languageIdToString.put("tvl", "Tuvalu");
        languageIdToString.put("twi", "Twi");
        languageIdToString.put("tyv", "Tuvinian");
        languageIdToString.put("uga", "Ugaritic");
        languageIdToString.put("uig", "Uighur");
        languageIdToString.put("ukr", "Ukrainian");
        languageIdToString.put("umb", "Umbundu");
        languageIdToString.put("und", "Undetermined");
        languageIdToString.put("urd", "Urdu");
        languageIdToString.put("uzb", "Uzbek");
        languageIdToString.put("vai", "Vai");
        languageIdToString.put("ven", "Venda");
        languageIdToString.put("vie", "Vietnamese");
        languageIdToString.put("vol", "Volap�k");
        languageIdToString.put("vot", "Votic");
        languageIdToString.put("wak", "Wakashan languages");
        languageIdToString.put("wal", "Walamo");
        languageIdToString.put("war", "Waray");
        languageIdToString.put("was", "Washo");
        languageIdToString.put("wel", "Welsh");
        languageIdToString.put("wen", "Sorbian languages");
        languageIdToString.put("wln", "Walloon");
        languageIdToString.put("wol", "Wolof");
        languageIdToString.put("xho", "Xhosa");
        languageIdToString.put("yao", "Yao");
        languageIdToString.put("yap", "Yapese");
        languageIdToString.put("yid", "Yiddish");
        languageIdToString.put("yor", "Yoruba");
        languageIdToString.put("ypk", "Yupik languages");
        languageIdToString.put("zap", "Zapotec");
        languageIdToString.put("zen", "Zenaga");
        languageIdToString.put("zha", "Zhuang; Chuang");
        languageIdToString.put("zho", "Chinese");
        languageIdToString.put("znd", "Zande");
        languageIdToString.put("zul", "Zulu");
        languageIdToString.put("zun", "Zuni");
        for (Map.Entry<String, String> entry : languageIdToString.entrySet()) {
            languageStringToId.put(entry.getValue(), entry.getKey());
        }

        textEncodingIdToString.put(0L, "ISO-8859-1");
        textEncodingIdToString.put(1L, "UTF-16");
        textEncodingIdToString.put(2L, "UTF-16BE");
        textEncodingIdToString.put(3L, "UTF-8");
        for (Map.Entry<Long, String> entry : textEncodingIdToString.entrySet()) {
            textEncodingStringToId.put(entry.getValue(), entry.getKey());
        }

        interpolationMethodIdToString.put(0L, "Band");
        interpolationMethodIdToString.put(1L, "Linear");
        for (Map.Entry<Long, String> entry : interpolationMethodIdToString.entrySet()) {
            interpolationMethodStringToId.put(entry.getValue(), entry.getKey());
        }

        pictureTypeIdToString.put(0L, "Other");
        pictureTypeIdToString.put(1L, "32x32 pixels 'file icon' (PNG only)");
        pictureTypeIdToString.put(2L, "Other file icon");
        pictureTypeIdToString.put(3L, "Cover (front)");
        pictureTypeIdToString.put(4L, "Cover (back)");
        pictureTypeIdToString.put(5L, "Leaflet page");
        pictureTypeIdToString.put(6L, "Media (e.g. label side of CD)");
        pictureTypeIdToString.put(7L, "Lead artist/lead performer/soloist");
        pictureTypeIdToString.put(8L, "Artist/performer");
        pictureTypeIdToString.put(9L, "Conductor");
        pictureTypeIdToString.put(10L, "Band/Orchestra");
        pictureTypeIdToString.put(11L, "Composer");
        pictureTypeIdToString.put(12L, "Lyricist/text writer");
        pictureTypeIdToString.put(13L, "Recording Location");
        pictureTypeIdToString.put(14L, "During recording");
        pictureTypeIdToString.put(15L, "During performance");
        pictureTypeIdToString.put(16L, "Movie/video screen capture");
        pictureTypeIdToString.put(17L, "A bright coloured fish");
        pictureTypeIdToString.put(18L, "Illustration");
        pictureTypeIdToString.put(19L, "Band/artist logotype");
        pictureTypeIdToString.put(20L, "Publisher/Studio logotype");
        for (Map.Entry<Long, String> entry : pictureTypeIdToString.entrySet()) {
            pictureTypeStringToId.put(entry.getValue(), entry.getKey());
        }

        timeStampFormatIdToString.put(1L, "Absolute time using MPEG [MPEG] frames as unit");
        timeStampFormatIdToString.put(2L, "Absolute time using milliseconds as unit");
        for (Map.Entry<Long, String> entry : timeStampFormatIdToString.entrySet()) {
            timeStampFormatStringToId.put(entry.getValue(), entry.getKey());
        }

        typeOfEventIdToString.put(0x00L, "Padding (has no meaning)");
        typeOfEventIdToString.put(0x01L, "End of initial silence");
        typeOfEventIdToString.put(0x02L, "Intro start");
        typeOfEventIdToString.put(0x03L, "Main part start");
        typeOfEventIdToString.put(0x04L, "Outro start");
        typeOfEventIdToString.put(0x05L, "Outro end");
        typeOfEventIdToString.put(0x06L, "Verse start");
        typeOfEventIdToString.put(0x07L, "Refrain start");
        typeOfEventIdToString.put(0x08L, "Interlude start");
        typeOfEventIdToString.put(0x09L, "Theme start");
        typeOfEventIdToString.put(0x0AL, "Variation start");
        typeOfEventIdToString.put(0x0BL, "Key change");
        typeOfEventIdToString.put(0x0CL, "Time change");
        typeOfEventIdToString.put(0x0DL, "Momentary unwanted noise (Snap, Crackle & Pop)");
        typeOfEventIdToString.put(0x0EL, "Sustained noise");
        typeOfEventIdToString.put(0x0FL, "Sustained noise end");
        typeOfEventIdToString.put(0x10L, "Intro end");
        typeOfEventIdToString.put(0x11L, "Main part end");
        typeOfEventIdToString.put(0x12L, "Verse end");
        typeOfEventIdToString.put(0x13L, "Refrain end");
        typeOfEventIdToString.put(0x14L, "Theme end");
        typeOfEventIdToString.put(0x15L, "Profanity");
        typeOfEventIdToString.put(0x16L, "Profanity end");
        typeOfEventIdToString.put(0xFDL, "Audio end (start of silence)");
        typeOfEventIdToString.put(0xFEL, "Audio file ends");
        for (Map.Entry<Long, String> entry : typeOfEventIdToString.entrySet()) {
            typeOfEventStringToId.put(entry.getValue(), entry.getKey());
        }

        typeOfChannelIdToString.put(0x00L, "Other");
        typeOfChannelIdToString.put(0x01L, "Master volume");
        typeOfChannelIdToString.put(0x02L, "Front right");
        typeOfChannelIdToString.put(0x03L, "Front left");
        typeOfChannelIdToString.put(0x04L, "Back right");
        typeOfChannelIdToString.put(0x05L, "Back left");
        typeOfChannelIdToString.put(0x06L, "Front centre");
        typeOfChannelIdToString.put(0x07L, "Back centre");
        typeOfChannelIdToString.put(0x08L, "Subwoofer");
        for (Map.Entry<Long, String> entry : typeOfChannelIdToString.entrySet()) {
            typeOfChannelStringToId.put(entry.getValue(), entry.getKey());
        }

        recievedAsIdToString.put(0x00L, "Other");
        recievedAsIdToString.put(0x01L, "Standard CD album with other songs");
        recievedAsIdToString.put(0x02L, "Compressed audio on CD");
        recievedAsIdToString.put(0x03L, "File over the Internet");
        recievedAsIdToString.put(0x04L, "Stream over the Internet");
        recievedAsIdToString.put(0x05L, "As note sheets");
        recievedAsIdToString.put(0x06L, "As note sheets in a book with other sheets");
        recievedAsIdToString.put(0x07L, "Music on other media");
        recievedAsIdToString.put(0x08L, "Non-musical merchandise");
        for (Map.Entry<Long, String> entry : recievedAsIdToString.entrySet()) {
            recievedAsStringToId.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Creates a new TagConstant object.
     */
    private TagConstant() {
        // keep people from instantiating this
    }
}