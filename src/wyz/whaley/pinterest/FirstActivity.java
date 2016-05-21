package wyz.whaley.pinterest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wyz.whaley.pinterest.data.BaseUser;
import wyz.whaley.pinterest.http.HttpRequestHelp;
import wyz.whaley.pinterest.http.HttpTask;
import wyz.whaley.pinterest.http.ImageLoader;
import wyz.whaley.pinterest.http.Request;
import wyz.whaley.pinterest.http.utils.JSONRequestNew;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ImageView;

public class FirstActivity extends Activity {
    private static final String TAG = "FirstActivity";

    private ImageView mAvatarIV;

    private BaseUser mUser;

    private DrawerLayout mDrawerLayout;

    String url1 = "https://api.dribbble.com/v1/user?access_token=98b9afaaf62fc652291eb7102d8daa0ca2933853f2998647748d79df60d0c06d";
    String accessToken = "98b9afaaf62fc652291eb7102d8daa0ca2933853f2998647748d79df60d0c06d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.first_activity);
        initView();
        // initUser();
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mAvatarIV = (ImageView) findViewById(R.id.avatar_iv);

    }

    private void initUser() {
        Request request = new JSONRequestNew(url1, new wyz.whaley.pinterest.http.ResponseListener() {
            @Override
            public void onSuccess(Object in) {
                Gson gson = new GsonBuilder().create();
                String string = (String) in;
                mUser = gson.fromJson(string, BaseUser.class);
                Log.i(TAG, "Avatar_rul = " + mUser.getAvatar_url());
                mAvatarIV.setTag(mUser.getAvatar_url());
                ImageLoader.getInstance().displayImage(FirstActivity.this, mAvatarIV, mUser.getAvatar_url(), 0, false);
            }

            @Override
            public void onFailed(Exception e) {

            }
        }, HttpRequestHelp.REQUEST_GET, this);
        HttpTask.getInstace().addToRequestQueue(request);
    }
}
