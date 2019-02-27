package com.example.rssreader.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rssreader.R;
import com.example.rssreader.model.dto.News;
import com.example.rssreader.service.loader.RssParserTaskLoader;
import com.example.rssreader.ui.adapters.ItemNewsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<News>> {

    public static final int ID_TASK_CALCULATED_PAGE_SIZE = 501;

    private EditText mEtEnterUrl;
    private TextView mTvTime;
    private TextView mTvNumberOf;
    private ProgressBar mProgressBar;
    private ItemNewsAdapter mItemNewsAdapter;
    private List<News> mNewsList = new ArrayList<>();
    private Long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        mTvTime = findViewById(R.id.tvTime);
        mTvNumberOf = findViewById(R.id.tvNumberOf);
        mEtEnterUrl = findViewById(R.id.etUrl);
        findViewById(R.id.btStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLoader(ID_TASK_CALCULATED_PAGE_SIZE, RssParserTaskLoader.createBundle(mEtEnterUrl.getText().toString()));
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mItemNewsAdapter = new ItemNewsAdapter(this);
        recyclerView.setAdapter(mItemNewsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initLoader(int id, Bundle bundle) {
        if (getSupportLoaderManager().getLoader(id) == null) {
            getSupportLoaderManager().initLoader(id, bundle, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(id, bundle, this).forceLoad();
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        if (mNewsList != null) mNewsList.clear();
        mProgressBar.setVisibility(View.VISIBLE);
        mStartTime = System.nanoTime();

        RssParserTaskLoader rssParserTaskLoader = new RssParserTaskLoader(this, args);
        Log.d(getLocalClassName(), "onCreateLoader: " + rssParserTaskLoader.hashCode());

        return rssParserTaskLoader;
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<List<News>> loader, List<News> newsList) {
        Log.d(getLocalClassName(), "onFinishedTaskLoader: ");

        Long endTime = System.nanoTime();
        mProgressBar.setVisibility(View.INVISIBLE);
        Log.d(getLocalClassName(), "Time running = " + (endTime - mStartTime) / 1000000);

        String finishTime = String.valueOf((endTime - mStartTime) / 1000000);
        mTvTime.setText(getString(R.string.time, finishTime));
        mTvNumberOf.setText(getString(R.string.number_of, String.valueOf(newsList.size())));
        mItemNewsAdapter.setNewsList(newsList);
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader loader) {

    }
}
