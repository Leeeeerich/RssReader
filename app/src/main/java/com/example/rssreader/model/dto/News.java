package com.example.rssreader.model.dto;

public class News {

    private String mTitle;
    private String mUrl;
    private String mText;

    public News(){

    }

    public News(String title, String url, String text) {
        mTitle = title;
        mUrl = url;
        mText = text;
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

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
