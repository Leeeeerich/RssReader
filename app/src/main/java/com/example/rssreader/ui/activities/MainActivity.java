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
import java.util.List;

public class MainActivity extends AppCompatActivity implements ISaxParser, PagesSizeCalculator.Callbacks {

    private EditText mEtEnterUrl;
    private TextView mTvTime;
    private TextView mTvNumberOf;
    private ProgressBar mProgressBar;
    private ItemNewsAdapter mItemNewsAdapter;
    private SaxParser mSaxParser;
    private PagesSizeCalculator mPagesSizeCalculator;
    private List<News> mNewsList = new ArrayList<>();
    private Long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSaxParser = new SaxParser();
        mSaxParser.setISaxParserListener(this);
        mPagesSizeCalculator = new PagesSizeCalculator();
        mPagesSizeCalculator.setCallbacks(this);

        mProgressBar = findViewById(R.id.progressBar);
        mTvTime = findViewById(R.id.tvTime);
        mTvNumberOf = findViewById(R.id.tvNumberOf);
        mEtEnterUrl = findViewById(R.id.etUrl);
        findViewById(R.id.btStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CalculatePagesSizeTask().execute(mEtEnterUrl.getText().toString());
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
        mPagesSizeCalculator.addTask(news);
    }

    @Override
    public void onEndDocument() {
        mPagesSizeCalculator.shutdownThreadPool();
    }

    @Override
    public void onDataLoaded(News news) {
        mNewsList.add(news);
    }

    @Override
    public void finishedCalculated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(getLocalClassName(), "All parsed");
                Long endTime = System.nanoTime();
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.d(getLocalClassName(), "Time running = " + (endTime - mStartTime) / 1000000);

                String finishTime = String.valueOf((endTime - mStartTime) / 1000000);
                mTvTime.setText(getString(R.string.time, finishTime));
                mTvNumberOf.setText(getString(R.string.number_of, String.valueOf(mNewsList.size())));
                mItemNewsAdapter.setNewsList(mNewsList);
            }
        });
    }

    @Override
    public void onErrorParse(Exception e) {
        Log.e(getLocalClassName(), e.getMessage());
    }

    class CalculatePagesSizeTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mNewsList != null) mNewsList.clear();
            mProgressBar.setVisibility(View.VISIBLE);
            mStartTime = System.nanoTime();
        }

        @Override
        protected Void doInBackground(String... params) {
            InputStream is = null;
            try {
                is = NetworkFactory.getUrlConnection(params[0]).getInputStream();
                mSaxParser.parser(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
