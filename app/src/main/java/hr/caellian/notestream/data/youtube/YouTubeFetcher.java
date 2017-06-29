package hr.caellian.notestream.data.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caellyan on 25/06/17.
 */

public class YouTubeFetcher {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36";

    private static final String SEARCH_BASE = "https://www.youtube.com/results?search_query=";
    private static final Pattern VIDEO_ID = Pattern.compile("/watch\\?v=([A-Za-z0-9_-]{11})");

    public static ArrayList<String> searchFor(String searched) {
        ArrayList<String> result = new ArrayList<>();

        String searchLink;
        try {
            searchLink = SEARCH_BASE + URLEncoder.encode(searched, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return result;
        }

        try {
            URL searchUrl = new URL(searchLink);
            HttpURLConnection urlConnection = (HttpURLConnection) searchUrl.openConnection();
            urlConnection.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String html = reader.readLine();
            Matcher matcher = VIDEO_ID.matcher(html);

            while (matcher.find()) {
                String found = matcher.group();
                if (!result.contains(found)) result.add(found);
            }

        } catch (IOException e) {
            return result;
        }

        return result;
    }

    public static ArrayList<String> getSuggestionsFor(String youtubeID) {
        ArrayList<String> result = new ArrayList<>();
        // TODO: Implement
        return result;
    }
}
