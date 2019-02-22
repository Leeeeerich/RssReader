package com.example.rssreader.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rssreader.R;
import com.example.rssreader.model.NewsItem;
import com.example.rssreader.model.dto.News;
import com.example.rssreader.service.parser.CountingPagesSize;
import com.example.rssreader.service.parser.ISaxParser;
import com.example.rssreader.service.parser.SaxParser;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ISaxParser{

    private EditText mEtEnterUrl;
    private Button mBtStart;
    private TextView mTvTime;

    private SaxParser mSaxParser;
    private CountingPagesSize mCountingPagesSize;

    private List<News> mNewsList;
    private List<NewsItem> mNewsItems;
    private Long mStartTime;
    private Long mEndTime;
    private String mFinishTime;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSaxParser = new SaxParser();
        mSaxParser.setISaxParserListener(this);

        mCountingPagesSize = new CountingPagesSize();

        mProgressBar = findViewById(R.id.progressBar);
        mTvTime = findViewById(R.id.tvTime);
        mEtEnterUrl = findViewById(R.id.etUrl);
        mBtStart = findViewById(R.id.btStart);
        mBtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTask().execute(mEtEnterUrl.getText().toString());
            }
        });
    }

    @Override
    public void getListNews(List<News> list) {
        mNewsList = list;
        mCountingPagesSize.setNewsList(list);
        Log.d(getLocalClassName(), "\nNumber of = " + list.size());
        for(News news : list){
            Log.d(getLocalClassName(), "" + news.getTitle() + "\n" + news.getUrl());
        }
    }

    class MyTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mStartTime = System.nanoTime();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                Log.d(getLocalClassName(), "Total count = " + mCountingPagesSize.countingPageSize(getUrlConnection("https://fakty.ua/296983-nabu-prishlo-s-obyskami-k-rukovodstu-poltavskoj-obladministracii-chto-icshut")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(String s : params) {
                try {
                    mSaxParser.parser(getUrlConnection(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mEndTime = System.nanoTime();
            mProgressBar.setVisibility(View.INVISIBLE);
            Log.d(getLocalClassName(), "Time running = " + (mEndTime - mStartTime) / 1000000);

            mFinishTime = String.valueOf((mEndTime - mStartTime) / 1000000);
            mTvTime.setText(getString(R.string.time, mFinishTime));
        }
    }

    private URLConnection getUrlConnection(String url) throws IOException {

        URL myUrl = new URL(url.isEmpty() ? "https://fakty.ua/rss_feed/ukraina" : url);
        URLConnection myUrlCon = myUrl.openConnection();
        myUrlCon.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        myUrlCon.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        return myUrlCon;
    }
}
