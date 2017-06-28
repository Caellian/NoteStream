package hr.caellian.notestream.data.youtube;

import android.util.SparseArray;

public class Format {

    public enum VCodec {
        H263, H264, MPEG4, VP8, VP9, NONE
    }

    public enum ACodec {
        MP3, AAC, VORBIS, OPUS, NONE
    }

    public static final SparseArray<Format> FORMAT_MAP = new SparseArray<>();

    static {
        // http://en.wikipedia.org/wiki/YouTube#Quality_and_formats

        // Video and Audio
        FORMAT_MAP.put(17, new Format(17, "3gp", 144, Format.VCodec.MPEG4, Format.ACodec.AAC, 24, false));
        FORMAT_MAP.put(36, new Format(36, "3gp", 240, Format.VCodec.MPEG4, Format.ACodec.AAC, 32, false));
        FORMAT_MAP.put(5, new Format(5, "flv", 240, Format.VCodec.H263, Format.ACodec.MP3, 64, false));
        FORMAT_MAP.put(43, new Format(43, "webm", 360, Format.VCodec.VP8, Format.ACodec.VORBIS, 128, false));
        FORMAT_MAP.put(18, new Format(18, "mp4", 360, Format.VCodec.H264, Format.ACodec.AAC, 96, false));
        FORMAT_MAP.put(22, new Format(22, "mp4", 720, Format.VCodec.H264, Format.ACodec.AAC, 192, false));

        // Dash Video
        FORMAT_MAP.put(160, new Format(160, "mp4", 144, Format.VCodec.H264, Format.ACodec.NONE, true));
        FORMAT_MAP.put(133, new Format(133, "mp4", 240, Format.VCodec.H264, Format.ACodec.NONE, true));
        FORMAT_MAP.put(134, new Format(134, "mp4", 360, Format.VCodec.H264, Format.ACodec.NONE, true));
        FORMAT_MAP.put(135, new Format(135, "mp4", 480, Format.VCodec.H264, Format.ACodec.NONE, true));
        FORMAT_MAP.put(136, new Format(136, "mp4", 720, Format.VCodec.H264, Format.ACodec.NONE, true));
        FORMAT_MAP.put(137, new Format(137, "mp4", 1080, Format.VCodec.H264, Format.ACodec.NONE, true));
        FORMAT_MAP.put(264, new Format(264, "mp4", 1440, Format.VCodec.H264, Format.ACodec.NONE, true));
        FORMAT_MAP.put(266, new Format(266, "mp4", 2160, Format.VCodec.H264, Format.ACodec.NONE, true));

        FORMAT_MAP.put(298, new Format(298, "mp4", 720, Format.VCodec.H264, 60, Format.ACodec.NONE, true));
        FORMAT_MAP.put(299, new Format(299, "mp4", 1080, Format.VCodec.H264, 60, Format.ACodec.NONE, true));

        // Dash Audio
        FORMAT_MAP.put(140, new Format(140, "m4a", Format.VCodec.NONE, Format.ACodec.AAC, 128, true));
        FORMAT_MAP.put(141, new Format(141, "m4a", Format.VCodec.NONE, Format.ACodec.AAC, 256, true));

        // WEBM Dash Video
        FORMAT_MAP.put(278, new Format(278, "webm", 144, Format.VCodec.VP9, Format.ACodec.NONE, true));
        FORMAT_MAP.put(242, new Format(242, "webm", 240, Format.VCodec.VP9, Format.ACodec.NONE, true));
        FORMAT_MAP.put(243, new Format(243, "webm", 360, Format.VCodec.VP9, Format.ACodec.NONE, true));
        FORMAT_MAP.put(244, new Format(244, "webm", 480, Format.VCodec.VP9, Format.ACodec.NONE, true));
        FORMAT_MAP.put(247, new Format(247, "webm", 720, Format.VCodec.VP9, Format.ACodec.NONE, true));
        FORMAT_MAP.put(248, new Format(248, "webm", 1080, Format.VCodec.VP9, Format.ACodec.NONE, true));
        FORMAT_MAP.put(271, new Format(271, "webm", 1440, Format.VCodec.VP9, Format.ACodec.NONE, true));
        FORMAT_MAP.put(313, new Format(313, "webm", 2160, Format.VCodec.VP9, Format.ACodec.NONE, true));

        FORMAT_MAP.put(302, new Format(302, "webm", 720, Format.VCodec.VP9, 60, Format.ACodec.NONE, true));
        FORMAT_MAP.put(308, new Format(308, "webm", 1440, Format.VCodec.VP9, 60, Format.ACodec.NONE, true));
        FORMAT_MAP.put(303, new Format(303, "webm", 1080, Format.VCodec.VP9, 60, Format.ACodec.NONE, true));
        FORMAT_MAP.put(315, new Format(315, "webm", 2160, Format.VCodec.VP9, 60, Format.ACodec.NONE, true));

        // WEBM Dash Audio
        FORMAT_MAP.put(171, new Format(171, "webm", Format.VCodec.NONE, Format.ACodec.VORBIS, 128, true));

        FORMAT_MAP.put(249, new Format(249, "webm", Format.VCodec.NONE, Format.ACodec.OPUS, 48, true));
        FORMAT_MAP.put(250, new Format(250, "webm", Format.VCodec.NONE, Format.ACodec.OPUS, 64, true));
        FORMAT_MAP.put(251, new Format(251, "webm", Format.VCodec.NONE, Format.ACodec.OPUS, 160, true));

        // HLS Live Stream
        FORMAT_MAP.put(91, new Format(91, "mp4", 144 ,Format.VCodec.H264, Format.ACodec.AAC, 48, false, true));
        FORMAT_MAP.put(92, new Format(92, "mp4", 240 ,Format.VCodec.H264, Format.ACodec.AAC, 48, false, true));
        FORMAT_MAP.put(93, new Format(93, "mp4", 360 ,Format.VCodec.H264, Format.ACodec.AAC, 128, false, true));
        FORMAT_MAP.put(94, new Format(94, "mp4", 480 ,Format.VCodec.H264, Format.ACodec.AAC, 128, false, true));
        FORMAT_MAP.put(95, new Format(95, "mp4", 720 ,Format.VCodec.H264, Format.ACodec.AAC, 256, false, true));
        FORMAT_MAP.put(96, new Format(96, "mp4", 1080 ,Format.VCodec.H264, Format.ACodec.AAC, 256, false, true));
    }

