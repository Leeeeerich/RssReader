package com.example.rssreader.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rssreader.R;
import com.example.rssreader.model.dto.News;
import com.example.rssreader.service.parser.CountingPagesSize;
import com.example.rssreader.service.parser.ISaxParser;
import com.example.rssreader.service.parser.NetworkFactory;
import com.example.rssreader.service.parser.SaxParser;
import com.example.rssreader.ui.adapters.ItemNewsAdapter;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ISaxParser {

    private EditText mEtEnterUrl;
    private TextView mTvTime;
    private TextView mTvNumberOf;
    private ProgressBar mProgressBar;
    private ItemNewsAdapter mItemNewsAdapter;
    private SaxParser mSaxParser;
    private CountingPagesSize mCountingPagesSize;
    private List<News> mNewsList;
    private Long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSaxParser = new SaxParser();
        mSaxParser.setISaxParserListener(this);

        mCountingPagesSize = new CountingPagesSize();

        mProgressBar = findViewById(R.id.progressBar);
        mTvTime = findViewById(R.id.tvTime);
        mTvNumberOf = findViewById(R.id.tvNumberOf);
        mEtEnterUrl = findViewById(R.id.etUrl);
        Button btStart = findViewById(R.id.btStart);
        btStart.setOnClickListener(new View.OnClickListener() {
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
    public void getListNews(List<News> list) {
        mNewsList = list;
        mCountingPagesSize.setNewsList(list);
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
            for (String s : params) {
                try {
                    mSaxParser.parser(NetworkFactory.getUrlConnection(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            mItemNewsAdapter.setNewsList(mNewsList);

//            try {
//                URLConnection connection = new NetworkFactory.Builder("123456")
//                        .addProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
//                        .addProperty("Content-Type", "text/plain; charset=utf-8")
//                        .build();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }
}
