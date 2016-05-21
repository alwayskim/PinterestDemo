package wyz.whaley.pinterest.http;

import wyz.whaley.pinterest.http.Request.RequestFinishListener;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpTask implements RequestFinishListener {

    private static HttpTask instance;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    // private ExecutorService addRequestExecutor =
    // Executors.newSingleThreadExecutor();

    private ArrayList<Request> currentRequests = new ArrayList<Request>();

    private static final String TAG = "HttpTask";

    private HttpTask() {
    }

    public static HttpTask getInstace() {
        if (instance == null) {
            synchronized (HttpRequestHelp.class) {
                if (instance == null) {
                    instance = new HttpTask();
                }
            }
        }
        return instance;
    }

    // public void doRequest(String url, ResponseListener listener, int
    // requestMode) {
    // Runnable runnable = new HttpRunnable(url, listener, requestMode);
    // Log.i(TAG, "before dorequest thread: " + Thread.currentThread());
    // executorService.execute(runnable);
    // }

    public void doRequest(Request request) {

        Log.i(TAG, "before dorequest thread: " + Thread.currentThread());
        request.setHttpTask(this);
        executorService.execute(request);
    }

    public void addToRequestQueue(final Request request) {
        synchronized (this) {
            for (Request r : currentRequests) {
                if (r.equals(request)) {
                    if (r.listenersList == null) {
                        r.listenersList = new ArrayList<ResponseListener>();
                    }
                    r.listenersList.add(request.listener);
                    return;
                }
            }
            request.setHttpTask(this);
            currentRequests.add(request);
            executorService.execute(request);
        }

    }

    @Override
    public void onRequestFinish(Request request) {
        synchronized (this) {
            currentRequests.remove(request);
        }
    }
}
