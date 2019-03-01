package com.example.rssreader.ui.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.rssreader.model.dto.News;
import com.example.rssreader.service.repositories.NewsRepository;

import java.util.List;

public class MainViewModel extends ViewModel implements NewsRepository.NewsRepositoryCallbacks {

    private MutableLiveData<List<News>> mNewsMutableLiveData;
    private NewsRepository mNewsRepository;

    private MainViewModelCallbacks mMainViewModelCallbacks;

    public MainViewModel() {
        this.mNewsRepository = NewsRepository.getInstance();
        mNewsRepository.setNewsRepositoryCallbacks(this);
    }

    public LiveData<List<News>> initNews() {
        if (mNewsMutableLiveData == null) {
            mNewsMutableLiveData = new MutableLiveData<>();
        }

        return mNewsMutableLiveData;
    }

    public void setMainViewModelCallbacks(MainViewModelCallbacks mainViewModelCallbacks) {
        mMainViewModelCallbacks = mainViewModelCallbacks;
    }

    public void getNews(String url) {
        mNewsRepository.getNewsList(url.isEmpty() ? "https://fakty.ua/rss_feed/ukraina" : url, mNewsMutableLiveData);
    }

    @Override
    public void noMoreData() {
        mMainViewModelCallbacks.noMoreData();
    }

    public interface MainViewModelCallbacks {
        void noMoreData();
    }
}
