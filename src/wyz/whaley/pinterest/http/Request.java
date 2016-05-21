package wyz.whaley.pinterest.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Request implements Runnable, Dispatcher {

    protected String url;

    protected ResponseListener listener;

    protected ArrayList<ResponseListener> listenersList;

    protected int requestMode;

    protected HttpTask httpTask;

    protected HashMap<String, String> mapParams = new HashMap<String, String>();

    public Request(String url, ResponseListener listener, int requestMode) {
        this.url = url;
        this.listener = listener;
        this.requestMode = requestMode;
    }

    public void setHttpTask(HttpTask httpTask) {
        this.httpTask = httpTask;
    }

    public HttpTask getHttpTask() {
        return httpTask;
    }
    
    public void setParams(HashMap<String, String> params) {
        this.mapParams = params;
    }
    
    public HashMap<String, String> getParams() {
        return mapParams;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        HttpRequestHelp.getInstace().doRequest(url, listener, requestMode, this);
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        if (o instanceof Request) {
            Request request = (Request) o;
            return request.url.equals(this.url) && (request.requestMode == this.requestMode) && this.mapParams.equals(request.mapParams);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return url.hashCode();
    }

    public interface RequestFinishListener {
        void onRequestFinish(Request request);
    }
}
