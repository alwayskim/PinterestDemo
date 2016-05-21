package wyz.whaley.pinterest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by alwayking on 16/3/13.
 */
public class AKGifImageView extends ImageView {
    private static final String TAG = "GifImageView";
    private Movie mMovie;

    private long mMovieStart;
    private boolean mIsAutoPlay = false;

    private boolean mIsPlaying = false;

    private int mImageWidth;

    private int mImageHeight;

    public AKGifImageView(Context context) {
        super(context);
    }

    public AKGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AKGifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIsAutoPlay(boolean mIsAutoPlay) {
        this.mIsAutoPlay = mIsAutoPlay;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            // 如果是GIF图片则重写设定PowerImageView的大小
//            setMeasuredDimension(mImageWidth, mImageHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long now = android.os.SystemClock.uptimeMillis();
        if (mMovie != null && mIsPlaying) {
            if (mMovieStart == 0) {
                mMovieStart = now;
            }
            int dur = mMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            int relTime = (int) ((now - mMovieStart) % dur);

            mMovie.setTime(relTime);
            float scaleWidth = (float) ((mImageWidth / (1f * mMovie.width())));
            Log.i(TAG, "movie scaled = " + scaleWidth);
            canvas.scale(scaleWidth, scaleWidth);
            mMovie.draw(canvas, 0, 0);
            invalidate();
        } else {
            super.onDraw(canvas);
        }
    }


    public void setMovie(Movie movie) {
        this.mMovie = movie;
        mImageHeight = getHeight();
        mImageWidth = getWidth();
        mMovieStart = 0;
        mIsPlaying = false;
    }

    public void startGif() {
        mIsPlaying = true;
        invalidate();
    }

    public void stopGif() {
        mIsPlaying = false;
    }
}
