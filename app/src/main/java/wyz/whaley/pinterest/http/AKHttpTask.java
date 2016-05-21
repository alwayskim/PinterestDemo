package wyz.whaley.pinterest.http;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class AKHttpTask {

    private static AKHttpTask instance;

    private Map<String, AKRequest> currentRequestMap = new HashMap<>(); //当前所有请求实例

    private PriorityBlockingQueue<AKRequest> realQueue = new PriorityBlockingQueue<>(); //请求实例队列

    private AtomicInteger mIDGenerator = new AtomicInteger(); //用于标注请求唯一标识符

    private AKRequestThread[] mThreads; //请求线程数组

    private final int KERNEL_COUNT = 4;

    private static final String TAG = "HttpTask";

    private AKHttpTask() {
        mThreads = new AKRequestThread[KERNEL_COUNT];
        for (int i = 0; i < KERNEL_COUNT; i++) {
            mThreads[i] = new AKRequestThread(realQueue);
            mThreads[i].start();
        }
    }

    public static AKHttpTask getInstace() {
        if (instance == null) {
            synchronized (AKHttpRequestEngine.class) {
                if (instance == null) {
                    instance = new AKHttpTask();
                }
            }
        }
        return instance;
    }

    public void add(final AKRequest request) {
        boolean canAdd = true;
        request.setHttpTask(this);
        synchronized (this) {
            if (currentRequestMap.containsKey(request.getUrl())) {
                currentRequestMap.get(request.getUrl()).mListenersList.add(request.mListener);
                canAdd = false;
            }
        }
        if (canAdd) {
            request.setId(getID());
            realQueue.add(request);
        }
    }


    public void onRequestFinish(AKRequest request) {
        synchronized (this) {
            currentRequestMap.remove(request.getUrl());
            Log.i(TAG, "currentRequestMap.size() = " + currentRequestMap.size());
        }
    }

    public int getID() {
        return mIDGenerator.incrementAndGet();
    }
}
