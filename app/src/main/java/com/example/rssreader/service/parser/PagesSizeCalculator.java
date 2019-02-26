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
    private boolean isNoMoreData;

    private AtomicInteger mCounter = new AtomicInteger(0);

    private Callbacks mCallbacks;

    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    public void init(News news) {
        mThreadPool = Executors.newFixedThreadPool(getSizePoolThread());

        try {

            mThreadPool.submit(startTask(news));
        } catch (IOException e) {
            decrementAndCheck();
            Log.e(getClass().getName(), e.getMessage());
        }
        mThreadPool.shutdown();
    }

    private Callable<Void> startTask(final News news) throws IOException {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                increment();
                InputStream is = NetworkFactory.getUrlConnection(news.getUrl()).getInputStream();
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

                news.setSize(String.valueOf(out.length()));
                mCallbacks.onDataCallbacks(news);
                Log.d(getClass().getName(), "State of counter = " + mCounter);
                decrementAndCheck();
                return null;
            }
        };
    }

    private void increment() {
        mCounter.incrementAndGet();
    }

    private void decrementAndCheck() {
        if (isNoMoreData() & mCounter.decrementAndGet() == 0) {
            Log.d(getClass().getName(), "No more task");
            mCallbacks.onAllParsed();
        }
    }

    public synchronized void setNoMoreData() {
        this.isNoMoreData = true;
    }

    public synchronized boolean isNoMoreData() {
        return isNoMoreData;
    }

    private int getSizePoolThread() {
        Log.d(getClass().getName(), "Your processor has = " + Runtime.getRuntime().availableProcessors() + " cores");
        return 2 * Runtime.getRuntime().availableProcessors() + 1;
    }

    public interface Callbacks {
        void onDataCallbacks(News news);

        void onAllParsed();
    }

}
