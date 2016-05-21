package wyz.whaley.pinterest.ui;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.hdodenhof.circleimageview.CircleImageView;
import wyz.whaley.pinterest.R;
import wyz.whaley.pinterest.data.BaseUser;
import wyz.whaley.pinterest.fragment.DisplayFragment;
import wyz.whaley.pinterest.http.AKHttpRequestEngine;
import wyz.whaley.pinterest.http.AKHttpTask;
import wyz.whaley.pinterest.http.utils.AKImageLoader;
import wyz.whaley.pinterest.http.AKRequest;
import wyz.whaley.pinterest.http.AKResponseListener;
import wyz.whaley.pinterest.http.utils.AKHttpConstant;
import wyz.whaley.pinterest.http.utils.AKJSONRequestNew;
import wyz.whaley.pinterest.maneger.ActivityManeger;
import wyz.whaley.pinterest.utils.SharedPreferenceUtils;

public class DrawerActivity extends BasicActivity {
    private static final String TAG = "DrawerActivity";

    private static final int ITEM_HEADER = 0;

    private static final int ITEM_BODY = 1;

    private Handler mHandler = new Handler();

//    private CircleImageView mAvatarIV;
//    private TextView mUserNameTV;
//    private TextView mUserBioTV;

    private HeaderViewHolder mHeaderViewHolder;

    private BaseUser mUser;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar mToolBar;

    private ListView mDrawerList;

    private DrawerAdapter mAdaper;

    private String mToken;

    private DisplayFragment mFragment;

