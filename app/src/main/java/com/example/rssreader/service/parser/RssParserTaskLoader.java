package com.example.rssreader.service.parser;

import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class RssParserTaskLoader extends Loader {

    private String url;
    private SaxParser mSaxParser;

    public RssParserTaskLoader(@NonNull Context context, Bundle bundle, SaxParser saxParser){
        super(context);
        this.mSaxParser = saxParser;

        if (bundle != null)
          url = bundle.getString(RssParserKeys.LINK_TO_RSS_CHANNEL.toString());
        if (TextUtils.isEmpty(url))
            url = "https://fakty.ua/rss_feed/ukraina";
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        Log.d(getClass().getName(), "qwerty: ");
        new CalculatePagesSizeTask().execute(url);
    }

    class CalculatePagesSizeTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
