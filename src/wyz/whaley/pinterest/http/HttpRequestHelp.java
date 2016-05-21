package wyz.whaley.pinterest.http;

import org.apache.http.HttpException;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpRequestHelp {

    private static HttpRequestHelp instance;
    public static final int REQUEST_POST = 1;
    public static final int REQUEST_GET = 2;
    
    private final String ACESS_TOKEN = "98b9afaaf62fc652291eb7102d8daa0ca2933853f2998647748d79df60d0c06d";

    private static final String TAG = "HttpRequestHelp";

    private HttpRequestHelp() {
    }

    public static HttpRequestHelp getInstace() {
        if (instance == null) {
            synchronized (HttpRequestHelp.class) {
                if (instance == null) {
                    instance = new HttpRequestHelp();
                }
            }
        }
        return instance;
    }

    public void doRequest(String url, ResponseListener listener, int mode, Dispatcher dispatcher) {
        switch (mode) {
        case REQUEST_POST:
            doPost();
            break;
        case REQUEST_GET:
            doGet(url, listener, dispatcher);
            break;
        default:
            break;
        }
    }

    private void doGet(String url, ResponseListener listener, Dispatcher dispatcher) {
        HttpURLConnection urlConnection = null;
        try {
            if (((Request) dispatcher).getParams().size() > 0) {
                url = url + doParamsForGET(((Request) dispatcher).getParams());
            }
            URL urlURL = new URL(url);
            Log.i(TAG, url);
            urlConnection = (HttpURLConnection) urlURL.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            Log.i(TAG, "urlConnection.getResponseCode() = " + urlConnection.getResponseCode());
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = urlConnection.getInputStream();
                // simulate slow http request
                // Thread.sleep(1000);
                if (dispatcher != null) {
                    dispatcher.dispatcherResult(in);
                    ((Request) dispatcher).getHttpTask().onRequestFinish(((Request) dispatcher));
                }
            } else {
                listener.onFailed(new HttpException());
            }
        } catch (Exception e) {
            Log.e(TAG, "Catch Exception : " + e.toString());
            ((Request) dispatcher).getHttpTask().onRequestFinish(((Request) dispatcher));
            listener.onFailed(e);
        } finally {
            urlConnection.disconnect();
        }
    }

    private String doParamsForGET(HashMap<String, String> params) {
        StringBuilder paramString = new StringBuilder();
        paramString.append("?");
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            paramString.append(key).append("=").append(val).append("&");
        }
        paramString.deleteCharAt(paramString.length() - 1);
        return paramString.toString();
    }

    private void doPost() {

    }
}
