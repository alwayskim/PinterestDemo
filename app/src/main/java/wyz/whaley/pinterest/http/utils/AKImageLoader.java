package wyz.whaley.pinterest.http.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wyz.whaley.pinterest.http.AKHttpRequestEngine;
import wyz.whaley.pinterest.http.AKHttpTask;
import wyz.whaley.pinterest.http.AKRequest;
import wyz.whaley.pinterest.http.AKResponseListener;
import wyz.whaley.pinterest.utils.BitmapUtils;
import wyz.whaley.pinterest.utils.DataUtils;
import wyz.whaley.pinterest.widget.AKGifImageView;

public class AKImageLoader {
    private static AKImageLoader instance;

    private Animation mFadeIn;

    private final int mAnimDuration = 500;
    private final int mCacheDuration = 250;

    private AKCache<String, Bitmap> mAKCache;

    private static final String TAG = "ImageLoader";

    private ExecutorService cacheThread = Executors.newSingleThreadExecutor();

    private AKImageLoader() {
        mAKCache = new AKCache<String, Bitmap>() {
            @Override
            protected int akSizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public static AKImageLoader getInstance() {
        if (instance == null) {
            synchronized (AKImageLoader.class) {
                if (instance == null) {
                    instance = new AKImageLoader();
                }
            }
        }
        return instance;
    }

    public AKCache<String, Bitmap> getCache() {
        return mAKCache;
    }


    public void displayImage(final Activity activity, final AKGifImageView imageView,
                             final String url, final boolean isFling, int needWidth) {
        final Bitmap bitmap;
        imageView.clearAnimation();
        imageView.setTag(url);
        if (mAKCache.get(url) != null) {
            bitmap = mAKCache.get(url);
            imageView.setImageBitmap(bitmap);

            Log.i(TAG, "cached already " + " : " + imageView.getTag());

        } else {
            if (!isFling) {
                getImageFromDiskAsyn(activity, imageView, url, isFling, needWidth);
            } else {
                Log.i(TAG, "JUMP");
            }
        }
    }

    private void getImageFromDiskAsyn(final Activity activity, final ImageView imageView,
                                      final String url, final boolean isFling, final int needWidth) {

        final int imageViewWidth = imageView.getWidth();


        Runnable cacheQueryRun = new Runnable() {
            @Override
            public void run() {
                final long time = System.currentTimeMillis();
                Bitmap bitmap = AKImageDiskCache.readImage(activity, url);
                if (imageView != null) {
                    if (bitmap != null) {
                        if (imageViewWidth > 0) {
                            bitmap = BitmapUtils.resizeBitmap(bitmap, imageViewWidth);
                        } else if (needWidth > 0) {
                            bitmap = BitmapUtils.resizeBitmap(bitmap, needWidth);
                        }
                        mAKCache.put(url, bitmap);
                        Log.i(TAG, "image disk query time = " + (System.currentTimeMillis() - time));
                    }
                    final Bitmap bit = bitmap;
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (imageView.getTag().equals(url) && imageView != null) {
                                if (bit != null) {
                                    Log.w(TAG, "image disk cached !!");
//                                    final Bitmap bit = mAKCache.get(url);
                                    imageView.setImageBitmap(bit);
                                    mFadeIn = new AlphaAnimation(0.3f, 1f);
                                    mFadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
                                    mFadeIn.setDuration(mCacheDuration);
//                                    imageView.startAnimation(mFadeIn);
                                    mAKCache.put(url, bit);
                                    return;
                                } else {
                                    AKImageRequest imageRequest = new AKImageRequest(url, new AKResponseListener() {
                                        @Override
                                        public void onSuccess(final Object bitmap) {
                                            if (bitmap instanceof byte[] && bitmap != null) {
                                                Bitmap bit = BitmapFactory.decodeByteArray((byte[]) bitmap, 0, ((byte[]) bitmap).length);
                                                if (imageViewWidth > 0) {
                                                    bit = BitmapUtils.resizeBitmap(bit, imageViewWidth);
                                                } else if (needWidth > 0) {
                                                    bit = BitmapUtils.resizeBitmap(bit, needWidth);
                                                }
                                                mAKCache.put(url, bit);
                                                activity.runOnUiThread((new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (imageView.getTag().equals(url) && imageView != null) {
                                                            imageView.setImageBitmap(mAKCache.get(url));
                                                            mFadeIn = new AlphaAnimation(0.3f, 1f);
                                                            mFadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
                                                            mFadeIn.setDuration(mAnimDuration);
                                                            imageView.startAnimation(mFadeIn);
                                                            Log.i(TAG, "success in imageview Tag compare " + " : " + imageView.getTag());
                                                        } else {
                                                            Log.i(TAG, "failed in imageview Tag compare " + ": pos : " + " tag: " + imageView.getTag() + "mUrl : " + url);
                                                        }
                                                    }
                                                }));
                                                AKCacheTask.getInstace().add(new Runnable() {
                                                    @Override
                                                    public void run() {
//                                                        byte[] bytes = BitmapUtils.Bitmap2Bytes((Bitmap) bitmap);
                                                        byte[] bytes = (byte[]) bitmap;

                                                        AKImageDiskCache diskCache = new AKImageDiskCache(activity);
                                                        OutputStream outPut = diskCache.getOutPutStream(url, activity);

                                                        try {
                                                            if (bytes != null) {
                                                                outPut.write(bytes);
                                                                diskCache.commitSave();
                                                                outPut.close();
                                                            }
                                                        } catch (IOException e) {
                                                            diskCache.abort();
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFailed(final Exception e) {
                                            Log.i(TAG, "request failed : " + url + " Exception : " + e.toString());
                                            if (imageView != null) {
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(activity, "pos : " + "  " + e.toString(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }, AKHttpRequestEngine.REQUEST_GET, activity);
                                    imageView.setTag(imageRequest);
                                    AKHttpTask.getInstace().add(imageRequest);
                                }
                            }
                        }
                    });
                }
            }
        };

        AKCacheTask.getInstace().add(cacheQueryRun);
    }

    public void displayAvatar(final Activity activity, final ImageView imageView, final String url, final boolean isFling) {
        final Bitmap bitmap;
        imageView.setTag(url);

        if (mAKCache.get(url) != null) {
            bitmap = mAKCache.get(url);
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "cached already " + " : " + imageView.getTag());
        } else {
            if (!isFling) {
                Log.i(TAG, "request image " + " : " + url);
                getImageFromDiskAsyn(activity, imageView, url, isFling, -1);
            } else {
                Log.i(TAG, "JUMP");
            }
        }
    }


    public void displayDetailImage(final Activity activity, final ImageView imageView, final int screenWidth, final String lowUrl,
                                   final String highUrl, final LoadImageListener listener) {
        Bitmap bitmap;
        imageView.setTag(highUrl);
        imageView.clearAnimation();

        if (mAKCache.get(highUrl) != null) {
            bitmap = BitmapUtils.resizeBitmap(mAKCache.get(highUrl), screenWidth);
            imageView.setImageBitmap(bitmap);
            listener.onFinished();
            return;
        }

        if (mAKCache.get(lowUrl) != null) {
            bitmap = BitmapUtils.resizeBitmap(mAKCache.get(lowUrl), screenWidth);
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "cached already " + " : " + imageView.getTag());
        }

        Log.i(TAG, "request image " + " : " + highUrl);
        imageView.clearAnimation();
        AKImageRequest imageRequest = new AKImageRequest(highUrl, new AKResponseListener() {
            @Override
            public void onSuccess(final Object bitmap) {
                if (bitmap instanceof byte[] && bitmap != null) {
                    final Bitmap bit = BitmapFactory.decodeByteArray((byte[]) bitmap, 0, ((byte[]) bitmap).length);
                    mAKCache.put(highUrl, bit);
                    activity.runOnUiThread((new Runnable() {
                        @Override
                        public void run() {
                            if (imageView != null) {

                                imageView.setImageBitmap(BitmapUtils.resizeBitmap(mAKCache.get(highUrl), screenWidth));
                                listener.onFinished();
                                Log.i(TAG, "success in imageview Tag compare " + " : " + imageView.getTag());
                            } else {
                                Log.i(TAG, "failed in imageview Tag compare " + ": pos : "
                                        + " tag: " + imageView.getTag() + "mUrl : " + highUrl);
                            }
                        }
                    }));
                }
            }

            @Override
            public void onFailed(final Exception e) {
                Log.i(TAG, "request failed : " + highUrl + " Exception : " + e.toString());
                if (imageView != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "pos : " + "  " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }, AKHttpRequestEngine.REQUEST_GET, AKRequest.Priority.HIGH, activity);
        imageView.setTag(imageRequest);
//        AKHttpTask.getInstace().addToRequestQueue(imageRequest);
        AKHttpTask.getInstace().add(imageRequest);
    }

    public interface LoadImageListener {
        void onFinished();

        void onUpdateProgress();
    }
}
