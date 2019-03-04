package com.example.rssreader.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.rssreader.R;
import com.example.rssreader.databinding.ActivityMainBinding;
import com.example.rssreader.ui.adapters.ItemNewsAdapter;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MainViewModel.MainViewModelCallbacks {

    private ActivityMainBinding mBinding;
    private MainViewModel mViewModel;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ItemNewsAdapter mItemNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.initNews().observe(this, news -> {
            if (mItemNewsAdapter != null) {
                mItemNewsAdapter.setNewsList(news);
            }
        });

        mViewModel.setMainViewModelCallbacks(this);
        mViewModel.getNews(""); //TODO it is for test design

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        mItemNewsAdapter = new ItemNewsAdapter();
        mBinding.recyclerView.setAdapter(mItemNewsAdapter);
        mBinding.recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            Log.d(getLocalClassName(), "Test settings button");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 4000);
    }

    @Override
    public void noMoreData() {

    }
}