    private int itag;
    private String ext;
    private int height;
    private int fps;
    private VCodec vCodec;
    private ACodec aCodec;
    private int audioBitrate;
    private boolean isDashContainer;
    private boolean isHlsContent;

    Format(int itag, String ext, int height, VCodec vCodec, ACodec aCodec, boolean isDashContainer) {
        this.itag = itag;
        this.ext = ext;
        this.height = height;
        this.fps = 30;
        this.audioBitrate = -1;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = false;
    }

    Format(int itag, String ext, VCodec vCodec, ACodec aCodec, int audioBitrate, boolean isDashContainer) {
        this.itag = itag;
        this.ext = ext;
        this.height = -1;
        this.fps = 30;
        this.audioBitrate = audioBitrate;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = false;
    }

    Format(int itag, String ext, int height, VCodec vCodec, ACodec aCodec, int audioBitrate,
           boolean isDashContainer) {
        this.itag = itag;
        this.ext = ext;
        this.height = height;
        this.fps = 30;
        this.audioBitrate = audioBitrate;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = false;
    }

    Format(int itag, String ext, int height, VCodec vCodec, ACodec aCodec, int audioBitrate,
           boolean isDashContainer, boolean isHlsContent) {
        this.itag = itag;
        this.ext = ext;
        this.height = height;
        this.fps = 30;
        this.audioBitrate = audioBitrate;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = isHlsContent;
    }

    Format(int itag, String ext, int height, VCodec vCodec, int fps, ACodec aCodec, boolean isDashContainer) {
        this.itag = itag;
        this.ext = ext;
        this.height = height;
        this.audioBitrate = -1;
        this.fps = fps;
        this.isDashContainer = isDashContainer;
        this.isHlsContent = false;
    }

    /**
     * Get the frames per second
     */
    public int getFps() {
        return fps;
    }

    /**
     * Audio bitrate in kbit/s or -1 if there is no audio track.
     */
    public int getAudioBitrate() {
        return audioBitrate;
    }

    /**
     * An identifier used by youtube for different formats.
     */
    public int getItag() {
        return itag;
    }

    /**
     * The file extension and conainer format like "mp4"
     */
    public String getExt() {
        return ext;
    }

    public boolean isDashContainer() {
        return isDashContainer;
    }

    public ACodec getAudioCodec() {
        return aCodec;
    }

    public VCodec getVideoCodec() {
        return vCodec;
    }

    public boolean isHlsContent() {
        return isHlsContent;
    }

    /**
     * The pixel height of the video stream or -1 for audio files.
     */
    public int getHeight() {
        return height;
    }

}
