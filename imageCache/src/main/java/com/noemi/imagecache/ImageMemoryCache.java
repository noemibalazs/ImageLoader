package com.noemi.imagecache;

import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.annotation.Nullable;

public class ImageMemoryCache {

    private LruCache<String, Bitmap> memoryCache;
    private static ImageMemoryCache instance = null;

    private ImageMemoryCache() {
    }

    public static ImageMemoryCache getInstance() {
        if (instance == null) {
            instance = new ImageMemoryCache();
        }
        return instance;
    }

    public void initializeCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        this.memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (memoryCache != null && getBitmapFromMemoryCache(key) == null) {
            memoryCache.put(key, bitmap);
            System.out.println("addBitmapToMemoryCache() - bitmap cached: " + key);
        }
    }

    @Nullable
    public Bitmap getBitmapFromMemoryCache(String key) {
        System.out.println("getBitmapFromMemoryCache() - bitmap was get from memory: " + key);
        return memoryCache.get(key);
    }

    public void clearCache() {
        if (memoryCache != null) {
            memoryCache.evictAll();
            System.out.println("Memory cache was cleared");
        }
    }
}
