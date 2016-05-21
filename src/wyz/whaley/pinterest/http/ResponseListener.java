package wyz.whaley.pinterest.http;

public interface ResponseListener {
    void onSuccess(Object in);

    void onFailed(Exception e);
}
