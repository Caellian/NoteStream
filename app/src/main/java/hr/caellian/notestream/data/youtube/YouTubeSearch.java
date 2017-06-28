package hr.caellian.notestream.data.youtube;

import android.content.Context;

import com.evgenii.jsevaluator.JsEvaluator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by caellyan on 25/06/17.
 */

public class YouTubeSearch {

    private static final String SEARCH_BASE = "https://www.youtube.com/results?search_query=";
    private static final Pattern VIDEO_ID = Pattern.compile("(?:watch\\?v=)([A-Za-z0-9\\-_]{11})");

    Context context;

    public YouTubeSearch(Context context) {
        this.context = context;
    }


    public ArrayList<String> searchFor(String searched) {
        ArrayList<String> result = new ArrayList<>();

        String searchUrl;
        try {
            searchUrl = SEARCH_BASE + URLEncoder.encode(searched, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return result;
        }

        JsEvaluator jsEvaluator = new JsEvaluator(YouTubeSearch.this.context);

        return result;
    }
}
