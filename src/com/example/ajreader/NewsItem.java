package com.example.ajreader;

public class NewsItem {
    public String title;
    public String description;
    public String link;
    public String pubDate;
    public String imageUrl;

    public NewsItem(String title, String description, String link, String pubDate, String imageUrl) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.imageUrl = imageUrl;
    }
}
