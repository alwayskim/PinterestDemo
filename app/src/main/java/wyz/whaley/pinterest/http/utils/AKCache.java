package wyz.whaley.pinterest.http.utils;

import android.util.LruCache;

/**
 * Created by alwayking on 16/3/15.
 */
public abstract class AKCache<T, K> {
    private LruCache<T, K> mKernalCache;
    public static final int CACHE_SMALL = 1;
    public static final int CACHE_MIDDLE = 2;
    public static final int CACHE_LARGE = 3;
    private int mCacheMode = CACHE_MIDDLE;
    private int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private int mCachePageSize = maxMemory / 28;
    public AKCache() {
        initCache();
    }
    public AKCache(int sizeMode) {
        mCacheMode = sizeMode;
        initCache();
    }
    private void initCache() {

        mKernalCache = new LruCache<T, K>(mCacheMode * mCachePageSize) {
            @Override
            protected int sizeOf(T key, K value) {
                return akSizeOf(key, value);
            }
        };
    }
    public void put(T key, K value) {
        mKernalCache.put(key, value);
    }
    public K get(T key) {
        return mKernalCache.get(key);
    }
    abstract protected int akSizeOf(T key, K value);
}
