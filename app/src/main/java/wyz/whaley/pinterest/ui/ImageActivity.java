package wyz.whaley.pinterest.ui;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import wyz.whaley.pinterest.R;
import wyz.whaley.pinterest.data.ShotInfo;
import wyz.whaley.pinterest.http.utils.AKImageLoader;
import wyz.whaley.pinterest.http.utils.AKGifImageLoader;
import wyz.whaley.pinterest.maneger.ActivityManeger;
import wyz.whaley.pinterest.utils.BitmapUtils;
import wyz.whaley.pinterest.utils.DribbbleParamsUtils;
import wyz.whaley.pinterest.widget.ZoomImageView;

public class ImageActivity extends BasicActivity {

    private ZoomImageView mImageView;

    private ShotInfo mShotInfo;

    private int mHeight;

    private int mWidth;

    private int mScreenWidth;

    private Bitmap mdefaultBitmap;

    private AKImageLoader.LoadImageListener mLoadImageListener;

    private AKGifImageLoader.LoadGifListener mLoadGifListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShotInfo = (ShotInfo) getIntent().getSerializableExtra(ActivityManeger.INTENT_SHOT);
        mHeight = mShotInfo.getHeight();
        mWidth = mShotInfo.getWidth();
        setContentView(R.layout.activity_image);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
//        mScreenWidth = metrics.widthPixels;

        mScreenWidth = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
        float ratio = (float) mScreenWidth / mWidth;

        mdefaultBitmap = BitmapUtils.createColorBitmap(this, (int) (mWidth * ratio),
                (int) (mHeight * ratio), getResources().getColor(R.color.whitesmoke));

        initView();
        initData();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreenWidth = metrics.widthPixels > metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
    }

    private void initView(){
        mImageView = (ZoomImageView) findViewById(R.id.shot_ziv);
//        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.drawer_background));

        mLoadImageListener = new AKImageLoader.LoadImageListener() {
            @Override
            public void onFinished() {
//                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUpdateProgress() {

            }
        };

        mLoadGifListener = new AKGifImageLoader.LoadGifListener() {
            @Override
            public void onFinished() {
//                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUpdateProgress() {

            }
        };
    }

    private void initData() {
        if (mShotInfo.isAnimated()) {
//            mShotIV.stopGif();
//            mShotIV.setMovie(null);
            AKGifImageLoader.getInstance().displayDetailGif(this, mImageView,
                    mScreenWidth, DribbbleParamsUtils.getSmallImage(mShotInfo.getImages()),
                    DribbbleParamsUtils.getBestImage(mShotInfo.getImages()), mLoadGifListener);
        } else {
            AKImageLoader.getInstance().displayDetailImage(this, mImageView,
                    mScreenWidth, DribbbleParamsUtils.getSmallImage(mShotInfo.getImages()),
                    DribbbleParamsUtils.getBestImage(mShotInfo.getImages()), mLoadImageListener);
        }
    }


}
