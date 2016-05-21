package wyz.whaley.pinterest;

import android.app.Application;
import android.content.Context;

/**
 * Created by alwayking on 16/4/9.
 */
public class MyApplication extends Application{
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
