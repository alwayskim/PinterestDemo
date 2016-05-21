package wyz.whaley.pinterest.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import wyz.whaley.pinterest.MyApplication;
import wyz.whaley.pinterest.R;
import wyz.whaley.pinterest.data.ShotInfo;
import wyz.whaley.pinterest.db.table.DownloadedTable;
import wyz.whaley.pinterest.http.utils.AKCacheTask;
import wyz.whaley.pinterest.http.utils.AKImageDiskCache;
import wyz.whaley.pinterest.http.utils.AKImageLoader;
import wyz.whaley.pinterest.http.utils.AKGifDownLoader;
import wyz.whaley.pinterest.http.utils.AKGifImageLoader;
import wyz.whaley.pinterest.http.utils.AKImageDownLoader;
import wyz.whaley.pinterest.maneger.ActivityManeger;
import wyz.whaley.pinterest.utils.BitmapUtils;
import wyz.whaley.pinterest.utils.DataUtils;
import wyz.whaley.pinterest.utils.DateUtils;
import wyz.whaley.pinterest.utils.DribbbleParamsUtils;
import wyz.whaley.pinterest.utils.FileUtils;
import wyz.whaley.pinterest.widget.AKGifImageView;

/**
 * Created by alwayking on 16/3/15.
 */
public class ShotDetailActivity extends BasicActivity {
    private static final String TAG = "ShotDetailActivity";

    private Toolbar mToolBar;

    private ShotInfo mShotInfo;

    private AKGifImageView mShotIV;

    private ProgressBar mProgressBar;

    private CircleImageView mAvatarCV;

    private TextView mShotTitleTV;

    private TextView mShotTitleDescriptionTV;

    private CardView mGifCV;

    private TextView mShotDescriptionTV;

    private AKShareDialog mShareDialog;

//    private View mIamgeDetail;
//    private ZoomImageView mZoomImageView;

    private int mHeight;

    private int mWidth;

    private int mScreenWidth;

    private Bitmap mdefaultBitmap;

    private boolean mIsDownloaded = false;

    private AKImageLoader.LoadImageListener mLoadImageListener;

    private AKGifImageLoader.LoadGifListener mLoadGifListener;

    private AsyncTask mDownLoader = null;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_detail);
        mShotInfo = (ShotInfo) getIntent().getSerializableExtra(ActivityManeger.INTENT_SHOT);

        mHeight = mShotInfo.getHeight();
        mWidth = mShotInfo.getWidth();

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

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mShotIV = (AKGifImageView) findViewById(R.id.big_shot_iv);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mShotTitleTV = (TextView) findViewById(R.id.shot_title_tv);
        mShotDescriptionTV = (TextView) findViewById(R.id.shot_description_tv);
        mShotTitleDescriptionTV = (TextView) findViewById(R.id.shot_title_description_tv);
        mAvatarCV = (CircleImageView) findViewById(R.id.avatar_civ);
        mGifCV = (CardView) findViewById(R.id.item_gif_cv);
        mShotTitleTV.setText(mShotInfo.getTitle());

//        mIamgeDetail = findViewById(R.id.image_detail);
//        mZoomImageView = (ZoomImageView) mIamgeDetail.findViewById(R.id.shot_ziv);
//        mIamgeDetail.setVisibility(View.GONE);


        if (mToolBar != null) {
            mToolBar.setTitle("");
            setSupportActionBar(mToolBar);

            mToolBar.setNavigationIcon(R.drawable.ic_action_back);
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShotDetailActivity.this.finish();
                }
            });
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mShotIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(ActivityManeger.INTENT_SHOT, mShotInfo);
                ActivityManeger.jump(ShotDetailActivity.this, ActivityManeger.IMAGE, intent);
            }
        });

        String description = getResources().getString(R.string.shot_title_description);
        description = String.format(description, mShotInfo.getUser().getName(), DateUtils.convertTZDate(mShotInfo.getCreated_at()));
        mShotTitleDescriptionTV.setText(description);
        mShotIV.setImageBitmap(mdefaultBitmap);

        if (mShotInfo.getDescription() == null) {
            mShotDescriptionTV.setVisibility(View.GONE);
        } else {
            mShotDescriptionTV.setText(Html.fromHtml(mShotInfo.getDescription()));
            mShotDescriptionTV.setMovementMethod(LinkMovementMethod.getInstance());
            mShotDescriptionTV.setBackgroundColor(0x00000000);
        }
