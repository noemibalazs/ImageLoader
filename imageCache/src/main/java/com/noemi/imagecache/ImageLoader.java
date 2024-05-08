package com.noemi.imagecache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageLoader {

    private static ImageMemoryCache memoryCache;
    private static ImageDiskCache diskCache;
    private static Bitmap holder;
    private static PeriodicClearCacheListener listener;

    private ImageLoader() {
    }

    private static ImageLoader instance = null;

    public static ImageLoader getInstance(ImageMemoryCache memory, ImageDiskCache disk, Bitmap placeHolder, PeriodicClearCacheListener clearListener) {
        if (instance == null) {
            memoryCache = memory;
            diskCache = disk;
            holder = placeHolder;
            listener = clearListener;
            instance = new ImageLoader();
        }
        return instance;
    }

    public void execute(String url, ImageView imageView, Boolean lastIndex) {
        WeakReference<ImageView> weakView = new WeakReference<>(imageView);
        Bitmap bitmap = loadImage(url, lastIndex);
        final ImageView view = weakView.get();
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
    }

    private Bitmap loadImage(String imageUrl, Boolean isLastIndex) {
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
            Callable<Bitmap> task = () -> getBitmapImage(imageUrl);
            Future<Bitmap> future = executors.submit(task);
            try {
                Bitmap bitmap = future.get();
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 600, 600, false);

                memoryCache.addBitmapToMemoryCache(imageUrl, resized);

                if (!diskCache.isClosed()) {
                    diskCache.addBitmapToDiskCache(imageUrl, resized);
                }

                System.out.println("Bitmap returned from endpoint");

                if (isLastIndex) {
                    listener.periodicCacheClear();
                }
                return resized;
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("loadImage() exception: " + e.getLocalizedMessage());
            } finally {
                executors.shutdown();
            }
        }
        return holder;
    }

    private Bitmap getBitmapImage(String imageUrl) {
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
            bitmap = holder;
            System.out.println("getBitmapImage() exception: " + e.getLocalizedMessage());
        }

        return bitmap;
    }
}
