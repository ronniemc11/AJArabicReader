package com.example.ajreader;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RssParser {

    public static List<NewsItem> parse(InputStream in) throws Exception {
        List<NewsItem> items = new ArrayList<NewsItem>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);

        int eventType = parser.getEventType();
        boolean inItem = false;

        String title = null;
        String description = null;
        String link = null;
        String pubDate = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if ("item".equalsIgnoreCase(tagName)) {
                        inItem = true;
                        title = null;
                        description = null;
                        link = null;
                        pubDate = null;
                    } else if (inItem && "title".equalsIgnoreCase(tagName)) {
                        title = safeNextText(parser);
                    } else if (inItem && "description".equalsIgnoreCase(tagName)) {
                        description = safeNextText(parser);
                    } else if (inItem && "link".equalsIgnoreCase(tagName)) {
                        link = safeNextText(parser);
                    } else if (inItem && "pubDate".equalsIgnoreCase(tagName)) {
                        pubDate = safeNextText(parser);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    tagName = parser.getName();
                    if ("item".equalsIgnoreCase(tagName)) {
                        inItem = false;
                        items.add(new NewsItem(
                                stripHtml(title),
                                stripHtml(description),
                                link == null ? "" : link.trim(),
                                pubDate == null ? "" : pubDate.trim()
                        ));
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return items;
    }

    private static String safeNextText(XmlPullParser parser) {
        try {
            return parser.nextText();
        } catch (Exception e) {
            return "";
        }
    }

    private static String stripHtml(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]+>", "").trim();
    }
}
