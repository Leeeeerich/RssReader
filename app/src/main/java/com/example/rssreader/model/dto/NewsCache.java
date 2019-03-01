package com.example.rssreader.model.dto;

import android.database.Observable;

/**
 * An interface representing a user Cache.
 */
public interface NewsCache {
    //TODO Correct description
    /**
     * Gets an {@link Observable} which will emit a {@link News}.
     *
     * @param url The user id to retrieve data.
     */
    Observable<News> get(final String url);

    /**
     * Puts and element into the cache.
     *
     * @param news Element to insert in the cache.
     */
    void put(News news);

    /**
     * Checks if an element (User) exists in the cache.
     *
     * @param url The id used to look for inside the cache.
     * @return true if the element is cached, otherwise false.
     */
    boolean isCached(final String url);

    /**
     * Checks if the cache is expired.
     *
     * @return true, the cache is expired, otherwise false.
     */
    boolean isExpired();

    /**
     * Evict all elements of the cache.
     */
    void evictAll();
}
