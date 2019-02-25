package com.example.rssreader.model.dto;

public class News {

    private String mTitle;
    private String mUrl;
    private String mSize;

    public News(){

    }

    public News(String title, String url, String size) {
        mTitle = title;
        mUrl = url;
        mSize = size;
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

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        mSize = size;
    }
}
