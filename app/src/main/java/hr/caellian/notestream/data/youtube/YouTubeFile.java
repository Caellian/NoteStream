package hr.caellian.notestream.data.youtube;

public class YouTubeFile {

    private Format format;
    private String url = "";

    YouTubeFile(Format format, String url) {
        this.format = format;
        this.url = url;
    }

    /**
     * The url to download the file.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Format data for the specific file.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Format data for the specific file.
     */
    @Deprecated
    public Format getMeta(){
        return format;
    }
}
