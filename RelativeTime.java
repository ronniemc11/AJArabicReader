package com.example.ajreader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RelativeTime {

    // RFC-822 style date format commonly used in RSS pubDate fields,
    // e.g. "Sun, 26 Jul 2026 10:15:00 GMT"
    private static final SimpleDateFormat RSS_DATE_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    public static String format(String pubDate) {
        if (pubDate == null || pubDate.length() == 0) {
            return "";
        }
        try {
            Date date = RSS_DATE_FORMAT.parse(pubDate);
            long diffMs = System.currentTimeMillis() - date.getTime();
            if (diffMs < 0) diffMs = 0;

            long minutes = diffMs / (60 * 1000);
            long hours = minutes / 60;
            long days = hours / 24;

            if (minutes < 1) {
                return "الآن";
            } else if (minutes < 60) {
                return minutes + " دقيقة";
            } else if (hours < 24) {
                return hours + " ساعة";
            } else {
                return days + " يوم";
            }
        } catch (Exception e) {
            return pubDate;
        }
    }
}
