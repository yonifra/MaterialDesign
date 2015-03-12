package com.cryptocodes.materialdesign;

/**
 * Created by jonathanf on 11/3/2015.
 */
public class FeedItem {
    private String title;
    private String thumbnail;
    private String header;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader()
    {
        return header;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
