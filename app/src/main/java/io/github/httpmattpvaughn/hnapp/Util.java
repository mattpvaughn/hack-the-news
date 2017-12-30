package io.github.httpmattpvaughn.hnapp;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class Util {

    private static final int MAX_URL_LENGTH = 25;

    public static String beautifyUrl(String url) {
        if (url == null) {
            return null;
        }
        URI uri = null;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            domain =  domain.startsWith("www.") ? domain.substring(4) : domain;
            return domain.substring(0, Math.min(MAX_URL_LENGTH, domain.length()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url.substring(0, Math.min(MAX_URL_LENGTH, url.length()));
    }

    // Given unix timestamp (milliseconds since 1970) representing the age
    // of a post, return a prettified string like "1hr ago", "2 days ago"
    public static String beautifyPostAge(int timestamp) {
        if (timestamp < 0) {
            timestamp = 0;
        }
        long currentTimestamp = System.currentTimeMillis();
        long diffMillis = currentTimestamp - (timestamp * 1000L);
        long diffSeconds = (long) (diffMillis / 1000.0);
        long diffMinutes = (long) (diffSeconds / 60.0);
        long diffHours = (long) (diffMinutes / 60.0);
        long diffDays = (long) (diffHours / 24.0);
        long diffWeeks = (long) (diffDays / 7);
        long diffMonths = (long) (diffDays / 30.0);
        long diffYears = (long) (diffMonths / 12.0);

        // Post is 60s old or newer
        if (diffSeconds < 60) {
            return diffSeconds + "s";
        }

        // Post is between 60min and 60s old
        if (diffMinutes < 60) {
            return diffMinutes + " min";
        }
        // Post is between 24hr and 60min old
        if (diffHours < 24) {
            return diffHours + "hr" + ((diffHours == 1) ? "" : "s");
        }
        // Post is between 30 days and 24hr old
        if (diffDays < 7) {
            return diffDays + " day" + ((diffDays == 1) ? "" : "s");
        }
        // Post between 7 days and 4 weeks old
        if (diffWeeks < 4) {
            return diffWeeks + "wk" + ((diffWeeks == 1) ? "" : "s");
        }
        // Post is between 12 months and 30 days old
        if (diffMonths < 12) {
            return diffMonths + " month" + ((diffMonths == 1) ? "" : "s");
        }

        // Post is older than 12 months old

        return diffYears + "yr" + ((diffYears == 1) ? "" : "s");
    }

    // formats html string to a "Spanned" object for use in textview
    public static Spanned stringToHtml(String string) {
        if (string == null) {
            return null;
        }

        Spanned spanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(string);
        }

        return spanned;
    }

    public static float dpToPixel(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
