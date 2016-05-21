package wyz.whaley.pinterest.http.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alwayking on 16/4/6.
 */
public class AKCacheTask {


    private static AKCacheTask instance;
    private ExecutorService cacheService;

    private AKCacheTask() {
        cacheService = Executors.newSingleThreadExecutor();
    }

    public static AKCacheTask getInstance() {
        if (instance == null) {
            synchronized (AKGifImageLoader.class) {
                if (instance == null) {
                    instance = new AKCacheTask();
                }
            }
        }
        return instance;
    }

    public static AKCacheTask getInstace() {
        if (instance == null) {
            synchronized (AKCacheTask.class) {
                if (instance == null) {
                    instance = new AKCacheTask();
                }
            }
        }
        return instance;
    }

    public void add(Runnable runnable) {
        cacheService.execute(runnable);
    }

    public void quit() {
        cacheService.shutdown();
    }
}
