package wyz.whaley.pinterest.ui;

import wyz.whaley.pinterest.R;
import wyz.whaley.pinterest.db.DBHelp;
import wyz.whaley.pinterest.db.DBTask;
import wyz.whaley.pinterest.db.DBTask.DBChangeListener;
import wyz.whaley.pinterest.db.ItemInfo;
import wyz.whaley.pinterest.http.AKHttpRequestEngine;
import wyz.whaley.pinterest.http.AKHttpTask;
import wyz.whaley.pinterest.http.AKJSONRequest;
import wyz.whaley.pinterest.http.AKRequest;
import wyz.whaley.pinterest.http.AKResponseListener;
import wyz.whaley.pinterest.utils.DataUtils;
import wyz.whaley.pinterest.utils.SharedPreferenceUtils;
import wyz.whaley.pinterest.widget.CustomPinterest;
import wyz.whaley.pinterest.widget.CustomPinterest.PinterestCallBack;
import wyz.whaley.pinterest.widget.CustomPinterestTV;
import wyz.whaley.pinterest.widget.MyBaseAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final String SERVER_URL = "http://172.16.106.45/study1/";
    // private static final String SERVER_URL =
    // "http://172.16.210.102:8080/examples/";

    private static final int PAGE_SIZE = 20;

    private boolean mIsLoadingMore = false;

    private boolean mIsTV = true;

    private int mItemCount;

    private LinkedList<ItemInfo> mItemList = new LinkedList<ItemInfo>();

    private CustomPinterest mPinterest;

    private CustomPinterestTV mPinterestTV;

    private MyAdapter mAdapter;

    private DBHelp mDBHelp;
    private DBTask mDBTask;

    private Handler mHandler = new Handler();

    private boolean mIsOpenFirst;

    private int mCurrentPage = 1;

    int count = 6;

    int mGenerateCount = 3000;

    private String[] urlStrings = { "http://avatar.csdn.net/C/6/8/1_bz419927089.jpg",
            "http://i1.dpfile.com/pc/d4f0f05c2b38484a8f841c5c35c3af4d%28700x700%29/thumb.jpg",
            "http://d.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=2ae8d91987025aafd36776cfcedd8752/9d82d158ccbf6c817bf1b331b93eb13532fa4021.jpg",
            "http://www.yyccar.cn/images/nfwwolt2mnxw63bomnxa/community/011f21552f7f9e000000c50042fea4.jpg",
            "http://img2.imgtn.bdimg.com/it/u=2857932075,999367391&fm=15&gp=0.jpg", "http://img2.imgtn.bdimg.com/it/u=1138406655,2399657813&fm=15&gp=0.jpg",
            "http://www.qqzhi.com/uploadpic/2014-07-16/111811443.jpg", "http://wenwen.soso.com/p/20110131/20110131192335-1013376730.jpg",
            "http://img2.imgtn.bdimg.com/it/u=3680762297,1807667461&fm=21&gp=0.jpg", "http://img2.imgtn.bdimg.com/it/u=2064387628,2019901804&fm=206&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=183734828,2229795965&fm=206&gp=0.jpg", "http://img0.imgtn.bdimg.com/it/u=790098218,638143216&fm=206&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3340999208,2685406284&fm=206&gp=0.jpg", "http://img1.imgtn.bdimg.com/it/u=3659841646,3041956753&fm=206&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=2384542948,2077708477&fm=206&gp=0.jpg", "http://img2.imgtn.bdimg.com/it/u=3147277467,4102538871&fm=206&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=70974367,1898294756&fm=206&gp=0.jpg", "http://img5.imgtn.bdimg.com/it/u=3767258876,282061377&fm=206&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=1836057116,3940729932&fm=206&gp=0.jpg", "http://img1.imgtn.bdimg.com/it/u=2316632241,3682596584&fm=206&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=3165159311,799786624&fm=206&gp=0.jpg", "http://img1.imgtn.bdimg.com/it/u=3786764465,884942022&fm=206&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=2857932075,999367391&fm=15&gp=0.jpg", "http://img0.imgtn.bdimg.com/it/u=1137572046,4100978860&fm=206&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=3666094918,1565989507&fm=206&gp=0.jpg", "http://img4.imgtn.bdimg.com/it/u=1124710306,1328595162&fm=206&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1956790230,2945275299&fm=206&gp=0.jpg", "http://img2.imgtn.bdimg.com/it/u=546811603,943746655&fm=206&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=132991976,3060010985&fm=206&gp=0.jpg" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDB();
        initPreference();
        initView();

        if (mIsOpenFirst) {
            mCurrentPage = 1;
            // requestData(mCurrentPage);
            generateItem();
            mDBHelp.insertItemList(mItemList);
            mDBTask.queryAll(mAdapter);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDBTask.queryAll(mAdapter);
                }
            }, 500);
        }
    }

    private void initPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceUtils.PREFERRENCE_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        mIsOpenFirst = sharedPreferences.getBoolean(SharedPreferenceUtils.FIRST_OPEN, true);
        if (mIsOpenFirst) {
            editor.putBoolean(SharedPreferenceUtils.FIRST_OPEN, false).commit();
        }
        mItemCount = sharedPreferences.getInt(SharedPreferenceUtils.ITEM_SUM, 0);
        Log.i(TAG, "itemCount : " + mItemCount);
    }

    private void initView() {
        mAdapter = new MyAdapter();
        if (mIsTV) {
            findViewById(R.id.pinterest).setVisibility(View.GONE);
            mPinterestTV = (CustomPinterestTV) findViewById(R.id.pinterestTV);
            mPinterestTV.setPinFocusEdgeResource(R.layout.pin_item_edge);
            mPinterestTV.setAdapter(mAdapter);
            mPinterestTV.setPinterestCallBack(new wyz.whaley.pinterest.widget.CustomPinterestTV.PinterestCallBack() {

                @Override
                public void refresh() {

                }

                @Override
                public void loadMore() {
                    mCurrentPage = mItemList.size() / PAGE_SIZE;
                    if (mItemList.size() % PAGE_SIZE > 0) {
                        mCurrentPage++;
                    }
                    if (!mIsLoadingMore && mCurrentPage < count) {
                        mIsLoadingMore = true;
                        mCurrentPage++;
                        requestData(mCurrentPage);
                    }
                }
            });
        } else {
            findViewById(R.id.pinterestTV).setVisibility(View.GONE);
            mPinterest = (CustomPinterest) findViewById(R.id.pinterest);
            mPinterest.setPinFocusEdgeResource(R.layout.pin_item_edge);
            mPinterest.setAdapter(mAdapter);
            mPinterest.setPinterestCallBack(new PinterestCallBack() {

                @Override
                public void refresh() {

                }

                @Override
                public void loadMore() {
                    mCurrentPage = mItemList.size() / PAGE_SIZE;
                    if (mItemList.size() % PAGE_SIZE > 0) {
                        mCurrentPage++;
                    }
                    if (!mIsLoadingMore && mCurrentPage < count) {
                        mIsLoadingMore = true;
                        mCurrentPage++;
                        requestData(mCurrentPage);
                    }
                }
            });
        }

    }

    private void initDB() {
        mDBHelp = DBHelp.getInstance(this);
        mDBTask = DBTask.getInstance(getApplicationContext());
    }

    private void generateItem() {
        for (long i = 0; i < mGenerateCount; i++) {
            ItemInfo itemObject = new ItemInfo();
            itemObject.setId(i);
            itemObject.setImageName("uh oh! this image's name is not determined.Just for using memory!");
            itemObject.setImageURL(urlStrings[(int) (i % urlStrings.length)]);
            itemObject.setWidth(DataUtils.getRandomWidth());
            itemObject.setHeight(DataUtils.getRandomHeight());
            mItemList.add(itemObject);
        }
    }

    private void requestData(int page) {
        String url = getURL(page);
        Log.i(TAG, "request page : " + url);
        AKRequest request = new AKJSONRequest(url, new AKResponseListener() {

            @Override
            public void onSuccess(Object in) {
                if (in instanceof List<?>) {
                    SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferenceUtils.PREFERRENCE_SETTING, Context.MODE_PRIVATE);
                    mItemCount = sharedPreferences.getInt(SharedPreferenceUtils.ITEM_SUM, 0);
                    mDBTask.insert((List<ItemInfo>) in, mAdapter);
                }
            }

            @Override
            public void onFailed(final Exception e) {
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, AKHttpRequestEngine.REQUEST_GET, this);
//        AKHttpTask.getInstace().addToRequestQueue(request);
        AKHttpTask.getInstace().add(request);
    }

    private String getURL(int page) {
        StringBuilder stringBuilder = new StringBuilder(SERVER_URL);
        stringBuilder.append("page").append(page).append(".json");
        return stringBuilder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            final EditText editText = new EditText(this);
            new AlertDialog.Builder(this).setTitle("columns").setView(editText).setPositiveButton("ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        int columns = Integer.valueOf(editText.getText().toString());
                        if (columns > 0 && columns < 6 && columns != mPinterest.getColumn()) {
                            mPinterest.setColumn(columns);
                        }
                    } catch (Exception e) {
                    }
                }
            }).setNegativeButton("cancel", null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private class MyAdapter implements MyBaseAdapter, DBChangeListener {

        LayoutInflater inflater;
        Drawable defaultDrawable;

        public MyAdapter() {
            inflater = LayoutInflater.from(MainActivity.this);
            // defaultDrawable = getResources().getDrawable(R.d);
        }

        @Override
        public View getView(View v, int position) {
            ViewHolder viewHolder = null;
            String url = mItemList.get(position).getImageURL();
            if (v == null) {
                viewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.grid_item, null);
                viewHolder.textView = (TextView) v.findViewById(R.id.item_tv);
                viewHolder.imageView = (ImageView) v.findViewById(R.id.item_giv);
                v.setTag(viewHolder);
                Log.i(TAG, "v is null , pos : " + position);
            } else {
                viewHolder = (ViewHolder) v.getTag();
                viewHolder.imageView.clearAnimation();
                viewHolder.imageView.setImageDrawable(null);
                Log.i(TAG, "v is not null , pos : " + position);
            }
            viewHolder.textView.setText(String.valueOf(mItemList.get(position).getId()));
            if (!url.equals("")) {
                boolean isFling = false;
                viewHolder.imageView.setTag(url);
                if (mIsTV) {
                    isFling = mPinterestTV.getTouchMode() == CustomPinterestTV.MODE_FLING;
                } else {
                    isFling = mPinterest.getTouchMode() == CustomPinterest.MODE_FLING;
                }
//                ImageLoader.getInstance().displayImage(MainActivity.this, viewHolder.imageView, url, isFling, false);
            }
            return v;
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public void listAfterDBChange(final List<?> list) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mItemList = (LinkedList<ItemInfo>) list;
                    if (mIsTV) {
                        mPinterestTV.dataChangeNotification();
                    } else {
                        mPinterest.dataChangeNotification();
                    }
                    mIsLoadingMore = false;
                }
            });
        }

    }

    public class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}
