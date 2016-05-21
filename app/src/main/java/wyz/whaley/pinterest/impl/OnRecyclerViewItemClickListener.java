package wyz.whaley.pinterest.impl;

import android.view.View;

import wyz.whaley.pinterest.data.ShotInfo;

/**
 * Created by alwayking on 16/3/10.
 */
public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, ShotInfo shotInfo);
}
