package wyz.whaley.pinterest.http.utils;

import org.json.JSONException;
import org.json.JSONObject;

import wyz.whaley.pinterest.http.Request;
import wyz.whaley.pinterest.http.ResponseListener;

import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONRequestNew extends Request {

    private static final String TAG = "JSONRequest";

    private Context context;

    public JSONRequestNew(String url, ResponseListener listener, int requestMode, Context context) {
        super(url, listener, requestMode);
        this.context = context;
    }

    @Override
    public void dispatcherResult(InputStream in) {
        // TODO Auto-generated method stub
        if (this.listener != null) {
            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(convertToString(in));
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            this.listener.onSuccess(convertToString(in));
        }
    }

    private byte[] convertToByteArray(InputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        int length = 0;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            inputStream.close();
            baos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos.toByteArray();
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
