package wyz.whaley.pinterest.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import wyz.whaley.pinterest.R;

/**
 * Created by alwayking on 16/4/7.
 */
public class AKShareDialog extends Dialog {

    private static final String TAG = "ShareDialog";

    private String mImagePath; //图像文件路径
    private String mUrl;  //图像url地址
    private Activity mActivity;

    List<ResolveInfo> mLaunchables;  //具有分享功能的应用列表

    public static final int SHARE_CODE = 2;

    private GridView mGridView; //网格列表

    public AKShareDialog(Activity context, String imagePath, String url) {
        super(context);
        this.mActivity = context;
        this.mUrl = url;
        this.mImagePath = imagePath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.share_dialog);
        setDialogSize();
        initView();
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.share_gv);
        final Intent intent = new Intent(Intent.ACTION_SEND);
        requestApplication(intent);
        mGridView.setAdapter(new ShareAdapter());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ActivityInfo activity = mLaunchables.get(i).activityInfo;
                ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                        activity.name);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(name);
                mActivity.startActivityForResult(intent, SHARE_CODE);
            }
        });

        Log.i(TAG, "dismiss");
    }

    private void requestApplication(Intent intent) {
        File diskFile = null;
        if (mImagePath != null) {
            diskFile = new File(mImagePath);
        }
        if (diskFile != null && diskFile.exists() && diskFile.isFile()) {
            intent.setType("image/*");
            Uri u = Uri.fromFile(diskFile);
            intent.putExtra(Intent.EXTRA_STREAM, u);
            intent.putExtra("sms_body", mUrl);
        } else {
            intent.setType("text/plain"); // 纯文本
        }
        intent.putExtra(Intent.EXTRA_TEXT, mUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager pm = getContext().getPackageManager();
        mLaunchables = pm.queryIntentActivities(intent, 0);
    }

    @Override
    public void show() {
        super.show();
    }

    private void setDialogSize() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().setGravity(Gravity.CENTER);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        lp.height = (int) (d.getHeight() * 0.80);
        lp.width = (int) (d.getWidth() * 0.85);
        getWindow().setAttributes(lp);
    }


    private class ShareAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public ShareAdapter() {
            super();
            mInflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mLaunchables.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view != null) {
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = mInflater.inflate(R.layout.share_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mTitleTV = (TextView) view.findViewById(R.id.title_tv);
                viewHolder.mIconIV = (ImageView) view.findViewById(R.id.icon_iv);
                view.setTag(viewHolder);
            }
            viewHolder.mTitleTV.setText(mLaunchables.get(i).loadLabel(getContext().getPackageManager()));
            viewHolder.mIconIV.setImageDrawable(mLaunchables.get(i).loadIcon(getContext().getPackageManager()));
            return view;
        }

        class ViewHolder {
            public TextView mTitleTV;
            public ImageView mIconIV;
        }
    }

}
