package com.example.rssreader.service.parser;

import android.util.Log;

import com.example.rssreader.model.dto.News;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountingPagesSize {

    private ExecutorService mThreadPool;

    public void setNewsList(List<News> newsList) {
        init(newsList);
    }

    private void init(List<News> newsList) {
        mThreadPool = Executors.newFixedThreadPool(getSizePoolThread());

        List list = new ArrayList<>();
        for (News news : newsList) {
            try {
            list.add(getCallablePageSize(news.getUrl()));
            /*
                Callable<Long> callablePageSize = getCallablePageSize(news.getUrl());
                Long aLong = mThreadPool.submit(callablePageSize).get();
                String s = String.valueOf(aLong);
                news.setSize(s);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            mThreadPool.invokeAll(list);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mThreadPool.shutdown();
    }

    private Callable<Long> getCallablePageSize(final String url) throws IOException {
        return new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                InputStream inputStream = NetworkFactory.getUrlConnection(url).getInputStream();
                String s = new InputStreamReader(inputStream, Charset.forName("UTF8")).toString();
                Log.d(getClass().getName(), "Ids thread = " + Thread.currentThread());
                return (long) s.length();
            }
        };
    }

    private int getSizePoolThread() {
        Log.d(getClass().getName(), "Your processor has = " + Runtime.getRuntime().availableProcessors() + " cores");
        return 2 * Runtime.getRuntime().availableProcessors() + 1;
    }

}
