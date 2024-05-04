package com.noemi.imagecache;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class ImageLoader {

    private final ImageMemoryCache memoryCache;
    private final ImageDiskCache diskCache;
    private final Bitmap placeHolder;
    private final WeakReference<ImageView> imageView;
    private final PeriodicClearCacheListener listener;

    public ImageLoader(ImageMemoryCache cache, ImageDiskCache diskCache, ImageView imageView, Bitmap placeHolder, PeriodicClearCacheListener listener) {
        this.memoryCache = cache;
        this.diskCache = diskCache;
        this.imageView = new WeakReference<ImageView>(imageView);
        this.placeHolder = placeHolder;
        this.listener = listener;
    }

    public void execute(String url, Boolean lastIndex) {
        Bitmap bitmap = new TaskExecutor(memoryCache, diskCache, url, placeHolder, listener).loadImage(lastIndex);
        final ImageView imageView = this.imageView.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
