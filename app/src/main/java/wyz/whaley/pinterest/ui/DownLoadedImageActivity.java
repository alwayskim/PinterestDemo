package wyz.whaley.pinterest.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wyz.whaley.pinterest.MyApplication;
import wyz.whaley.pinterest.R;
import wyz.whaley.pinterest.data.ShotInfo;
import wyz.whaley.pinterest.db.table.AKBaseTable;
import wyz.whaley.pinterest.db.table.DownloadedTable;
import wyz.whaley.pinterest.http.utils.AKCacheTask;
import wyz.whaley.pinterest.maneger.ActivityManeger;
import wyz.whaley.pinterest.utils.BitmapUtils;

public class DownLoadedImageActivity extends BasicActivity {

    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_EDIT = 2;
    private static final String TAG = "DownLoadedImageActivity";

    private GridView mGridView;

    private List<ShotInfo> mDownloadedList = new LinkedList<>();

    private TextView mTextView;

    private Toolbar mToolBar;

    private View mSelectBar;

    private TextView mAllTextView;

    private TextView mCountTextView;

    private TextView mDeleteTextView;

    private Map<Integer, ShotInfo> mSelectPosMap = new HashMap();

    private DownLoadAdapter mDownLoadAdapter;

    private Handler mHandler = new Handler();

    private int mScreenWidth;

    private int mStatus = STATUS_NORMAL;

    private int mWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_loaded_image);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
//        mScreenWidth = metrics.widthPixels;
        mScreenWidth = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;

        initView();
        initData();
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.download_gv);
        mTextView = (TextView) findViewById(R.id.empty_tv);
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mSelectBar = findViewById(R.id.download_select_bar);
        mAllTextView = (TextView) findViewById(R.id.download_all_tv);
        mCountTextView = (TextView) findViewById(R.id.download_select_count_tv);
        mDeleteTextView = (TextView) findViewById(R.id.download_delete_tv);
        if (mToolBar != null) {
            mToolBar.setTitle("Downloaded Images");
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.drawable.ic_action_back);
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownLoadedImageActivity.this.finish();
                }
            });
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mDownLoadAdapter = new DownLoadAdapter();
        mGridView.setAdapter(mDownLoadAdapter);
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mStatus = STATUS_EDIT;
                mSelectBar.setVisibility(View.VISIBLE);
                if (!mSelectPosMap.containsKey(i)) {
                    mSelectPosMap.put(i, mDownloadedList.get(i));
                } else {
                    mSelectPosMap.remove(i);
                }
                mCountTextView.setText(String.valueOf(mSelectPosMap.size()));
                mDownLoadAdapter.notifyDataSetChanged();
                return true;
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mStatus == STATUS_NORMAL) {
                    Intent intent = new Intent();
                    intent.putExtra(ActivityManeger.INTENT_SHOT, mDownloadedList.get(position));
                    ActivityManeger.jump(DownLoadedImageActivity.this, ActivityManeger.DETAIL, intent);
                } else if (mStatus == STATUS_EDIT) {
                    if (!mSelectPosMap.containsKey(position)) {
                        mSelectPosMap.put(position, mDownloadedList.get(position));
                    } else {
                        mSelectPosMap.remove(position);
                    }
                    mCountTextView.setText(String.valueOf(mSelectPosMap.size()));
                    Log.i(TAG, "click.....");
                    mDownLoadAdapter.notifyDataSetChanged();
                }
            }
        });

        mDeleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStatus == STATUS_EDIT) {
                    AKCacheTask.getInstace().add(new Runnable() {
                        @Override
                        public void run() {
                            List<ShotInfo> deleteList = new LinkedList();
                            deleteList.addAll(mSelectPosMap.values());
                            DownloadedTable.getInstance(MyApplication.getContext()).deleteList(deleteList, new AKBaseTable.DBCallBack() {
                                @Override
                                public void queryComplete(List<ShotInfo> list) {
                                    initData();
                                }
                            });
                        }
                    });
                }
            }
        });

        mAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStatus == STATUS_EDIT) {
                    for(int i = 0; i < mDownloadedList.size();i++){
                        if (!mSelectPosMap.containsKey(i)) {
                            mSelectPosMap.put(i, mDownloadedList.get(i));
                        }
                    }
                    mCountTextView.setText(String.valueOf(mSelectPosMap.size()));
                    mDownLoadAdapter.notifyDataSetChanged();
                }
            }
        });
        mSelectBar.setVisibility(View.GONE);
    }

    private void initData() {
        mStatus = STATUS_NORMAL;
        AKCacheTask.getInstace().add(new Runnable() {
            @Override
            public void run() {
                final List<ShotInfo> list = DownloadedTable.getInstance(MyApplication.getContext()).query(new AKBaseTable.DBCallBack() {
                    @Override
                    public void queryComplete(List<ShotInfo> list) {

                    }
                });
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadedList.clear();
                        mSelectPosMap.clear();
                        mSelectBar.setVisibility(View.GONE);
                        mDownloadedList = list;
                        if (list.size() > 0) {
                            mTextView.setVisibility(View.GONE);

                        } else {
                            mTextView.setVisibility(View.VISIBLE);
                        }
                        mDownLoadAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void setStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.ColorPrimaryDark));
        }
    }

    private class DownLoadAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        DownLoadAdapter() {
            mLayoutInflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mDownloadedList.size();
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
            final ShotInfo shotInfo = mDownloadedList.get(i);

            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = mLayoutInflater.inflate(R.layout.download_item, null);
                viewHolder.mImageView = (ImageView) view.findViewById(R.id.download_iv);
                viewHolder.mSelectView = view.findViewById(R.id.select_view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final String filePath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getPath() + "/AKBubble/" + shotInfo.getTitle() + "." + (shotInfo.isAnimated() ? "gif" : "jpg");
            viewHolder.mImageView.setTag(filePath);
            if (mStatus == STATUS_EDIT && mSelectPosMap.containsKey(i)) {
                viewHolder.mSelectView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mSelectView.setVisibility(View.GONE);
            }
            AKCacheTask.getInstace().add(new Runnable() {
                @Override
                public void run() {

                    File file = new File(filePath);
                    if (file.exists()) {
                        int width = mScreenWidth / 3;
                        if (viewHolder.mImageView != null && viewHolder.mImageView.getWidth() > 0) {
                            width = viewHolder.mImageView.getWidth();
                        }
                        final Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(filePath, width, width * 3 / 4);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (viewHolder.mImageView.getTag().equals(filePath)) {
                                    viewHolder.mImageView.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }
                }
            });
//            viewHolder.mImageView.setImageBitmap();
            return view;
        }

        private class ViewHolder {
            public ImageView mImageView;
            public View mSelectView;
        }
    }
}
