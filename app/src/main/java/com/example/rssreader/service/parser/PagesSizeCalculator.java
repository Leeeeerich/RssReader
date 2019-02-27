package com.example.rssreader.service.parser;

import android.util.Log;

import com.example.rssreader.model.dto.News;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PagesSizeCalculator {

    private ExecutorService mThreadPool;
    private AtomicInteger mCounter = new AtomicInteger(0);
    private Callbacks mCallbacks;

    public PagesSizeCalculator() {
        init();
    }

    public void setCallbacks(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    public void init() {
        mThreadPool = Executors.newFixedThreadPool(getSizePoolThread());
    }

    public void addTask(News news) {
        mThreadPool.submit(createTask(news));
    }

    private Callable<Void> createTask(final News news) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                increment();
                InputStream is = NetworkFactory.getUrlConnection(news.getUrl()).getInputStream();
                Log.d(getClass().getName(), "Ids thread = " + Thread.currentThread());

                final int bufferSize = 1024;
                final char[] buffer = new char[bufferSize];
                final StringBuilder out = new StringBuilder();
                Reader in = new InputStreamReader(is, StandardCharsets.UTF_8);
                for (; ; ) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0) break;
                    out.append(buffer, 0, rsz);
                }

                news.setSize(String.valueOf(out.length()));
                mCallbacks.onDataLoaded(news);
                Log.d(getClass().getName(), "State of counter = " + mCounter);
                decrementAndCheck();
                return null;
            }
        };
    }

    public void shutdownThreadPool() {
        mThreadPool.shutdown();
    }

    private void increment() {
        mCounter.incrementAndGet();
    }

    private void decrementAndCheck() {
        if (mThreadPool.isShutdown() & mCounter.decrementAndGet() == 0) {
            Log.d(getClass().getName(), "No more task");
            mCallbacks.finishedCalculated();
        }
    }

    private int getSizePoolThread() {
        Log.d(getClass().getName(), "Your processor has = " + Runtime.getRuntime().availableProcessors() + " cores");
        return 2 * Runtime.getRuntime().availableProcessors() + 1;
    }

    public interface Callbacks {
        void onDataLoaded(News news);

        void finishedCalculated();
    }
}
