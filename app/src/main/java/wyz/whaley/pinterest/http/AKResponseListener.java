package wyz.whaley.pinterest.http;

public interface AKResponseListener {
    void onSuccess(Object in);

    void onFailed(Exception e);
}
