package com.noemi.imagecache;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class ImageLoader {

    private static ImageMemoryCache memoryCache;
    private static ImageDiskCache diskCache;
    private static Bitmap holder;
    private static PeriodicClearCacheListener listener;

    private ImageLoader() {}

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

    public void execute(String url, Boolean lastIndex, ImageView view) {
        WeakReference<ImageView> i = new WeakReference<>(view);
        Bitmap bitmap = new TaskExecutor(memoryCache, diskCache, url, holder, listener).loadImage(lastIndex);
        final ImageView imageView = i.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
