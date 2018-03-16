package io.github.httpmattpvaughn.hnapp;

import org.junit.Test;

/**
 * Created by Matt Vaughn: http://mattpvaughn.github.io/
 */

public class UtilTest {

    @Test
    public void shortifyUrl() {
        // remove extraneous stuff
        final String url1 = "https://subdomain.domain.tld/blah-blah-blah";
        final String url2 = "subdomain.domain.tld";
        assert (Util.shortifyUrl(url1).equals(url2));

        // remove www subdomains
        final String url3 = "https://www.wired.com/story/free-speech-issue-yondr-smartphones/";
        final String url4 = "wired.com";
        assert (Util.shortifyUrl(url3).equals(url4));

        // cap max length of a string at 22 if domain + subdomain + tld are too long
        final String url5 = "https://gooboohooboodoo.schloppfesteightyeight.com/story/free-speech-issue-yondr-smartphones/";
        assert (Util.shortifyUrl(url5).length() == Util.MAX_URL_LENGTH);

        // method should fail if url is not parseable- potentially in the future
        // we could add more extensive testing for this, but for now assume URI
        // has been tested well <-- this is why we get crash reports :(
        final String url6 = "";
        try {
            Util.shortifyUrl(url6);
            assert false;
        } catch (Exception e) {

        }
    }


    @Test
    public void beautifyPostAge() {
        // TODO- out of date- write cron job to change this every minute
        long exampleCurrentTimestamp = 1520697240;
        long oneMinuteAgo = 1520697180;
        String oneMinuteString = "1 min";
        long oneHourAgo = 1520693640;
        String oneHourString = "1hr";
        long oneDayAgo = 1520610840;
        String oneDayString = "1 day";
        long oneWeekAgo = 1520092440;
        String oneWeekString = "1wk";
        long fourWeeksAgo = 1518278040;
        String fourWeeksString = "4wks";
        long oneMonthAgo = 1517500440;
        String oneMonthString = "1mo";
        long oneYearAgo = 1489161240;
        String oneYearString = "1yr";

        assert Util.beautifyPostAge(oneMinuteAgo, exampleCurrentTimestamp).equals(oneMinuteString);
        assert Util.beautifyPostAge(oneHourAgo, exampleCurrentTimestamp).equals(oneHourString);
        assert Util.beautifyPostAge(oneDayAgo, exampleCurrentTimestamp).equals(oneDayString);
        assert Util.beautifyPostAge(oneWeekAgo, exampleCurrentTimestamp).equals(oneWeekString);
        assert Util.beautifyPostAge(fourWeeksAgo, exampleCurrentTimestamp).equals(fourWeeksString);
        assert Util.beautifyPostAge(oneMonthAgo, exampleCurrentTimestamp).equals(oneMonthString);
        assert Util.beautifyPostAge(oneYearAgo, exampleCurrentTimestamp).equals(oneYearString);
        assert !Util.beautifyPostAge(oneYearAgo, exampleCurrentTimestamp).equals(oneMinuteString);


    }


}
