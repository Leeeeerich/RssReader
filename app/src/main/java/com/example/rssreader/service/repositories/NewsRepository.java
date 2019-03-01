package com.example.rssreader.service.repositories;

import android.arch.lifecycle.MutableLiveData;
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

public class NewsRepository implements ISaxParser, PagesSizeCalculator.Callbacks {

    private static volatile NewsRepository instance;
    private MutableLiveData<List<News>> mNewsMutableLiveData;
    private final List<News> mNewsList = new ArrayList<>();

    private SaxParser mSaxParser;
    private PagesSizeCalculator mPagesSizeCalculator;

    private NewsRepositoryCallbacks mNewsRepositoryCallbacks;

    public void setNewsRepositoryCallbacks(NewsRepositoryCallbacks newsRepositoryCallbacks) {
        mNewsRepositoryCallbacks = newsRepositoryCallbacks;
    }

    private NewsRepository() {
        mSaxParser = new SaxParser();
        mSaxParser.setISaxParserListener(this);

        mPagesSizeCalculator = new PagesSizeCalculator();
        mPagesSizeCalculator.setCallbacks(this);
    }

    public static NewsRepository getInstance() {
        synchronized (NewsRepository.class) {
            if (instance == null) {
                instance = new NewsRepository();
            }
        }
        return instance;
    }

    public void getNewsList(String url, MutableLiveData<List<News>> mutableLiveData) {
        this.mNewsMutableLiveData = mutableLiveData;
        new Thread(() -> {
            InputStream is = null;
            try {
                is = NetworkFactory.getUrlConnection(url).getInputStream();
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
        }).start();
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
        Log.e(getClass().getName(), "Parsing error = " + e.getMessage());
    }

    @Override
    public void onDataLoaded(News news) {
        synchronized (mNewsList) {
            mNewsList.add(news);
        }
        mNewsMutableLiveData.postValue(mNewsList);
    }

    @Override
    public void onErrorCalculated(IOException e) {
        Log.e(getClass().getName(), "Calculating error = " + e.getMessage());
    }

    @Override
    public void onCalculatingFinished() {
        mNewsRepositoryCallbacks.noMoreData();
    }

    public interface NewsRepositoryCallbacks {
        void noMoreData();
    }
}