//        String text = "<html><body background=\"#00000000\" link=\"#7c4dff\"><font color=\"#555555\">" + mShotInfo.getDescription() + "</font></body</html>";

//        mShotDescriptionTV.loadData(text, "text/html", "UTF-8");
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

        mLoadGifListener = new AKGifImageLoader.LoadGifListener() {
            @Override
            public void onFinished() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onUpdateProgress() {

            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareShot();
                break;
            case R.id.action_download:
                downLoadShot();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downLoadShot() {
        if (mIsDownloaded) {

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("DownLoading...");
            dialog.show();

            final File f = new File(AKImageDiskCache.getDiskCachePath(this, DribbbleParamsUtils.getBestImage(mShotInfo.getImages())));
            final String srcFilePath = f.getAbsolutePath();
            final String destFilePath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getPath() + "/AKBubble";
            Log.i(TAG, destFilePath);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    String destFile = null;
                    if (FileUtils.makeDirs(destFilePath) && f.exists()) {
                        String name = mShotInfo.getTitle() + "." + (mShotInfo.isAnimated() ? "gif" : "jpg");
                        destFile = destFilePath + "/" + name;
                        try {
                            FileUtils.copyFile(srcFilePath, destFile);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    DownloadedTable.getInstance(MyApplication.getContext()).insert(mShotInfo);
                    return destFile;
                }

                @Override
                protected void onPostExecute(String file) {
                    //通知系统更新图像
                    MediaScannerConnection.scanFile(ShotDetailActivity.this, new String[]{file}, null, null);

                    mShotIV.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    }, 500);

                    Log.i(TAG, "dismiss");
                }
            }.execute();

        }
    }

    private void shareShot() {
        if (mIsDownloaded) {
            final File f = new File(AKImageDiskCache.getDiskCachePath(this,
                    DribbbleParamsUtils.getBestImage(mShotInfo.getImages())));
            final String srcFilePath = f.getAbsolutePath();
            final File destFile = FileUtils.getDiskCacheDir(this, "TMP");
            final String destFilePath = destFile.getAbsolutePath();
            Log.i(TAG, destFilePath);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    if (mIsDownloaded) {
                        String destFileStr = null;
                        if (FileUtils.makeDirs(destFilePath) && f.exists()) {
                            String name = "AKBubble" + "." + (mShotInfo.isAnimated() ? "gif" : "jpg");
                            destFileStr = destFilePath + "/" + name;
                            try {
                                FileUtils.copyFile(srcFilePath, destFileStr);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                        return destFileStr;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String file) {
                    shareTo(file);
                }
            }.execute();
        } else {
            shareTo(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AKShareDialog.SHARE_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mShareDialog.dismiss();
//                        Toast.makeText(this, "Share success.", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        mShareDialog.dismiss();
//                        Toast.makeText(this, "Share canceled.", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    private void shareTo(String file) {
        mShareDialog = new AKShareDialog(this, file, mShotInfo.getHtml_url());
        mShareDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shot_detail_menu, menu);
        return true;
    }

    private void initData() {
        final String highUrl = DribbbleParamsUtils.getBestImage(mShotInfo.getImages());
        String lowUrl = DribbbleParamsUtils.getSmallImage(mShotInfo.getImages());
        Bitmap lowBit = AKImageLoader.getInstance().getCache().get(lowUrl);
        if (mShotInfo.isAnimated()) {
            if (lowBit != null) {
                mShotIV.setImageBitmap(BitmapUtils.resizeBitmap(lowBit, mScreenWidth));
            }

            mDownLoader = new AKGifDownLoader(this, highUrl, new AKGifDownLoader.ProgressListener() {
                @Override
                public void updateProgress(int progress) {
                    mProgressBar.setProgress(progress);
                }

                @Override
                public void complete(Movie movie) {
                    mProgressBar.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    if (movie != null) {
                        mShotIV.setMovie(movie);
                        mShotIV.startGif();
                        mShotIV.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        mIsDownloaded = true;
                        AKCacheTask.getInstace().add(new Runnable() {
                            @Override
                            public void run() {
                                final File destFile = FileUtils.getDiskCacheDir(ShotDetailActivity.this, "TMP");
                                final String destFilePath = destFile.getAbsolutePath();
                                String destFileStr;
                                destFileStr = destFilePath;
                                AKImageDiskCache diskCache = null;
                                OutputStream outPut = null;
                                InputStream in = null;
                                File file = new File(destFileStr, "detail.tmp");
                                if (file.exists()) {
                                    try {
                                        in = new BufferedInputStream(new FileInputStream(file));
                                        byte[] bytes = DataUtils.convertToByteArray(in);

                                        diskCache = new AKImageDiskCache(ShotDetailActivity.this);
                                        outPut = diskCache.getOutPutStream(highUrl, ShotDetailActivity.this);
                                        if (bytes != null) {
                                            outPut.write(bytes);
                                            diskCache.commitSave();
                                            outPut.close();
                                        }
                                    } catch (Exception e) {

                                    } finally {
                                        if (diskCache != null) {
                                            diskCache.abort();
                                        }
                                        try {
                                            if (outPut != null) {
                                                outPut.close();
                                            }
                                            if (in != null) {
                                                in.close();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            }
                        });
                    }
                }

                @Override
                public void setIsIndeterminate(boolean isIndeterminate) {

                }
            });


        } else {
//            AKImageLoader.getInstance().displayDetailImage(this, mShotIV,
//                    mScreenWidth, DribbbleParamsUtils.getSmallImage(mShotInfo.getImages()),
//                    DribbbleParamsUtils.getBestImage(mShotInfo.getImages()), mLoadImageListener);
            if (lowBit != null) {
                mShotIV.setImageBitmap(BitmapUtils.resizeBitmap(lowBit, mScreenWidth));
            }
            mDownLoader = new AKImageDownLoader(this, highUrl, mScreenWidth, new AKImageDownLoader.ProgressListener() {
                @Override
                public void updateProgress(int progress) {
                    mProgressBar.setProgress(progress);
                }

                @Override
                public void complete(Bitmap bitmap) {
                    mProgressBar.setVisibility(View.GONE);
                    if (bitmap != null) {
                        mShotIV.setImageBitmap(bitmap);
                        mIsDownloaded = true;
                        AKCacheTask.getInstace().add(new Runnable() {
                            @Override
                            public void run() {
                                final File destFile = FileUtils.getDiskCacheDir(ShotDetailActivity.this, "TMP");
                                final String destFilePath = destFile.getAbsolutePath();
                                String destFileStr;
                                destFileStr = destFilePath;
                                File file = new File(destFileStr, "detail.tmp");
                                if (file.exists()) {
                                    try {
                                        InputStream in = new BufferedInputStream(new FileInputStream(file));
                                        byte[] bytes = DataUtils.convertToByteArray(in);

                                        AKImageDiskCache diskCache = new AKImageDiskCache(ShotDetailActivity.this);
                                        OutputStream outPut = diskCache.getOutPutStream(highUrl, ShotDetailActivity.this);
                                        if (bytes != null) {
                                            outPut.write(bytes);
                                            diskCache.commitSave();
                                            outPut.close();
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                    }
                }

                @Override
                public void setIsIndeterminate(final boolean isIndeterminate) {
                    ShotDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setIndeterminate(isIndeterminate);
                            mProgressBar.setProgress(0);
                        }
                    });

                }
            });


        }
        AKCacheTask.getInstace().add(new Runnable() {
            @Override
            public void run() {
                final long time = System.currentTimeMillis();
                if (mShotInfo.isAnimated()) {
                    final Movie movie = AKImageDiskCache.readGif(ShotDetailActivity.this, highUrl);
                    Log.i(TAG, "hd gif disk time = " + (System.currentTimeMillis() - time));
                    if (movie != null && mShotIV != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                                mShotIV.setMovie(movie);
                                mShotIV.startGif();
                                mShotIV.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                mIsDownloaded = true;
                            }
                        });
                    } else {
                        ((AKGifDownLoader) mDownLoader).execute();
                    }
                } else {
                    final Bitmap bitmap = AKImageDiskCache.readImage(ShotDetailActivity.this, highUrl);
                    Log.i(TAG, "hd image disk time = " + (System.currentTimeMillis() - time));
                    if (bitmap == null){
                        Log.i(TAG, "bitmap = null ");
                    }
                    if (mShotIV == null){
                        Log.i(TAG, "mShotIV = null ");
                    }
                    if (bitmap != null && mShotIV != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                                mShotIV.setImageBitmap(bitmap);
                                mIsDownloaded = true;
                            }
                        });
                    } else {
                        ((AKImageDownLoader) mDownLoader).execute();
                    }
                }

            }
        });
        AKImageLoader.getInstance().displayAvatar(this, mAvatarCV, mShotInfo.getUser().getAvatar_url(), false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownLoader != null) {
            mDownLoader.cancel(false);
        }
    }
}
