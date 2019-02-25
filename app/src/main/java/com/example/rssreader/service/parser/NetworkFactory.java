package com.example.rssreader.service.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class NetworkFactory {

    public static URLConnection getUrlConnection(String url) throws IOException {
        URL myUrl = new URL(url.isEmpty() ? "https://fakty.ua/rss_feed/ukraina" : url);
        URLConnection myUrlCon = myUrl.openConnection();
        myUrlCon.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        myUrlCon.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        return myUrlCon;
    }


    public static class Builder {
        private Map<String, String> mProperties;
        private String mUrl;

        public Builder(String url) {
            if (url == null || url.isEmpty())
                throw new RuntimeException("Wrong url");
            mUrl = url;
        }

        private Map<String, String> getMap() {
            if (mProperties == null) {
                mProperties = new HashMap<>();
            }
            return mProperties;
        }

        public Builder addProperty(String key, String value) {
            getMap().put(key, value);
            return this;
        }

        public URLConnection build() throws IOException {
            URL url = new URL(mUrl);
            URLConnection connection = url.openConnection();
            if (mProperties != null) {
                for (String key : mProperties.keySet()) {
                    connection.setRequestProperty(key, mProperties.get(key));
                }
            }
            return connection;
        }
    }


}

