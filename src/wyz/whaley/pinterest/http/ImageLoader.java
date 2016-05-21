package wyz.whaley.pinterest.http;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class ImageLoader {
    private static ImageLoader instance;

    private int maxMemory = (int) Runtime.getRuntime().maxMemory();

    private int maxCacheSize = maxMemory / 16;

    private Animation mFadeIn;

    private final int mAnimDuration = 1000;

    LruCache<String, Bitmap> cache;

    private static final String TAG = "ImageLoader";

    private ImageLoader() {
        cache = new LruCache<String, Bitmap>(maxCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        Log.i(TAG, "maxMemory : " + maxMemory / 1024 / 1024 + " maxCacheSize : " + maxCacheSize / 1024 / 1024);
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    public void displayImage(final Activity activity, final ImageView imageView, final String url, final int position, final boolean isFling) {
        Bitmap bitmap;
        if (cache.get(url) != null && url.equals((String) imageView.getTag())) {
            bitmap = cache.get(url);
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "cached already " + ": pos : " + position + " : " + imageView.getTag());

        } else {
            if (true) {
                Log.i(TAG, "request image " + ": pos : " + position + " : " + url);
                ImageRequest imageRequest = new ImageRequest(url, new ResponseListener() {
                    @Override
                    public void onSuccess(final Object bitmap) {
                        if (bitmap instanceof Bitmap) {
                            final Bitmap bit = (Bitmap) bitmap;
                            cache.put(url, bit);
                            activity.runOnUiThread((new Runnable() {
                                @Override
                                public void run() {
                                    if (url.equals((String) imageView.getTag()) && imageView != null) {
                                        imageView.setImageBitmap(bit);
                                        mFadeIn = new AlphaAnimation(0, 1);
                                        mFadeIn.setInterpolator(new DecelerateInterpolator());
                                        mFadeIn.setDuration(mAnimDuration);
                                        imageView.startAnimation(mFadeIn);
                                        Log.i(TAG, "success in imageview Tag compare " + ": pos : " + position + " : " + imageView.getTag());
                                    } else {
                                        Log.i(TAG, "failed in imageview Tag compare " + ": pos : " + position + " tag: " + imageView.getTag() + "url : " + url);
                                    }
                                }
                            }));
                        }
                    }

                    @Override
                    public void onFailed(final Exception e) {
                        Log.i(TAG, "request failed : " + url + " Exception : " + e.toString());
                        if (imageView != null) {
                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(activity, "pos : " + position + "  " + e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }, HttpRequestHelp.REQUEST_GET);
                HttpTask.getInstace().addToRequestQueue(imageRequest);
            }
        }
    }

    // public static int calculateInSampleSize(BitmapFactory.Options options,
    // int reqWidth, int reqHeight) {
    // // Raw height and width of image
    // final int height = options.outHeight;
    // final int width = options.outWidth;
    // int inSampleSize = 1;
    //
    // if (height > reqHeight || width > reqWidth) {
    //
    // final int halfHeight = height / 2;
    // final int halfWidth = width / 2;
    //
    // // Calculate the largest inSampleSize value that is a power of 2 and
    // // keeps both
    // // height and width larger than the requested height and width.
    // while ((halfHeight / inSampleSize) > reqHeight && (halfWidth /
    // inSampleSize) > reqWidth) {
    // inSampleSize *= 2;
    // }
    // }
    //
    // return inSampleSize;
    // }
    //
    // public static Bitmap decodeBitmapFromHttp(InputStream bitmap, int
    // reqWidth, int reqHeight) {
    //
    // // First decode with inJustDecodeBounds=true to check dimensions
    // final BitmapFactory.Options options = new BitmapFactory.Options();
    // options.inJustDecodeBounds = true;
    // BitmapFactory.decodeStream(bitmap, null, options);
    //
    // // Calculate inSampleSize
    // options.inSampleSize = calculateInSampleSize(options, reqWidth,
    // reqHeight);
    //
    // // Decode bitmap with inSampleSize set
    // options.inJustDecodeBounds = false;
    // return BitmapFactory.decodeStream(bitmap, null, options);
    // }
}
