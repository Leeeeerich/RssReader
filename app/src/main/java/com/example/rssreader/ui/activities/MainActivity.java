package com.example.rssreader.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.rssreader.service.parser.ISaxParser;
import com.example.rssreader.service.parser.NetworkFactory;
import com.example.rssreader.service.parser.PagesSizeCalculator;
import com.example.rssreader.service.parser.SaxParser;
import com.example.rssreader.ui.adapters.ItemNewsAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ISaxParser, PagesSizeCalculator.Callbacks {

    private EditText mEtEnterUrl;
    private TextView mTvTime;
    private TextView mTvNumberOf;
    private ProgressBar mProgressBar;
    private ItemNewsAdapter mItemNewsAdapter;
    private SaxParser mSaxParser;
    private PagesSizeCalculator mPagesSizeCalculator;
    private Map<String, News> mNewsList = new HashMap<>();
    private Long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSaxParser = new SaxParser();
        mSaxParser.setISaxParserListener(this);

        mPagesSizeCalculator = new PagesSizeCalculator();

        mProgressBar = findViewById(R.id.progressBar);
        mTvTime = findViewById(R.id.tvTime);
        mTvNumberOf = findViewById(R.id.tvNumberOf);
        mEtEnterUrl = findViewById(R.id.etUrl);
        findViewById(R.id.btStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTask().execute(mEtEnterUrl.getText().toString());
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

    @Override
    public void onLoadItem(News news) {
        mPagesSizeCalculator.init(news);
    }

    @Override
    public void onEndDocument() {
        mPagesSizeCalculator.setNoMoreData();
    }

    @Override
    public void onDataCallbacks(String url, long size) {
        News news = mNewsList.get(url);
        if(news != null) {
            news.setSize(String.valueOf(size));
            mNewsList.put(url, news);
        }
    }

    @Override
    public void onAllParsed() {
        mItemNewsAdapter.setNewsList(new ArrayList<>(mNewsList.values()));
    }

    @Override
    public void onErrorParse(Exception e) {
        Log.e(getLocalClassName(), e.getMessage());
    }

    class MyTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mNewsList != null) mNewsList.clear();
            mProgressBar.setVisibility(View.VISIBLE);
            mStartTime = System.nanoTime();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                InputStream is = NetworkFactory.getUrlConnection(params[0]).getInputStream();
                mSaxParser.parser(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Long endTime = System.nanoTime();
            mProgressBar.setVisibility(View.INVISIBLE);
            Log.d(getLocalClassName(), "Time running = " + (endTime - mStartTime) / 1000000);

            String finishTime = String.valueOf((endTime - mStartTime) / 1000000);
            mTvTime.setText(getString(R.string.time, finishTime));
            mTvNumberOf.setText(getString(R.string.number_of, String.valueOf(mNewsList.size())));
        }
    }
}
