package com.example.ajreader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RssFetcher {

    public static final String FEED_URL = "http://ronniemc.pythonanywhere.com/feed";

    public static List<NewsItem> fetch(String feedUrl) throws Exception {
        HttpURLConnection connection = null;
        InputStream in = null;
        try {
            URL url = new URL(feedUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
            connection.connect();

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP error code: " + status);
            }

            in = connection.getInputStream();
            return RssParser.parse(in);
        } finally {
            if (in != null) {
                try { in.close(); } catch (Exception ignored) {}
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
