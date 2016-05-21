package wyz.whaley.pinterest.http;

import android.util.Log;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import wyz.whaley.pinterest.http.utils.AKHttpConstant;
import wyz.whaley.pinterest.utils.DataUtils;

public class AKHttpRequestEngine {

    private static AKHttpRequestEngine instance;
    public static final int REQUEST_POST = 1;
    public static final int REQUEST_GET = 2;


    private static final String TAG = "HttpRequestEngine";

    private AKHttpRequestEngine() {
    }

    public static AKHttpRequestEngine getInstace() {
        if (instance == null) {
            synchronized (AKHttpRequestEngine.class) {
                if (instance == null) {
                    instance = new AKHttpRequestEngine();
                }
            }
        }
        return instance;
    }

    public void doRequest(AKRequest request) {
        if (!request.isCanceled()) {
            startRequest(request);
        } else {
            request.finishSelf();
        }
    }

    private void startRequest(AKRequest request) {
        HttpURLConnection urlConnection = getHttpURLConnection(request);
        int responseCode = 0;
        try {
            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = urlConnection.getInputStream();
                if (request != null) {
                    if (!request.isCanceled()) {
                        request.dispatcherResult(in);
                    } else if (request.isCanceled()) {
                        Log.i(TAG, "request has canceled.. " + urlConnection.getResponseCode());
                    }
                }
                in.close();
            } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {

            } else {
                request.mListener.onFailed(new Exception());
            }
            request.finishSelf();
        } catch (IOException e) {
            Log.e(TAG, "Catch Exception : " + e.toString());
            request.finishSelf();
            request.mListener.onFailed(e);
        }
    }

    private HttpURLConnection getHttpURLConnection(AKRequest request) {
        HttpURLConnection urlConnection = null;
        URL urlURL = null;
        String url = request.getUrl();
        if (request.mRequestMode == REQUEST_GET && request.getParams().size() > 0) {
            url += DataUtils.dealHttpParams(request.getParams());
        }
        try {
            urlURL = new URL(url);
            urlConnection = (HttpURLConnection) urlURL.openConnection();
            switch (request.mRequestMode) {
                case REQUEST_GET:
                    urlConnection.setRequestMethod("GET");
                    break;
                case REQUEST_POST:
                    urlConnection.setRequestMethod("POST");
                    break;
                default:
                    break;
            }
            //设置请求超时时间
            urlConnection.setConnectTimeout(request.getTimeOut());
            //Dribbble Auth2.0认证
            String token = null;
            if (AKHttpConstant.USER_ACCESS_TOKEN != null) {
                token = AKHttpConstant.USER_ACCESS_TOKEN;
            } else {
                token = AKHttpConstant.APPLICATION_ACCESS_TOKEN;
            }
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);

            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            //写入POST请求所上传的数据
            if (request.mRequestMode == REQUEST_POST && request.getParams().size() > 0) {
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(DataUtils.getPostDataString(request.getParams()));
                writer.flush();
                writer.close();
                os.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlConnection;
    }
}
