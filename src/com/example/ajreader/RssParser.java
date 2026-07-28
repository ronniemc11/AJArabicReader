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
        String imageUrl = null;

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
                        imageUrl = null;
                    } else if (inItem && "title".equalsIgnoreCase(tagName)) {
                        title = safeNextText(parser);
                    } else if (inItem && "description".equalsIgnoreCase(tagName)) {
                        description = safeNextText(parser);
                    } else if (inItem && "link".equalsIgnoreCase(tagName)) {
                        link = safeNextText(parser);
                    } else if (inItem && "pubDate".equalsIgnoreCase(tagName)) {
                        pubDate = safeNextText(parser);
                    } else if (inItem && "enclosure".equalsIgnoreCase(tagName)) {
                        String url = parser.getAttributeValue(null, "url");
                        String type = parser.getAttributeValue(null, "type");
                        if (url != null && (type == null || type.startsWith("image"))) {
                            imageUrl = url;
                        }
                    } else if (inItem && ("media:thumbnail".equalsIgnoreCase(tagName)
                            || "thumbnail".equalsIgnoreCase(tagName)
                            || "media:content".equalsIgnoreCase(tagName))) {
                        String url = parser.getAttributeValue(null, "url");
                        if (url != null && imageUrl == null) {
                            imageUrl = url;
                        }
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
                                pubDate == null ? "" : pubDate.trim(),
                                imageUrl
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
        String result = text.replaceAll("<[^>]+>", "").trim();
        result = result.replace("&quot;", "\"");
        result = result.replace("&amp;", "&");
        result = result.replace("&#39;", "'");
        result = result.replace("&apos;", "'");
        result = result.replace("&lt;", "<");
        result = result.replace("&gt;", ">");
        result = result.replace("&nbsp;", " ");
        return result;
    }
}
