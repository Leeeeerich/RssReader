package com.example.rssreader.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.example.rssreader.R;
import com.example.rssreader.databinding.ActivityMainBinding;
import com.example.rssreader.model.dto.News;
import com.example.rssreader.service.loader.RssParserTaskLoader;
import com.example.rssreader.ui.adapters.ItemNewsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ID_TASK_CALCULATED_PAGE_SIZE = 501;

    private ActivityMainBinding mBinding;
    private MainViewModel mViewModel;

    private ItemNewsAdapter mItemNewsAdapter;
    private List<News> mNewsList = new ArrayList<>();
    private Long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.initNews().observe(this, news -> {
            mNewsList.add(news);
            mItemNewsAdapter.notifyDataSetChanged();
        });

        mBinding.btStart.setOnClickListener(v -> {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mViewModel.getNews(mBinding.etUrl.getText().toString());
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        mItemNewsAdapter = new ItemNewsAdapter();
        mBinding.recyclerView.setAdapter(mItemNewsAdapter);
        mBinding.recyclerView.setLayoutManager(linearLayoutManager);
    }


//    @Override
//    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
//        if (mNewsList != null) mNewsList.clear();
//        mBinding.progressBar.setVisibility(View.VISIBLE);
//        mStartTime = System.nanoTime();
//
//        RssParserTaskLoader rssParserTaskLoader = new RssParserTaskLoader(this, args);
//        Log.d(getLocalClassName(), "onCreateLoader: " + rssParserTaskLoader.hashCode());
//
//        return rssParserTaskLoader;
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull android.support.v4.content.Loader<List<News>> loader, List<News> newsList) {
//        Log.d(getLocalClassName(), "onFinishedTaskLoader: ");
//
//        Long endTime = System.nanoTime();
//        mBinding.progressBar.setVisibility(View.INVISIBLE);
//        Log.d(getLocalClassName(), "Time running = " + (endTime - mStartTime) / 1000000);
//
//        String finishTime = String.valueOf((endTime - mStartTime) / 1000000);
//        mBinding.tvTime.setText(getString(R.string.time, finishTime));
//        mBinding.tvNumberOf.setText(getString(R.string.number_of, String.valueOf(newsList.size())));
//        mItemNewsAdapter.setNewsList(newsList);
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull android.support.v4.content.Loader loader) {
//
//    }
}
