package wyz.whaley.pinterest.http.utils;

/**
 * Created by alwayking on 16/3/14.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import wyz.whaley.pinterest.http.AKHttpRequestEngine;
import wyz.whaley.pinterest.http.AKHttpTask;
import wyz.whaley.pinterest.http.AKResponseListener;
import wyz.whaley.pinterest.utils.BitmapUtils;
import wyz.whaley.pinterest.widget.AKGifImageView;


public class AKGifImageLoader {
    private static AKGifImageLoader instance;

    private Animation mFadeIn;

    private final int mAnimDuration = 500;

    AKCache<String, byte[]> mAKCache;

    private static final String TAG = "GifLoader";

    private AKGifImageLoader() {
        mAKCache = new AKCache<String, byte[]>() {
            @Override
            protected int akSizeOf(String key, byte[] value) {
                return value.length;
            }
        };
    }

    public static AKGifImageLoader getInstance() {
        if (instance == null) {
            synchronized (AKGifImageLoader.class) {
                if (instance == null) {
                    instance = new AKGifImageLoader();
                }
            }
        }
        return instance;
    }

    public void displayGif(final Activity activity, final AKGifImageView imageView, final String url, final boolean isFling) {
        imageView.setTag(url);
        if (mAKCache.get(url) != null) {
            final byte[] gifByte;
            Bitmap bitmap;
            imageView.clearAnimation();
            gifByte = mAKCache.get(url);
            if (AKImageLoader.getInstance().getCache().get(url) != null) {
                bitmap = AKImageLoader.getInstance().getCache().get(url);
            } else {
                bitmap = BitmapFactory.decodeByteArray(gifByte, 0, gifByte.length);
            }
            imageView.setImageBitmap(bitmap);
            new AsyncTask<Void, Void, Movie>() {
                @Override
                protected Movie doInBackground(Void... params) {
                    return Movie.decodeByteArray(gifByte, 0, gifByte.length);
                }

                @Override
                protected void onPostExecute(Movie movie) {
                    if (imageView.getTag().equals(url)) {
                        imageView.setMovie(movie);
                    }
                }
            }.execute();
            Log.i(TAG, "cached already " + " : " + imageView.getTag());
        } else {
            imageView.clearAnimation();
            if (!isFling) {
                Log.i(TAG, "request image " + " : " + url);
                imageView.clearAnimation();
                AKGifRequest gifRequest = new AKGifRequest(url, new AKResponseListener() {
                    @Override
                    public void onSuccess(final Object gif) {
                        if (gif instanceof byte[]) {
                            final byte[] gifByte = (byte[]) gif;
                            mAKCache.put(url, gifByte);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(gifByte, 0, gifByte.length);
                            final Bitmap bit = BitmapUtils.resizeBitmap(bitmap, 400);
                            AKImageLoader.getInstance().getCache().put(url, bitmap);
                            if (imageView.getTag().equals(url) && imageView != null) {
                                final Movie movie = Movie.decodeByteArray(gifByte, 0, gifByte.length);
                                activity.runOnUiThread((new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setImageBitmap(bit);
                                        imageView.setMovie(movie);

                                        mFadeIn = new AlphaAnimation(0.6f, 1);
                                        mFadeIn.setDuration(mAnimDuration);
                                        imageView.startAnimation(mFadeIn);
                                        Log.i(TAG, "success in imageview Tag compare " + " : " + imageView.getTag());
                                    }
                                }));
                            } else {
                                Log.i(TAG, "failed in imageview Tag compare " + ": pos : " + " tag: " + imageView.getTag() + "mUrl : " + url);
                            }
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
                imageView.setTag(gifRequest);
//                AKHttpTask.getInstace().addToRequestQueue(gifRequest);
                AKHttpTask.getInstace().add(gifRequest);
            } else {
                Log.i(TAG, "JUMP");
            }
        }
    }


    public void displayDetailGif(final Activity activity, final AKGifImageView imageView, final int screenWidth, final String lowUrl,
                                 final String highUrl, final LoadGifListener listener) {
        imageView.setTag(highUrl);
        imageView.clearAnimation();
        if (AKImageLoader.getInstance().getCache().get(lowUrl) != null) {
            Bitmap bitmap = AKImageLoader.getInstance().getCache().get(lowUrl);
            bitmap = BitmapUtils.resizeBitmap(bitmap, screenWidth);
            imageView.setImageBitmap(bitmap);
        }

        new AsyncTask<Void, Bitmap, Movie>() {
            @Override
            protected Movie doInBackground(Void... params) {
                Movie movie = null;

                if (mAKCache.get(highUrl) != null) {
                    movie = Movie.decodeByteArray(mAKCache.get(highUrl), 0, mAKCache.get(highUrl).length);
                }
                return movie;
            }

            @Override
            protected void onProgressUpdate(Bitmap... values) {
            }

            @Override
            protected void onPostExecute(Movie movie) {
                if (imageView.getTag().equals(highUrl)) {
                    if (movie != null) {
                        listener.onFinished();
                        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        imageView.setMovie(movie);
                        imageView.startGif();
                    }
                }
            }
        }.execute();

        if (mAKCache.get(highUrl) == null) {
            Log.i(TAG, "request image " + " : " + highUrl);
            AKGifRequest gifRequest = new AKGifRequest(highUrl, new AKResponseListener() {
                @Override
                public void onSuccess(final Object gif) {
                    if (gif instanceof byte[]) {
                        final byte[] gifByte = (byte[]) gif;
                        final Movie movie = Movie.decodeByteArray(gifByte, 0, gifByte.length);
                        mAKCache.put(highUrl, gifByte);
                        activity.runOnUiThread((new Runnable() {
                            @Override
                            public void run() {
                                if (imageView.getTag().equals(highUrl) && imageView != null) {
                                    listener.onFinished();
                                    if (movie != null) {
                                        imageView.setMovie(movie);
                                        imageView.startGif();
                                        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                    }
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
            }, AKHttpRequestEngine.REQUEST_GET, activity);
            imageView.setTag(gifRequest);
//            AKHttpTask.getInstace().addToRequestQueue(gifRequest);
            AKHttpTask.getInstace().add(gifRequest);
        }
    }

    public interface LoadGifListener {
        void onFinished();

        void onUpdateProgress();
    }
}

