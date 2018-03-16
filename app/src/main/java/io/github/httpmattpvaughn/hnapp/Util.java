package io.github.httpmattpvaughn.hnapp;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;

import io.github.httpmattpvaughn.hnapp.views.MyTextView;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class Util {

    // Empirically chosen based on what looks good on my device, definitely
    // worth evaluating and probably changing based on density/current width
    static final int MAX_URL_LENGTH = 22;

    // Change the format of a url from the default- remove everything from url except for
    // the subdomain, domain, and tld. Remove subdomain if it is www
    // example: http://subdomain.domain.tld/blah-blah-blah would become subdomain.domain.tld
    public static String shortifyUrl(String url) {
        if (url == null) {
            return null;
        }
        URI uri;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            // remove wwws for attractiveness
            domain = domain.startsWith("www.") ? domain.substring(4) : domain;
            // if the domain would be shortened to, for example, www.google.c, let the show thing show
            if (domain.length() > MAX_URL_LENGTH && domain.length() < MAX_URL_LENGTH + 3) {
                return domain;
            }
            return domain.substring(0, Math.min(MAX_URL_LENGTH, domain.length()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // default to just returning the non-beautified url (malformed?- server issue...)
        return url.substring(0, Math.min(MAX_URL_LENGTH, url.length()));
    }

    // Returns a pretty string of the age like "1hr ago", "2 days ago"
    // submittedTimestamp: Unix timestamp of time post was submitted
    // currentTimestamp: Unix timestamp of current time
    public static String beautifyPostAge(long submittedTimestamp, long currentTimestamp) {
        if (submittedTimestamp < 0) {
            submittedTimestamp = 0;
        }
        long diffSeconds = currentTimestamp - (submittedTimestamp);
        long diffMinutes = (long) (diffSeconds / 60.0);
        long diffHours = (long) (diffMinutes / 60.0);
        long diffDays = (long) (diffHours / 24.0);
        long diffWeeks = diffDays / 7;
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
        if (diffWeeks <= 4) {
            return diffWeeks + "wk" + ((diffWeeks == 1) ? "" : "s");
        }
        // Post is between 12 months and 30 days old
        if (diffMonths < 12) {
            return diffMonths + " month" + ((diffMonths == 1) ? "" : "s");
        }

        // Post is older than 12 months old
        return diffYears + "yr" + ((diffYears == 1) ? "" : "s");
    }

    // TODO- change stringToHtml to use a custom implementation b/c there aren't any
    // TODO- handle clicks in
    // libraries that solve my problem well
    //
    // My problem: want to parse this limited HTML subset efficiently (v quickly),
    // have longpressability on links, and be able to open handle links manually

    // formats html string to a "Spanned" object for use in textview
    public static CharSequence stringToHtml(String string,
                                            TextView textView,
                                            MyTextView.OnLinkClickListener listener) {
        if (string == null) {
            return null;
        }

        CharSequence spanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(string);
        }

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(spanned);
        URLSpan[] urls = strBuilder.getSpans(0, spanned.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span, textView, listener);
        }

        textView.setLinksClickable(true);

        return spanned;
    }

    protected static void makeLinkClickable(SpannableStringBuilder strBuilder,
                                            final URLSpan span,
                                            final TextView textView,
                                            final MyTextView.OnLinkClickListener listener) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                listener.onClickLink(textView, span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    // Sets the theme of an activity using sharedprefs
    // note: is this a problem MVP-wise? I'm assuming helper methods can
    // be for Model, View or Presenter... just keep contexts out of presenters/models
    public static void setTheme(Context context) {
        String theme = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.theme_preference_key), null);
        if(theme != null && !theme.equals("light")) {
            switch (theme) {
                case "dark":
                    context.setTheme(R.style.DarkTheme);
                    break;
                case "black":
                    context.setTheme(R.style.DarkTheme_Black);
                    break;
            }
        }
    }
}
