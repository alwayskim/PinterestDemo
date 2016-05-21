package wyz.whaley.pinterest.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DataUtils {
    // private static final int[] mResolutionHeight = { 200, 220, 240, 260, 280
    // };
    // private static final int[] mResolutionWidth = { 130, 140, 150, 160, 170
    // };
     private static final int[] mResolutionHeight = { 150 };
     private static final int[] mResolutionWidth = { 100 };

    private static Random random = new Random();

    public static int getRandomWidth() {
        return mResolutionWidth[random.nextInt(mResolutionWidth.length)];
    }

    public static int getRandomHeight() {
        return mResolutionHeight[random.nextInt(mResolutionHeight.length)];
    }


    public static String dealHttpParams(Map<String, String> params) {
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

    public static String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static byte[] convertToByteArray(InputStream in){
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        try {
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

}
