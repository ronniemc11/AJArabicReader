package com.example.ajreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageLoader {

    private static final int MAX_CACHE_ENTRIES = 40;

    @SuppressWarnings("serial")
    private static final Map<String, Bitmap> cache =
            new LinkedHashMap<String, Bitmap>(MAX_CACHE_ENTRIES, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
                    return size() > MAX_CACHE_ENTRIES;
                }
            };

    public static void load(final ImageView imageView, final String url) {
        if (url == null || url.length() == 0) {
            imageView.setImageDrawable(null);
            return;
        }

        Bitmap cached = cache.get(url);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }

        imageView.setTag(url);
        imageView.setImageDrawable(null);

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                HttpURLConnection connection = null;
                InputStream in = null;
                try {
                    URL imageUrl = new URL(url);
                    connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    connection.connect();
                    in = connection.getInputStream();
                    return BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    return null;
                } finally {
                    if (in != null) {
                        try { in.close(); } catch (Exception ignored) {}
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    cache.put(url, bitmap);
                }
                Object tag = imageView.getTag();
                if (bitmap != null && url.equals(tag)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }
}
