package wyz.whaley.pinterest.widget;

import android.view.View;

public interface MyBaseAdapter {
    View getView(View v, int position);

    int getCount();

    Object getItem(int position);
}
