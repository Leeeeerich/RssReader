package com.example.rssreader.service.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.rssreader.model.dto.News;
import com.example.rssreader.service.parser.ISaxParser;
import com.example.rssreader.service.parser.NetworkFactory;
import com.example.rssreader.service.parser.PagesSizeCalculator;
import com.example.rssreader.service.parser.SaxParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RssParserTaskLoader extends AsyncTaskLoader<List<News>> implements ISaxParser, PagesSizeCalculator.Callbacks {

    private static final String LINK_TO_RSS_CHANNEL = "link_to_rss_channel";
    private String mUrl;
    private SaxParser mSaxParser;
    private PagesSizeCalculator mPagesSizeCalculator;
    private List<News> mNewsList = new ArrayList<>();
    private Thread mThread;

    public static Bundle createBundle(String s) {
        Bundle bundle = new Bundle();
        bundle.putString(LINK_TO_RSS_CHANNEL, s);
        return bundle;
    }

    public static String getLinkToRssChannel() {
        return LINK_TO_RSS_CHANNEL;
    }

    public RssParserTaskLoader(@NonNull Context context, Bundle args) {
        super(context);
        if (args != null & args.getString(LINK_TO_RSS_CHANNEL).isEmpty()) {
            mUrl = args.getString(LINK_TO_RSS_CHANNEL);
        } else {
            mUrl = "https://fakty.ua/rss_feed/ukraina";
        }

        mSaxParser = new SaxParser();
        mSaxParser.setISaxParserListener(this);

        mPagesSizeCalculator = new PagesSizeCalculator();
        mPagesSizeCalculator.setCallbacks(this);
    }

    @Nullable
    @Override
    public List<News> loadInBackground() {
        Log.d(getClass().getName(), "Thread id = " + Thread.currentThread());
        mThread = Thread.currentThread();
        InputStream is = null;
        try {
            is = NetworkFactory.getUrlConnection(mUrl).getInputStream();
            mSaxParser.parser(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mNewsList;
    }

    @Override
    public void onLoadItem(News news) {
        mPagesSizeCalculator.addTask(news);
    }

    @Override
    public void onEndDocument() {
        mPagesSizeCalculator.shutdownThreadPool();
    }

    @Override
    public void onErrorParse(Exception e) {
        Log.e(getClass().getName(), "Parsed error = " + e.getMessage());
    }

    @Override
    public void onDataLoaded(News news) {
        mNewsList.add(news);
    }

    @Override
    public void CalculatingFinished() {
        synchronized (mThread) {
            mThread.notify();
        }
        deliverResult(mNewsList);
    }

    @Override
    public void onErrorCalculated(IOException e) {
        Log.e(getClass().getName(), "Calculating error = " + e.getMessage());
    }
}