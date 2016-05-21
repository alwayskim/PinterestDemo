package wyz.whaley.pinterest.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {

    public static final String PREFERRENCE_SETTING = "preference_setting";

    public static final String FIRST_OPEN = "first_open";

    public static final String ITEM_SUM = "item_sum";


    public static final String COMMON_PREFERENCE = "wyz.whaley.pinterest";

    public static final String COMMON_TOKEN = "access_token";


    // 写入或更新数据
    public void put(Context context, String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(COMMON_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }
    //读取数据
    public static Object get(Context context, String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(COMMON_PREFERENCE, Context.MODE_PRIVATE);

        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    //删除单个键值
    public static void delete(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(COMMON_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
    //清空所有键值
    public static void clear(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(COMMON_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
