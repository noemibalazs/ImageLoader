package com.noemi.imagecache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageDiskCache {

    private static ImageDiskCache instance = null;
    private DiskLruCache diskLruCache;

    private ImageDiskCache() {
    }

    public static ImageDiskCache getInstance() {
        if (instance == null) {
            instance = new ImageDiskCache();
        }
        return instance;
    }

    public void initialize(Context context) {
        File cacheDir = getDiskCacheDir(context);
        try {
            diskLruCache = DiskLruCache.open(cacheDir, 1, 1, (long) (21 * 1024 * 1024));
        } catch (IOException e) {
            System.out.println("initialize() exception: " + e.getLocalizedMessage());
        }
    }

    private File getDiskCacheDir(Context context) {
        final String cachePath = context.getCacheDir().getPath();
        return new File(cachePath + File.separator + "zipo_images");
    }

    private String fileKey(String key) {
        String fileKey = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(key.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hash = new StringBuilder(no.toString(16));
            while (hash.length() < 32) {
                hash.insert(0, "0");
            }
            fileKey = hash.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("fileKey() exception: " + e.getLocalizedMessage());
        }
        return fileKey;
    }

    public void addBitmapToDiskCache(String key, Bitmap bitmap) {
        try {
            String fileKey = fileKey(key);
            DiskLruCache.Snapshot snapshot = diskLruCache.get(fileKey);
            if (snapshot == null) {
                DiskLruCache.Editor editor = diskLruCache.edit(fileKey);
                OutputStream stream = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                editor.commit();
                stream.close();
            } else {
                snapshot.close();
            }
        } catch (IOException e) {
            System.out.println("addBitmapToDiskCache() exception: " + e.getLocalizedMessage());
        }
    }

    @Nullable
    public Bitmap getBitmapFromDiskCache(String key) {
        Bitmap bitmap = null;
        try {
            String fileKey = fileKey(key);
            DiskLruCache.Snapshot snapshot = diskLruCache.get(fileKey);
            if (snapshot != null) {
                InputStream stream = snapshot.getInputStream(0);
                if (stream != null) {
                    bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                }
            }
        } catch (IOException e) {
            System.out.println("getBitmapFromDiskCache() exception: " + e.getLocalizedMessage());
        }
        return bitmap;
    }

    public void clearCache() {
        if (!isClosed() && diskLruCache != null) {
            try {
                diskLruCache.close();
                diskLruCache.delete();
            } catch (IOException e) {
                System.out.println("clearCache() exception: " + e.getLocalizedMessage());
            }
        }
    }

    public Boolean isClosed(){
        return diskLruCache.isClosed();
    }
}
