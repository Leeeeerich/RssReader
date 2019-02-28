package com.example.rssreader.ui.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.rssreader.model.dto.News;
import com.example.rssreader.service.repositories.NewsRepository;

public class MainViewModel extends ViewModel implements NewsRepository.NewsRepositoryCallbacks {

    private MutableLiveData<News> mNewsMutableLiveData;
    private NewsRepository mNewsRepository;

    private MainViewModelCallbacks mMainViewModelCallbacks;

    public MainViewModel() {
        this.mNewsRepository = NewsRepository.getInstance();
        mNewsRepository.setNewsRepositoryCallbacks(this);
    }

    public LiveData<News> initNews() {
        if (mNewsMutableLiveData == null) {
            mNewsMutableLiveData = new MutableLiveData<>();
        }

        mNewsMutableLiveData = mNewsRepository.getNewsMutableLiveData();
        return mNewsMutableLiveData;
    }

    public void setMainViewModelCallbacks(MainViewModelCallbacks mainViewModelCallbacks) {
        mMainViewModelCallbacks = mainViewModelCallbacks;
    }

    public MutableLiveData<News> getNewsMutableLiveData() {
        return mNewsMutableLiveData;
    }

    public void getNews(String url) {
        mNewsRepository.getNewsList(url.isEmpty() ? "https://fakty.ua/rss_feed/ukraina" : url);
    }

    public void setNewsMutableLiveData(News newsMutableLiveData) {
        mNewsMutableLiveData.setValue(newsMutableLiveData);
    }

    @Override
    public void noMoreData() {
        mMainViewModelCallbacks.noMoreData();
    }

    public interface MainViewModelCallbacks{
        void noMoreData();
    }
}
