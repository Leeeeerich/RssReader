package com.example.rssreader.service.parser;

import com.example.rssreader.model.dto.News;

import java.util.List;

public interface ISaxParser {
    void getListNews(List<News> list);
}
