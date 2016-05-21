package wyz.whaley.pinterest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class ItemView extends FrameLayout {

    private static final String TAG = "ItemView";

    private static int mCountLayout = 0;
    private static int mCountMeasure = 0;

    private static int mItemIdCount = 0;
    private int mItemId;

    public ItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mItemId = mItemIdCount++;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO Auto-generated method stub
        if (mItemId == 1) {
            Log.w(TAG, "Item view onLayout ID : " + mItemId);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mItemId == 1) {
            Log.i(TAG, "Item view onMeasure ID : " + mItemId);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
