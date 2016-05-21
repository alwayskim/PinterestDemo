package wyz.whaley.pinterest.ui;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import wyz.whaley.pinterest.R;
import wyz.whaley.pinterest.data.ShotInfo;
import wyz.whaley.pinterest.http.utils.AKImageLoader;
import wyz.whaley.pinterest.http.utils.AKGifImageLoader;
import wyz.whaley.pinterest.maneger.ActivityManeger;
import wyz.whaley.pinterest.utils.BitmapUtils;
import wyz.whaley.pinterest.utils.DateUtils;
import wyz.whaley.pinterest.utils.DribbbleParamsUtils;
import wyz.whaley.pinterest.widget.AKGifImageView;

public class BaseDetailActivity extends ActionBarActivity {

    private Toolbar mToolBar;

    private ShotInfo mShotInfo;

    private AKGifImageView mShotIV;

    private ProgressBar mProgressBar;

    private CircleImageView mAvatarCV;

    private TextView mShotTitleTV;

    private TextView mShotTitleDescriptionTV;

    private CardView mGifCV;

    private TextView mShotDescriptionWV;

    private int mHeight;

    private int mWidth;

    private int mScreenWidth;

    private Bitmap mdefaultBitmap;

    private AKImageLoader.LoadImageListener mLoadImageListener;

    private AKGifImageLoader.LoadGifListener mLoadGifListener;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_detail);
        mShotInfo = (ShotInfo) getIntent().getSerializableExtra(ActivityManeger.INTENT_SHOT);
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mHeight = mShotInfo.getHeight();
        mWidth = mShotInfo.getWidth();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        float ratio = (float) mScreenWidth / mWidth;

        mdefaultBitmap = BitmapUtils.createColorBitmap(this, (int) (mWidth * ratio),
                (int) (mHeight * ratio), getResources().getColor(R.color.whitesmoke));
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }
        initView();
        initData();
    }

    private void initView() {
        mShotIV = (AKGifImageView) findViewById(R.id.big_shot_iv);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mShotTitleTV = (TextView) findViewById(R.id.shot_title_tv);
        mShotDescriptionWV = (TextView) findViewById(R.id.shot_description_tv);
        mShotTitleDescriptionTV = (TextView) findViewById(R.id.shot_title_description_tv);
        mAvatarCV = (CircleImageView) findViewById(R.id.avatar_civ);
        mGifCV = (CardView) findViewById(R.id.item_gif_cv);
        mProgressBar.setIndeterminate(true);
        mShotTitleTV.setText(mShotInfo.getTitle());

        String description = getResources().getString(R.string.shot_title_description);
        description = String.format(description, mShotInfo.getUser().getUsername(), DateUtils.convertTZDate(mShotInfo.getCreated_at()));
        mShotTitleDescriptionTV.setText(description);
        mShotIV.setImageBitmap(mdefaultBitmap);

//        mShotIV.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        mShotDescriptionWV.getSettings().setDomStorageEnabled(true);
        if (mShotInfo.getDescription() == null) {
            mShotDescriptionWV.setVisibility(View.GONE);
        } else {
            mShotDescriptionWV.setText(Html.fromHtml(mShotInfo.getDescription()));
        }
//        mShotDescriptionWV.loadData(mShotInfo.getDescription(), "text/html", null);

        if (mShotInfo.isAnimated()) {
            mGifCV.setVisibility(View.VISIBLE);
        } else {
            mGifCV.setVisibility(View.GONE);
        }
        mLoadImageListener = new AKImageLoader.LoadImageListener() {
            @Override
            public void onFinished() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUpdateProgress() {

            }
        };

        mLoadGifListener =new AKGifImageLoader.LoadGifListener() {
            @Override
            public void onFinished() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUpdateProgress() {

            }
        };

    }

    private void initData() {
        if (mShotInfo.isAnimated()) {
            AKGifImageLoader.getInstance().displayDetailGif(this, mShotIV,
                    mScreenWidth, DribbbleParamsUtils.getSmallImage(mShotInfo.getImages()),
                    DribbbleParamsUtils.getBestImage(mShotInfo.getImages()), mLoadGifListener);
        } else {
            AKImageLoader.getInstance().displayDetailImage(this, mShotIV,
                    mScreenWidth, DribbbleParamsUtils.getSmallImage(mShotInfo.getImages()),
                    DribbbleParamsUtils.getBestImage(mShotInfo.getImages()), mLoadImageListener);
        }
        AKImageLoader.getInstance().displayAvatar(this, mAvatarCV, mShotInfo.getUser().getAvatar_url(), false);
    }


}
