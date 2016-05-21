package wyz.whaley.pinterest.http.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import wyz.whaley.pinterest.utils.DataUtils;
import wyz.whaley.pinterest.utils.FileUtils;

/**
 * Created by alwayking on 16/4/3.
 */
public class AKImageDiskCache {

    public static final String TAG = "AKImageDiskCache";

    private static final String IAMGE_CACHE = "image";

    public DiskLruCache mDiskLruCache = null;
    public DiskLruCache.Editor mEditor = null;
    private static final int VALUE_COUNT = 1;
    private static final int MAX_SIZE = 40 * 1024 * 1024;

    public AKImageDiskCache(Context context) {
        mDiskLruCache = getDiskLruCache(context);
    }

    private static DiskLruCache getDiskLruCache(Context context) {   //
        try {
            File cacheDir = FileUtils.getDiskCacheDir(context, IAMGE_CACHE);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            return DiskLruCache.open(cacheDir, 1, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized OutputStream getOutPutStream(String url, Context context) {  //获取写入外存缓存文件的输出流
        try {
            String key = hashKeyForDisk(url);
            mEditor = mDiskLruCache.edit(key);
            if (mEditor != null) {
                OutputStream outputStream = mEditor.newOutputStream(0);
                return outputStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public synchronized void commitSave() {
        try {
            mEditor.commit();
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

    }

    public synchronized void abort() {
        try {
            mEditor.abort();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

    }

    public static Bitmap readImage(Context context, String url) {
        InputStream is = null;
        try {
            DiskLruCache diskLruCache = getDiskLruCache(context);
            String key = hashKeyForDisk(url);
            DiskLruCache.Snapshot snapShot = diskLruCache.get(key);
            if (snapShot != null) {
                is = snapShot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Movie readGif(Context context, String url) {
        InputStream is = null;
        Movie movie = null;
        try {
            DiskLruCache diskLruCache = getDiskLruCache(context);
            String key = hashKeyForDisk(url);
            DiskLruCache.Snapshot snapShot = diskLruCache.get(key);
            if (snapShot != null) {
                is = snapShot.getInputStream(0);
                byte[] bytes = DataUtils.convertToByteArray(is);
                movie = Movie.decodeByteArray(bytes, 0, bytes.length);
            }
            return movie;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getDiskCachePath(Context context, String url) {
        String key = hashKeyForDisk(url);
        File file = FileUtils.getDiskCacheDir(context, IAMGE_CACHE);
        String path = file.getPath() + "/" + key + "." + (VALUE_COUNT - 1);
        Log.i(TAG, path);
        return path;
    }

    public static String hashKeyForDisk(String key) {  //mdk编码
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
