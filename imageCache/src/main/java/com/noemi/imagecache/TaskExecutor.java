package com.noemi.imagecache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecutor {

    private final ImageMemoryCache memoryCache;
    private final ImageDiskCache diskCache;
    private final Bitmap placeHolder;
    private final String imageUrl;
    private final PeriodicClearCacheListener clearInterface;

    public TaskExecutor(ImageMemoryCache cache, ImageDiskCache diskCache, String url, Bitmap placeHolder, PeriodicClearCacheListener listener) {
        this.memoryCache = cache;
        this.diskCache = diskCache;
        this.placeHolder = placeHolder;
        this.imageUrl = url;
        this.clearInterface = listener;
    }

    public Bitmap loadImage(Boolean isLastIndex) {
        if (memoryCache.getBitmapFromMemoryCache(imageUrl) != null) {
            System.out.println("Bitmap returned from memory");
            return memoryCache.getBitmapFromMemoryCache(imageUrl);
        } else if (!diskCache.isClosed() && diskCache.getBitmapFromDiskCache(imageUrl) != null) {
            Bitmap bitmap = diskCache.getBitmapFromDiskCache(imageUrl);
            memoryCache.addBitmapToMemoryCache(imageUrl, bitmap);
            System.out.println("Bitmap returned from disk");
            return bitmap;
        } else {
            ExecutorService executors = Executors.newSingleThreadExecutor();
            Callable<Bitmap> task = this::getBitmapImage;
            Future<Bitmap> future = executors.submit(task);
            try {
                Bitmap bitmap = future.get();
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 600, 600, false);

                memoryCache.addBitmapToMemoryCache(imageUrl, resized);
                if (!diskCache.isClosed()){
                    diskCache.addBitmapToDiskCache(imageUrl, resized);
                }
                System.out.println("Bitmap returned from endpoint");
                if (isLastIndex) {
                    clearInterface.periodicCacheClear();
                }
                return resized;
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("loadImage() exception: " + e.getLocalizedMessage());
            } finally {
                executors.shutdown();
            }
        }
        return placeHolder;
    }

    @Nullable
    private Bitmap getBitmapImage() {
        Bitmap bitmap;
        try {
            URL newUrl = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) newUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream stream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
        } catch (IOException e) {
            bitmap = placeHolder;
            System.out.println("getBitmapImage() exception: " + e.getLocalizedMessage());
        }

        return bitmap;
    }
}
