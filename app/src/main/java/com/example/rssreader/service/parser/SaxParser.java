package com.example.rssreader.service.parser;

import com.example.rssreader.model.dto.News;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SaxParser {

    private List<News> mNewsList = new ArrayList<>();
    private ISaxParser mISaxParser;

    public void setISaxParserListener(ISaxParser saxParser){
        this.mISaxParser = saxParser;
    }

    public void parser(URLConnection connection) {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                News mNews;

                boolean item = false;
                boolean name = false;
                boolean link = false;

                public void startElement(String uri, String localName,
                                         String qName, Attributes attributes)
                        throws SAXException {
                    if(qName.equalsIgnoreCase("item")){
                        item = true;
                    }
                    if (qName.equalsIgnoreCase("title")) {
                        name = true;
                    }
                    if (qName.equalsIgnoreCase("link")) {
                        link = true;
                    }
                }

                public void endElement(String uri, String localName,
                                       String qName) throws SAXException {
                }

                public void characters(char ch[], int start, int length)
                        throws SAXException {

                    if (item & name) {
                        mNews = new News();

                        mNews.setTitle(new String(ch, start, length));
                        name = false;
                    }
                    if (item & link) {
                        mNews.setUrl(new String(ch, start, length));
                        link = false;

                        mNewsList.add(mNews);
                        mNews = null;
                    }
                }

            };
            saxParser.parse(connection.getInputStream(), handler);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
        mISaxParser.getListNews(mNewsList);
    }
}
