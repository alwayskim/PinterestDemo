package wyz.whaley.pinterest.maneger;

import android.content.Context;
import android.content.Intent;

import wyz.whaley.pinterest.ui.BaseDetailActivity;
import wyz.whaley.pinterest.ui.DownLoadedImageActivity;
import wyz.whaley.pinterest.ui.ImageActivity;
import wyz.whaley.pinterest.ui.ShotDetailActivity;

/**
 * Created by alwayking on 16/3/10.
 */
public class ActivityManeger {
    private static final String TAG = "ActivityManeger";

    public static final String INTENT_SHOT = "shot";

    public static final int DETAIL = 1;

    public static final int IMAGE = 2;

    public static final int DOWNLOAD = 3;

    public static void jump(Context context, int activityID,Intent intent) {
        Class target = getTarget(activityID);
        if (target != null) {
            if (intent == null){
                intent = new Intent();
            }
            intent.setClass(context, target);
            context.startActivity(intent);
        }
    }

    public static Class getTarget(int id) {
        switch (id) {
            case DETAIL:
                return ShotDetailActivity.class;
            case IMAGE:
                return ImageActivity.class;
            case DOWNLOAD:
                return DownLoadedImageActivity.class;
        }
        return null;
    }
}
