package com.example.rssreader.service.parser;

public enum RssParserKeys {
    LINK_TO_RSS_CHANNEL("link_to_rss_channel"),
    PARSE_CHANNEL_TASK_ID("parse_channel_task_id");

    private final String name;

    private RssParserKeys(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
