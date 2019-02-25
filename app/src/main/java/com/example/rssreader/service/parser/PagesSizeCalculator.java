package com.example.rssreader.service.parser;

import android.util.Log;

import com.example.rssreader.model.dto.News;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PagesSizeCalculator {

    private ExecutorService mThreadPool;
    private boolean isMoreData = false;
    private AtomicInteger mCounter;

    private Callbacks mCallbacks;

    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    public void init(News news) {
        mThreadPool = Executors.newFixedThreadPool(getSizePoolThread());

        try {
            increment();
            mThreadPool.submit(startTask(news.getUrl()));
        } catch (IOException e) {
            decrementAndCheck();
            Log.e(getClass().getName(), e.getMessage());
        }
    }

    private Callable<Void> startTask(final String url) throws IOException {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                InputStream is = NetworkFactory.getUrlConnection(url).getInputStream();
                Log.d(getClass().getName(), "Ids thread = " + Thread.currentThread());

                final int bufferSize = 1024;
                final char[] buffer = new char[bufferSize];
                final StringBuilder out = new StringBuilder();
                Reader in = new InputStreamReader(is, "UTF-8");
                for (; ; ) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0) break;
                    out.append(buffer, 0, rsz);
                }

                mCallbacks.onDataCallbacks(url, out.length());
                return null;
            }
        };
    }

    private void increment(){
        mCounter.incrementAndGet();
    }

    private void decrementAndCheck(){
        if(!isMoreData & mCounter.decrementAndGet() == 0) {
            mThreadPool.shutdown();
        }
    }

    public void setNoMoreData(){
        this.isMoreData = false;
    }

    private int getSizePoolThread() {
        Log.d(getClass().getName(), "Your processor has = " + Runtime.getRuntime().availableProcessors() + " cores");
        return 2 * Runtime.getRuntime().availableProcessors() + 1;
    }

    public interface Callbacks {
        void onDataCallbacks(String url, long size);

        void onAllParsed();
    }

}
