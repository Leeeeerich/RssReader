package com.example.rssreader.service.parser;

import com.example.rssreader.model.dto.News;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SaxParser {

    private ISaxParser mISaxParser;

    public void setISaxParserListener(ISaxParser saxParser) {
        this.mISaxParser = saxParser;
    }

    public void parser(InputStream is) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(is, createHandler());
        } catch (Exception e) {
            mISaxParser.onErrorParse(e);
        }
    }

    public DefaultHandler createHandler() {
        return new DefaultHandler() {
            News mNews;
            String mValue;
            boolean isItem = false;

            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                if (qName.equalsIgnoreCase("item")) {
                    mNews = new News();
                    isItem = true;
                }
            }

            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (isItem & qName.equalsIgnoreCase("title")) {
                    mNews.setTitle(mValue);
                } else if (isItem & qName.equalsIgnoreCase("link")) {
                    mNews.setUrl(mValue);
                } else if (qName.equalsIgnoreCase("item")) {
                    mISaxParser.onLoadItem(mNews);
                    mNews = null;
                    isItem = false;
                } else if (qName.equalsIgnoreCase("channel")) {
                    mISaxParser.onEndDocument();
                }
            }

            public void characters(char ch[], int start, int length) throws SAXException {
                mValue = new String(ch, start, length);
            }
        };
    }
}
