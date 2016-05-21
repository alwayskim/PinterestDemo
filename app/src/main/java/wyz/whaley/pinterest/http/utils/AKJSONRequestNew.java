package wyz.whaley.pinterest.http.utils;

import wyz.whaley.pinterest.http.AKRequest;
import wyz.whaley.pinterest.http.AKResponseListener;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AKJSONRequestNew extends AKRequest {

    private static final String TAG = "JSONRequest";


    public AKJSONRequestNew(String url, AKResponseListener listener, int requestMode, Context context) {
        super(url, listener, requestMode, context);
    }

    @Override
    public void dispatcherResult(InputStream in) {
        if (this.mListener != null) {
            this.mListener.onSuccess(convertToString(in));
        }
    }

    private String convertToString(InputStream in) {
        StringBuilder sb = new StringBuilder();
        String content;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(in);
            br = new BufferedReader(isr);
            while ((content = br.readLine()) != null) {
                sb.append(content);
            }
            isr.close();
            br.close();
        } catch (IOException ioe) {
        } finally {

        }
        return sb.toString();
    }

}
