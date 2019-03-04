package com.example.rssreader.model.dto;

public class News {

    private String mTitle;
    private String mFromChannel;
    private String mUrlCoverChannel;
    private String mUrl;
    private String mDescription;
    private String mUrlCover;

    public News() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getFromChannel() {
        return mFromChannel;
    }

    public void setFromChannel(String fromChannel) {
        mFromChannel = fromChannel;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getUrlCoverChannel() {
        return mUrlCoverChannel;
    }

    public void setUrlCoverChannel(String urlCoverChannel) {
        mUrlCoverChannel = urlCoverChannel;
    }

    public String getUrlCover() {
        return mUrlCover;
    }

    public void setUrlCover(String urlCover) {
        mUrlCover = urlCover;
    }
}
