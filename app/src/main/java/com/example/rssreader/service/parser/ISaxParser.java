package com.example.rssreader.service.parser;

import com.example.rssreader.model.dto.News;

public interface ISaxParser {
    void onLoadItem(News news);

    void onEndDocument();

    void onErrorParse(Exception e);
}