    private String mItemList[] = {"head", "Home", "Following", "Likes", "DownLoaded Images"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
        }
        setContentView(R.layout.drawer_activity);
        initView();
        initData();

    }

    @Override
    protected void setStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.tranlightgray));
        }
    }


    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        if (mToolBar != null) {
            mToolBar.setTitle("AKBubble");
            setSupportActionBar(mToolBar);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolBar, R.drawable.ic_drawer, R.drawable.ic_drawer) {
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mAdaper = new DrawerAdapter();
        mDrawerList.setAdapter(mAdaper);
        if (mFragment == null) {
            mFragment = new DisplayFragment();
            mFragment.setUrl(getDrawerUrl(1), 1);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.shots_container, mFragment).commit();
    }


    private void initData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SharedPreferenceUtils.COMMON_PREFERENCE, MODE_PRIVATE);
        mToken = sharedPreferences.getString(SharedPreferenceUtils.COMMON_TOKEN, null);
        if (mToken != null) {
            AKHttpConstant.USER_ACCESS_TOKEN = mToken;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AKHttpConstant.USER_ACCESS_TOKEN == null) {
                    LoginDialog loginDialog = new LoginDialog(DrawerActivity.this, new LoginDialog.LoginListener() {
                        @Override
                        public void success() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences sharedPreferences = DrawerActivity.this.getSharedPreferences(SharedPreferenceUtils.COMMON_PREFERENCE, MODE_PRIVATE);
                                    mToken = sharedPreferences.getString(SharedPreferenceUtils.COMMON_TOKEN, null);
                                    if (mToken != null) {
                                        AKHttpConstant.USER_ACCESS_TOKEN = mToken;
                                    }
                                    initUser();
                                }
                            });
                        }
                    });
                    loginDialog.show();
                } else {
                    initUser();
                }
            }
        }, 100);
    }

    private String getDrawerUrl(int pos) {
        String url = null;
        String toolBarTitle = null;
        switch (pos) {
            case 1:
                url = AKHttpConstant.decordFromBaseUri(AKHttpConstant.SHOTS);
                toolBarTitle = getResources().getString(R.string.app_name);
                break;
            case 2:
                url = AKHttpConstant.decordFromBaseUri(AKHttpConstant.FOLLOWING_SHOTS);
                toolBarTitle = "Following Shots";
                break;
            case 3:
                url = AKHttpConstant.decordFromBaseUri(AKHttpConstant.LIKE_SHOTS);
                toolBarTitle = "Likes";
                break;
            case 4:
                break;
            case 5:
//                toolBarTitle = "DownLoaded Images";
                break;
        }
        getSupportActionBar().setTitle(toolBarTitle);
        return url;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initUser() {
        final AKRequest request = new AKJSONRequestNew(AKHttpConstant.BASE_URI + AKHttpConstant.USER_INFO, new AKResponseListener() {
            @Override
            public void onSuccess(Object in) {
                Gson gson = new GsonBuilder().create();
                String string = (String) in;
                mUser = gson.fromJson(string, BaseUser.class);
                Log.i(TAG, "USER INFO : " + mUser.getName() + "   " + mUser.getBio());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mHeaderViewHolder != null) {
                            mHeaderViewHolder.mAvatarIV.setTag(mUser.getAvatar_url());
                            AKImageLoader.getInstance().displayAvatar(DrawerActivity.this, mHeaderViewHolder.mAvatarIV, mUser.getAvatar_url(), false);
                            mHeaderViewHolder.mUserNameTV.setText(mUser.getName());
                            mHeaderViewHolder.mUserBioTV.setText(mUser.getBio());
                        }
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DrawerActivity.this, "HTTP exception", Toast.LENGTH_SHORT).show();
                    }
                });
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initUser();
                    }
                }, 2000);
            }
        }, AKHttpRequestEngine.REQUEST_GET, this);
        AKHttpTask.getInstace().add(request);
    }

    class DrawerAdapter extends BaseAdapter implements View.OnClickListener {

        private View mFoucsItem;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (getItemViewType(position) == ITEM_BODY) {
                BodyViewHolder viewHolder;
                if (convertView != null) {
                    viewHolder = (BodyViewHolder) convertView.getTag();
                } else {
                    convertView = LayoutInflater.from(DrawerActivity.this).inflate(R.layout.drawer_item, null);
                    viewHolder = new BodyViewHolder();
                    viewHolder.mIconIv = (ImageView) convertView.findViewById(R.id.drawer_item_iv);
                    viewHolder.mTextView = (TextView) convertView.findViewById(R.id.drawer_item_tv);
                    viewHolder.mTextView.setText(mItemList[position]);
                    viewHolder.mIconIv.setVisibility(View.GONE);
                    convertView.setOnClickListener(this);
                    if (mFoucsItem == null) {
                        mFoucsItem = convertView;
                        mFoucsItem.setBackgroundColor(getResources().getColor(R.color.translightgray));
                        viewHolder.mTextView.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    }
                }
                convertView.setTag(viewHolder);
                viewHolder.mTextView.setTag(position);
            } else {
                HeaderViewHolder viewHolder;
                if (convertView != null) {
                    viewHolder = (HeaderViewHolder) convertView.getTag();
                } else {
                    convertView = LayoutInflater.from(DrawerActivity.this).inflate(R.layout.drawer_header_item, null);
                    viewHolder = new HeaderViewHolder();
                    mHeaderViewHolder = viewHolder;
                    mHeaderViewHolder.mAvatarIV = (CircleImageView) convertView.findViewById(R.id.avatar_civ);
                    mHeaderViewHolder.mUserNameTV = (TextView) convertView.findViewById(R.id.user_name_tv);
                    mHeaderViewHolder.mUserBioTV = (TextView) convertView.findViewById(R.id.user_bio_tv);
                    convertView.setTag(viewHolder);
                }
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return mItemList.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return ITEM_HEADER;
            else {
                return ITEM_BODY;
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getTag() instanceof BodyViewHolder) {
                BodyViewHolder viewHolder = (BodyViewHolder) v.getTag();
                BodyViewHolder preViewHolder = (BodyViewHolder) mFoucsItem.getTag();
                int pos = (int) viewHolder.mTextView.getTag();
                if (pos < 4) {
                    if (mFoucsItem != null) {
                        mFoucsItem.setBackgroundResource(R.drawable.drawer_selector);
                    }
                    mFoucsItem = v;
                    preViewHolder.mTextView.setTextColor(getResources().getColor(R.color.drawer_item_text_color));
                    viewHolder.mTextView.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    mFoucsItem.setBackgroundColor(getResources().getColor(R.color.translightgray));
                    String url = getDrawerUrl(pos);
                    mFragment.setUrlRefresh(url, pos);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else if (pos == 4) {
                    ActivityManeger.jump(DrawerActivity.this, ActivityManeger.DOWNLOAD, null);
                }
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }
    }

    class HeaderViewHolder {
        public CircleImageView mAvatarIV;
        public TextView mUserNameTV;
        public TextView mUserBioTV;
    }

    class BodyViewHolder {
        public ImageView mIconIv;
        public TextView mTextView;
    }

}
