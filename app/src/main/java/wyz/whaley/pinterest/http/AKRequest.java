package wyz.whaley.pinterest.http;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public abstract class AKRequest implements AKDispatcher, Comparable<AKRequest> {
    private static final String TAG = "RequestClass";
    public String mUrl; //请求URL
    protected AKResponseListener mListener; //请求结果监听
    protected ArrayList<AKResponseListener> mListenersList; //多个相同请求的结果监听List
    protected int mRequestMode; //请求模式GET、POST等
    protected AKHttpTask mHttpTask;
    protected Map<String, String> mMapParams; //请求参数
    private int mId;
    private Priority mPriority;
    private int mTimeOut = 5000;
    protected Context mContext;
    private boolean mIsCanceled; //判断请求是否被取消

    public enum Priority {  //请求优先级
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public AKRequest(String url, AKResponseListener listener, int requestMode, Context context) {
        initRequest(url, listener, requestMode, Priority.NORMAL, context);
    }

    public AKRequest(String url, AKResponseListener listener, int requestMode, Priority priority, Context context) {
        initRequest(url, listener, requestMode, priority, context);
    }

    private void initRequest(String url, AKResponseListener listener, int requestMode, Priority priority, Context context) {
        this.mUrl = url;
        this.mListener = listener;
        this.mRequestMode = requestMode;
        this.mMapParams = new HashMap<>();
        this.mIsCanceled = false;
        this.mPriority = priority;
        this.mContext = context;
    }

    public boolean isCanceled() {
        return mIsCanceled;
    }

    public void setIsCanceled(boolean isCanceled) {
        this.mIsCanceled = isCanceled;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void finishSelf() {
        if (mHttpTask != null) {
            mHttpTask.onRequestFinish(this);
        }
    }

    public int getTimeOut() {
        return mTimeOut;
    }

    public void setTimeOut(int mTimeOut) {
        this.mTimeOut = mTimeOut;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public int getId() {
        return mId;
    }

    public void setHttpTask(AKHttpTask mHttpTask) {
        this.mHttpTask = mHttpTask;
    }

    public AKHttpTask getHttpTask() {
        return mHttpTask;
    }

    public void setParams(Map<String, String> params) {
        this.mMapParams.putAll(params);
    }

    public Map<String, String> getParams() {
        return mMapParams;
    }

    public void run() {
        if (!mIsCanceled) {
            AKHttpRequestEngine.getInstace().doRequest(this);
        } else {
            Log.i(TAG, "Request has canceled!!");
            finishSelf();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof AKRequest) {
            AKRequest request = (AKRequest) o;
            return request.mUrl.equals(this.mUrl) && (request.mRequestMode == this.mRequestMode) && this.mMapParams.equals(request.mMapParams);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mUrl.hashCode();
    }

    @Override
    public int compareTo(AKRequest other) {

        Priority left = this.getPriority();
        Priority right = other.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.mId - other.mId :
                right.ordinal() - left.ordinal();
    }
}
