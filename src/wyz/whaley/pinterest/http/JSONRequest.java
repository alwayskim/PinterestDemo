package wyz.whaley.pinterest.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wyz.whaley.pinterest.db.ItemInfo;
import wyz.whaley.pinterest.utils.DataUtils;
import wyz.whaley.pinterest.utils.PreferenceUtility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class JSONRequest extends Request {

    private static final String TAG = "JSONRequest";

    private int sum = 0;

    private Context context;

    public JSONRequest(String url, ResponseListener listener, int requestMode, Context context) {
        super(url, listener, requestMode);
        this.context = context;
    }

    @Override
    public void dispatcherResult(InputStream in) {
        // TODO Auto-generated method stub
        if (this.listener != null) {
            this.listener.onSuccess(praseJsonArray(in));
        }
    }

    // 解析测试服务器的json
    private List<ItemInfo> praseJsonArray(InputStream inputStream) {
        byte[] jsonBytes = convertToByteArray(inputStream);
        String jsonString = new String(jsonBytes);
        List<ItemInfo> list = new LinkedList<ItemInfo>();
        try {
            JSONObject json = new JSONObject(jsonString);
            sum = json.getInt("sum") + 1;
            SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceUtility.PREFERRENCE_SETTING, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PreferenceUtility.ITEM_SUM, sum).commit();
            Log.i(TAG, "SUM : " + String.valueOf(sum));
            JSONArray jsonArray = json.getJSONArray("result");
            for (int i = 0; i < jsonArray.length() + 1; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                long id = object.getInt(ItemInfo.ID);
                String imageName = object.getString(ItemInfo.IMAGE_NAME);
                String imageURL = object.getString(ItemInfo.IMAGE_URL);
                list.add(new ItemInfo(id, imageName, imageURL, DataUtils.getRandomWidth(), DataUtils.getRandomHeight()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
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

}
