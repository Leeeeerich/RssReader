package com.example.rssreader.service.parser;

import android.util.Log;

import com.example.rssreader.model.dto.News;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountingPagesSize {

    public CountingPagesSize(){

    }

    public void setNewsList(List<News> newsList){
        try {
            method(newsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void method(List<News> newsList) throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        newsList.remove(0);
        for(News news : newsList){
            //long result = countingPageSize(getUrlConnection(news.getUrl()));
            try {
                Log.d(getClass().getName(), "Size of = " + getCallablePageSize(news.getUrl()).call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        threadPool.shutdown();
    }

    private Callable<Long> getCallablePageSize(final String url) throws IOException {
        return new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return countingPageSize(getUrlConnection(url));
            }
        };
    }

    public long countingPageSize(URLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        String s = new InputStreamReader(inputStream, Charset.forName("UTF8")).toString();
        return s.length();
    }

    private int getSizePoolThread(){
        Log.d(getClass().getName(), "Your apparature has = " + Runtime.getRuntime().availableProcessors());
        return Runtime.getRuntime().availableProcessors() + 1;
    }

    private URLConnection getUrlConnection(String url) throws IOException {
        URL myUrl = new URL(url);
        URLConnection myUrlCon = myUrl.openConnection();
        myUrlCon.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        myUrlCon.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        return myUrlCon;
    }
}
