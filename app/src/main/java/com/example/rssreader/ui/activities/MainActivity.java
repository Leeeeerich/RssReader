package com.example.rssreader.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.example.rssreader.R;
import com.example.rssreader.databinding.ActivityMainBinding;
import com.example.rssreader.ui.adapters.ItemNewsAdapter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private MainViewModel mViewModel;

    private ItemNewsAdapter mItemNewsAdapter;
    private Long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.initNews().observe(this, news -> {
            Log.d(getLocalClassName(), "ViewModel current Thread = " + Thread.currentThread());
            if (mItemNewsAdapter != null) {
                mItemNewsAdapter.addNews(news);
            }
        });

        mBinding.btStart.setOnClickListener(v -> {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mStartTime = System.nanoTime();
            mViewModel.getNews(mBinding.etUrl.getText().toString());
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        mItemNewsAdapter = new ItemNewsAdapter();
        mBinding.recyclerView.setAdapter(mItemNewsAdapter);
        mBinding.recyclerView.setLayoutManager(linearLayoutManager);
    }
}
