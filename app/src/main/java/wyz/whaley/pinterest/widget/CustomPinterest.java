package wyz.whaley.pinterest.widget;

import wyz.whaley.pinterest.db.ItemInfo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Scroller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CustomPinterest extends ViewGroup {

    private static final String TAG = "CustomPinterest";

    private static final String TAG_COUNT = "CustomPinterestCount";
    private static final String TAG_SCROLL = "CustomPinterestScroll";

    public static final int MODE_DOWN = 0;

    public static final int MODE_SCROLL = 1;

    public static final int MODE_RESET = 2;

    public static final int MODE_FLING = 3;

    public static final int MODE_FLING_SLOW = 4;

    private boolean mIsTV = true;

    private int mCountLayout = 0;

    private int mCountMeasure = 0;

    private int mCountComputeScroll = 0;

    private int mTouchMode = 2;

    private int mTouchSlop;

    private boolean mIsFirstLayout;

    private LinkedList<Integer>[] mListItemPosition;

    private Map<Integer, View> mActiveViews = new HashMap<Integer, View>();

    private Map<Integer, View> mCurrentViews = new HashMap<Integer, View>();

    private LinkedList<View> mScrapViews = new LinkedList<View>();

    private LinkedList<Integer>[] mRadishPits;
    private HashMap<Integer, Integer> mContainsMaps;

    private int mColumn = 6;

    private int[] mPinBottom;

    private int[] mPinTop;

    private int[] mColumnsFirstPos;

    private MyBaseAdapter mAdapter;

    private Scroller mScroller;

    private VelocityTracker mVelocityTracker;

    private int mMinFlingVelocity;

    private int mMaxFlingVelocity;

    private int mSlowFlingVelocity = 3000;

    private int mItemCount;
    private int mSpaceHorizontal = 50;
    private int mSpaceVertical = 50;

    private int mLastItemPos = -1;

    private int mScrollDistance = 0;

    private int mBeginY;

    private int flingBeginY;

    private PinterestCallBack mPinterestCallBack;

    // for tv
    private int mFocusPosition;

    private int mFoucsColumn;

    private int mFocusPosInList;

    private long mDadPressGap;

    private long mTime;

    private long mPreDuration = 0;

    private static final int DPAD_LEFT = -1;

    private static final int DPAD_RIGHT = 1;

    private static final int DPAD_UP = 2;

    private static final int DPAD_DOWN = 3;

    private static final int DURATION_BUFFER = 30;

    private static final int TV_WALK_DURATION = 670;

    private static final int TV_BIKE_DURATION = 370;

    private static final int TV_TRAIN_DURATION = 270;

    private static final int TV_ROCKET_DURATION = 100;

    private int mItemEdgeViewId = -1;
    private View mItemEdgeView;

    private LinkedList<Integer> mScrollCount;

    public CustomPinterest(Context context) {
        super(context);
        init();
    }

    public CustomPinterest(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomPinterest(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        Log.i(TAG, "mMinFlingVelocity = " + mMinFlingVelocity + "  mMaxFlingVelocity = " + mMaxFlingVelocity);

        if (mScroller == null) {
            mScroller = new Scroller(getContext());
        }
        resetAllStatus();
    }

    public void setIsTV(boolean isTV) {
        mIsTV = isTV;
        Log.i(TAG, "isTV = " + isTV);
    }

    public void setPinterestCallBack(PinterestCallBack mPinterestCallBack) {
        this.mPinterestCallBack = mPinterestCallBack;
    }

    public void setAdapter(MyBaseAdapter adapter) {
        this.mAdapter = adapter;
        this.mItemCount = mAdapter.getCount();

        resetAllStatus();
        requestLayout();
    }

    public int getColumn() {
        return mColumn;
    }

    public void setColumn(int column) {
        this.mColumn = column;
        resetAllStatus();
        requestLayout();
    }

    public int getTouchMode() {
        return mTouchMode;
    }

    public void setPinFocusEdgeResource(int veiwId) {
        if (mIsTV) {
            this.mItemEdgeViewId = veiwId;
            mItemEdgeView = inflate(getContext(), mItemEdgeViewId, null);
        }

    }

    public void dataChangeNotification() {
        mItemCount = mAdapter.getCount();
        // resetAllStatus();
        requestLayout();
    }

    private void resetAllStatus() {
        // mFirstItemPos = 0;
        mIsFirstLayout = true;
        mLastItemPos = -1;
        mScrollDistance = 0;
        mTouchMode = MODE_RESET;

        mPinBottom = new int[mColumn];
        mPinTop = new int[mColumn];
        mColumnsFirstPos = new int[mColumn];
        mRadishPits = new LinkedList[mColumn];
        mContainsMaps = new HashMap<Integer, Integer>();
        mListItemPosition = new LinkedList[mColumn];
        mScrollCount = new LinkedList<Integer>();

        mScrapViews.clear();
        mCurrentViews.clear();
        mActiveViews.clear();

        for (int i = 0; i < mColumn; i++) {
            mPinBottom[i] = 0;
            mPinTop[i] = 0;
            mColumnsFirstPos[i] = 0;
            mRadishPits[i] = new LinkedList<Integer>();
            mListItemPosition[i] = new LinkedList<Integer>();
        }
        removeAllViewsInLayout();
    }

    private void resetDataChangeStatus() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getChildCount() == 0) {
            return false;
        }
        int action = event.getAction();
        int y = (int) event.getY();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mScroller.forceFinished(true);
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            } else {
                mVelocityTracker.clear();
            }
            mVelocityTracker.addMovement(event);
            mTouchMode = MODE_DOWN;
            mBeginY = y;
            mScrollDistance = 0;

            break;
        case MotionEvent.ACTION_MOVE:
            mVelocityTracker.addMovement(event);
            if (mTouchMode == MODE_DOWN) {
                startScrollIfNeeded(y);
            } else if (mTouchMode == MODE_SCROLL) {
                scrollGrid(y);
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mTouchMode == MODE_SCROLL) {
                mTouchMode = MODE_RESET;
                mVelocityTracker.computeCurrentVelocity(1000);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMaxFlingVelocity) {
                    if (velocityY < 0) {
                        velocityY = -mMaxFlingVelocity;
                    } else {
                        velocityY = mMaxFlingVelocity;
                    }

                }
                Log.i(TAG, "Action_Up ... Mode_SCROLL , velocity Y : " +

                velocityY);
                if (Math.abs(velocityY) > mMinFlingVelocity) {
                    Log.w(TAG, "FLING STATU..");
                    if (Math.abs(velocityY) < mSlowFlingVelocity) {
                        mTouchMode = MODE_FLING_SLOW;
                    } else {
                        mTouchMode = MODE_FLING;
                    }
                    flingGrid(velocityY);
                }
            }
            break;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        int code = event.getKeyCode();
        int action = event.getAction();

        long timeGap = System.currentTimeMillis() - mTime;
        if (timeGap > 2 * TV_WALK_DURATION) {
            mPreDuration = 0;
        }

        // Log.i(TAG_COUNT, "down dpad timeGap = " + timeGap);

        if (mPreDuration > timeGap + DURATION_BUFFER) {
            mPreDuration -= timeGap;
            mTime = System.currentTimeMillis();
            Log.e(TAG, " key lost");
            return true;
        }
        switch (action) {
        case KeyEvent.ACTION_DOWN:
            int duration;
            Log.i(TAG_COUNT, "down dpad timeGap = " + timeGap);
            if (event.getRepeatCount() < 1) {
                mTouchMode = MODE_FLING_SLOW;
                if (timeGap > TV_WALK_DURATION) {
                    duration = TV_WALK_DURATION;

                } else if (timeGap > TV_BIKE_DURATION) {
                    duration = TV_BIKE_DURATION - DURATION_BUFFER;
                    // duration = (int) TV_BIKE_DURATION;
                } else {
                    duration = TV_TRAIN_DURATION - DURATION_BUFFER;
                    // duration = (int) TV_TRAIN_DURATION;
                    mTouchMode = MODE_SCROLL;
                }
            } else {
                mTouchMode = MODE_FLING;
                duration = TV_ROCKET_DURATION - DURATION_BUFFER;
            }
            Log.e(TAG, "duration = " + duration + "  timeGap = " + timeGap);
            if (timeGap < TV_ROCKET_DURATION) {
                return true;
            }
            mTime = System.currentTimeMillis();
            mPreDuration = duration;

            switch (code) {
            case KeyEvent.KEYCODE_DPAD_UP:
                dealDPadUp(duration);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                dealDPadLeft(duration);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                dealDPadRight(duration);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                dealDPadDown(duration);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:

                break;

            }

        case KeyEvent.ACTION_UP:
            Log.e(TAG, "ACTION_UP");
            mTouchMode = MODE_RESET;
            // mTime = System.currentTimeMillis();
            break;
        default:
            break;
        }

        // return super.dispatchKeyEvent(event);
        return true;
    }

    private void dealDPadUp(int duration) {
        if (mFocusPosInList > 0) {
            View view = mCurrentViews.get(mListItemPosition[mFoucsColumn].get(mFocusPosInList));
            int backPos = mListItemPosition[mFoucsColumn].get(--mFocusPosInList);
            int verticalDistance = 0;
            mFocusPosition = backPos;
            View nextView = mCurrentViews.get(mFocusPosition);
            if (nextView.getTop() < 0) {
                if (mFocusPosition >= mColumn) {
                    verticalDistance += mSpaceVertical;
                }
                verticalDistance -= nextView.getTop();
                scrollTVPin(verticalDistance, duration);
            } else {
                nextView.requestFocus();
            }
            animatePinFocus(view, nextView, verticalDistance, duration, DPAD_UP);
        } else {
        }

    }

    private void dealDPadDown(int duration) {
        Log.i(TAG, "mFoucsColumn=" + mFoucsColumn + "  mFocusPosInList=" + mFocusPosInList + "  mFocusItemID=" + mFocusPosition
                + "  mListItemPosition[mFoucsColumn].size()=" + mListItemPosition[mFoucsColumn].size() + "  childcount = " + getChildCount());
        if (mFocusPosInList < mListItemPosition[mFoucsColumn].size() - 1) {
            int verticalDistance = 0;
            View view = mCurrentViews.get(mListItemPosition[mFoucsColumn].get(mFocusPosInList));
            int nextPos = mListItemPosition[mFoucsColumn].get(++mFocusPosInList);
            mFocusPosition = nextPos;
            View nextView = mCurrentViews.get(mFocusPosition);
            if (nextView.getBottom() + mSpaceVertical >= getHeight()) {
                // view.requestFocus();
                verticalDistance = getHeight() - nextView.getBottom() - mSpaceVertical;
                Log.i(TAG, "DPadDown need scroll down distance  " + verticalDistance);
                scrollTVPin(verticalDistance, duration);
            } else {
                nextView.requestFocus();
            }
            animatePinFocus(view, nextView, verticalDistance, duration, DPAD_DOWN);
        } else {
            if (mPinterestCallBack != null) {
                mPinterestCallBack.loadMore();
            }
        }
    }

    private int getViewCenterVertical(View view) {
        return (view.getBottom() - view.getTop()) / 2 + view.getTop();
    }

    private float getViewCenterHorizontal(View view) {
        return (float) (view.getRight() - view.getLeft()) / 2 + view.getLeft();
    }

    private void searchNextFocusPosAccordingCenter(int direction, int duration) {
        View view = mCurrentViews.get(mFocusPosition);
        int centerY = getViewCenterVertical(view);
        int verticalDistance = 0;
        int nextColumn = (mFoucsColumn + direction + mColumn) % mColumn;
        for (int i = 0; i < mListItemPosition[nextColumn].size(); i++) {
            View nextView = mCurrentViews.get(mListItemPosition[nextColumn].get(i));
            if (centerY >= (nextView.getTop() - mSpaceVertical) && centerY < nextView.getBottom()) {
                Log.w(TAG, "nextColumn = " + nextColumn + "  curView's id = " + mFocusPosition + "  nextView's id = " + mListItemPosition[nextColumn].get(i)
                        + "  centerY = " + centerY + "  nextView.getTop() = " + nextView.getTop() + "  nextView.getBottom() = " + nextView.getBottom());
                mFoucsColumn = nextColumn;
                mFocusPosInList = i;
                mFocusPosition = mListItemPosition[mFoucsColumn].get(i);
                if (nextView.getTop() < 0) {
                    verticalDistance = -nextView.getTop() + mSpaceVertical;
                    scrollTVPin(verticalDistance, duration);
                } else if (nextView.getBottom() + mSpaceVertical >= getHeight()) {
                    Log.i(TAG, "need scroll down distance  " + (getHeight() - nextView.getBottom() - mSpaceVertical));
                    verticalDistance = getHeight() - nextView.getBottom() - mSpaceVertical;
                    scrollTVPin(verticalDistance, duration);
                }
                nextView.requestFocus();
                animatePinFocus(view, nextView, verticalDistance, duration, direction);

                break;
            } else if (i == mListItemPosition[nextColumn].size() - 1) {
                mFoucsColumn = nextColumn;
                mFocusPosInList = i;
                mFocusPosition = mListItemPosition[mFoucsColumn].get(i);
                if (nextView.getTop() < 0) {
                    verticalDistance = -nextView.getTop() + mSpaceVertical;
                    scrollTVPin(verticalDistance, duration);
                } else if (nextView.getBottom() + mSpaceVertical >= getHeight()) {
                    verticalDistance = getHeight() - nextView.getBottom() - mSpaceVertical;
                    Log.i(TAG, "need scroll down distance  " + verticalDistance);
                    scrollTVPin(verticalDistance, duration);
                } else {
                    // nextView.requestFocus();
                }
                nextView.requestFocus();
                animatePinFocus(view, nextView, verticalDistance, duration, direction);
                break;
            }
        }
    }

    private void animatePinFocus(View startView, View endView, int verticalDistance, int duration, int direction) {
        if (false) {
            animatePinNew(startView, endView, verticalDistance, duration, direction);
            return;
        }

        AnimationSet animationSet = new AnimationSet(true);
        Animation translate = new TranslateAnimation(startView.getLeft(), endView.getLeft(), startView.getTop(), endView.getTop() + verticalDistance);
        translate.setStartOffset(0);
        translate.setFillAfter(true);
        Log.e(TAG, "mItemEdgeView.getHeight() = " + mItemEdgeView.getHeight());

        Animation scale = new ScaleAnimation((float) startView.getWidth() / mItemEdgeView.getWidth(), (float) endView.getWidth() / startView.getWidth(),
                (float) startView.getHeight() / mItemEdgeView.getHeight(), (float) endView.getHeight() / mItemEdgeView.getHeight(),
                getViewCenterHorizontal(startView), endView.getTop() + verticalDistance);
        scale.setFillAfter(true);
        scale.setStartOffset(0);

        animationSet.addAnimation(translate);
        animationSet.addAnimation(scale);

        animationSet.setFillAfter(true);
        animationSet.setDuration(duration);

        mItemEdgeView.startAnimation(animationSet);

        Animation scaleIn = new ScaleAnimation(1, (float) (endView.getWidth() - (float) endView.getPaddingBottom() / 2)
                / (endView.getWidth() - 2 * endView.getPaddingBottom()), 1, (float) (endView.getHeight() - (float) endView.getPaddingBottom() / 2)
                / (endView.getHeight() - 2 * endView.getPaddingBottom()), getViewCenterHorizontal(endView), getViewCenterVertical(endView)
                + endView.getPaddingBottom() + verticalDistance);
        scaleIn.setFillAfter(true);
        scaleIn.setDuration(duration);

        // endView.startAnimation(scaleIn);
    }

    private void animatePinNew(View startView, View endView, int verticalDistance, int duration, int direction) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(mItemEdgeView, "translationX", endView.getLeft() - mItemEdgeView.getLeft()),
                ObjectAnimator.ofFloat(mItemEdgeView, "translationY", endView.getTop() + verticalDistance - mItemEdgeView.getTop()),
                ObjectAnimator.ofFloat(mItemEdgeView, "scaleX", 1, (float) endView.getWidth() / mItemEdgeView.getWidth()),
                ObjectAnimator.ofFloat(mItemEdgeView, "scaleY", 1, (float) endView.getHeight() / mItemEdgeView.getHeight()));
        // ObjectAnimator.ofFloat(mItemEdgeView, "scaleX", endView.getLeft() -
        // startView.getLeft());
        set.setDuration(duration).start();

    }

    private void searchNextAccordingDirection(int direction, int duration) {
        View view = mCurrentViews.get(mFocusPosition);
        int centerY = getViewCenterVertical(view);
        int baseY;
        if (direction < 0) {
            baseY = view.getTop();
        } else {
            baseY = view.getBottom();
        }
        int nextColumn = (mFoucsColumn + direction + mColumn) % mColumn;
        for (int i = 0; i < mListItemPosition[nextColumn].size(); i++) {
            View nextView = mCurrentViews.get(mListItemPosition[nextColumn].get(i));
            if (direction > 0) {
                if (nextView.getTop() - mSpaceVertical <= baseY && nextView.getBottom() > baseY) {
                    mFoucsColumn = nextColumn;
                    // mFocusPosInList = i;
                    mFocusPosition = mListItemPosition[mFoucsColumn].get(i);
                    if (nextView.getTop() < 0) {
                        scrollTVPin(-nextView.getTop() + mSpaceVertical, duration);
                    } else if (nextView.getBottom() + mSpaceVertical >= getHeight()) {
                        Log.i(TAG, "need scroll down distance  " + (getHeight() - nextView.getBottom() - mSpaceVertical));
                        scrollTVPin(getHeight() - nextView.getBottom() - mSpaceVertical, duration);
                    } else {
                        nextView.requestFocus();
                    }
                    break;
                } else if (i == mListItemPosition[nextColumn].size() - 1) {
                    mFoucsColumn = nextColumn;
                    // mFocusPosInList = i;
                    mFocusPosition = mListItemPosition[mFoucsColumn].getLast();
                    if (nextView.getTop() < 0) {
                        scrollTVPin(-nextView.getTop() + mSpaceVertical, duration);
                    } else if (nextView.getBottom() + mSpaceVertical >= getHeight()) {
                        Log.i(TAG, "need scroll down distance  " + (getHeight() - nextView.getBottom() - mSpaceVertical));
                        scrollTVPin(getHeight() - nextView.getBottom() - mSpaceVertical, duration);
                    } else {
                        nextView.requestFocus();
                    }
                    break;
                }
            } else {
                if (nextView.getBottom() + mSpaceVertical >= baseY && nextView.getTop() < baseY) {
                    mFoucsColumn = nextColumn;
                    // mFocusPosInList = i;
                    mFocusPosition = mListItemPosition[mFoucsColumn].get(i);
                    if (nextView.getTop() < 0) {
                        scrollTVPin(-nextView.getTop() + mSpaceVertical, duration);
                    } else if (nextView.getBottom() + mSpaceVertical >= getHeight()) {
                        Log.i(TAG, "need scroll down distance  " + (getHeight() - nextView.getBottom() - mSpaceVertical));
                        scrollTVPin(getHeight() - nextView.getBottom() - mSpaceVertical, duration);
                    } else {
                        nextView.requestFocus();
                    }
                    break;
                }
            }
        }

    }

    private void dealDPadRight(int duration) {
        // searchNextAccordingDirection(1, duration);
        searchNextFocusPosAccordingCenter(DPAD_RIGHT, duration);
    }

    private void dealDPadLeft(int duration) {
        // searchNextAccordingDirection(-1, duration);
        searchNextFocusPosAccordingCenter(DPAD_LEFT, duration);
    }

    private void dealDPadCenter() {

    }

    private void changeFocusPosInList(int column, int focusPos) {
        for (int i = 0; i < mListItemPosition[column].size(); i++) {
            if (focusPos == mListItemPosition[column].get(i)) {
                mFocusPosInList = i;
                break;
            }
        }
        View view = mCurrentViews.get(mFocusPosition);
        if (view != null) {
            view.requestFocus();
        }
    }

    private boolean startScrollIfNeeded(int y) {
        if (Math.abs(y - mBeginY) > mTouchSlop) {
            if (shouldScroll()) {
                mTouchMode = MODE_SCROLL;
                scrollGrid(y);
                Log.i(TAG, "startScrollIfNeeded true");
                return true;
            }
        }
        return false;
    }

    private void scrollGrid(int y) {
        directScrollPin(y - mBeginY);
        Log.w(TAG, "scrollDistance " + mScrollDistance);
        mBeginY = y;

        calculatePin();
        invalidate();
        // requestLayout();
    }

    private void flingGrid(int velocity) {
        flingBeginY = 0;
        mScroller.fling(0, 0, 0, velocity, 0, 0, -100 * 1000, 100 * 1000);
        invalidate();
    }

    private void scrollTVPin(int distance, int duration) {
        flingBeginY = 0;
        mScroller.startScroll(0, 0, 0, distance, duration);
        Log.e(TAG_SCROLL, "startScroll distance = " + distance);
        // if (duration == TV_WALK_DURATION) {
        // mScroller.startScroll(0, 0, 0, distance, duration);
        // }
        //
        // else if (duration > TV_ROCKET_DURATION) {
        // mScroller.fling(0, 0, 0, distance *10, 0, 0, -getHeight(),
        // getHeight());
        // }
        invalidate();
    }

    private void directScrollPin(int distance) {
        mScrollDistance = dealDistance(distance);

        Log.w(TAG_SCROLL, "direct scrollDistance " + mScrollDistance);
    }

    private int dealDistance(int distance) {
        // if (mIsTV) {
        // return distance;
        // }
        boolean isNeg = distance < 0;
        if (Math.abs(distance) >= 3 * getHeight() / 5) {
            distance = 3 * getHeight() / 5;
            if (isNeg) {
                distance = -distance;
            }

        }
        return distance;
    }

    private boolean shouldScroll() {
        for (int i = 0; i < mColumn; i++) {
            Log.i(TAG, "shouldScroll mPinTop = " + mPinTop[i]);
            if (mPinBottom[i] > getHeight() || mPinTop[i] < 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            // Log.w(TAG, "mScroller.getCurrVelocity() : " +
            // mScroller.getCurrVelocity());
            Log.i(TAG_COUNT, "computeScroll count : " + ++mCountComputeScroll);
            if (Math.abs(mScroller.getCurrVelocity()) < mSlowFlingVelocity) {
                mTouchMode = MODE_FLING_SLOW;
            }
            directScrollPin(mScroller.getCurrY() - flingBeginY);

            calculatePin();

            Log.i(TAG_SCROLL, "computeScroll scroll distance = " + (mScroller.getCurrY() - flingBeginY) + " mScroller.getCurrY() = " + mScroller.getCurrY());
            flingBeginY = mScroller.getCurrY();

            postInvalidate();
        } else {
            mCountComputeScroll = 0;
        }
    }

    private void calculatePin() {

        if (mItemCount == 0) {
            return;
        }
        if (getChildCount() == 0) {
            fillView(mPinBottom, mPinTop);
        } else {
            int[] lastTop = new int[mColumn];
            int[] firstBottom = new int[mColumn];
            for (int i = 0; i < mColumn; i++) {
                if (mListItemPosition[i].size() > 0) {
                    firstBottom[i] = mPinTop[i] + getItemHeight(mListItemPosition[i].get(0));
                    lastTop[i] = mPinBottom[i] - getItemHeight(mListItemPosition[i].getLast()) - mSpaceVertical;
                }

            }
            Log.i(TAG_SCROLL, "after direct scroll distance is = " + mScrollDistance);
            fillView(firstBottom, lastTop);
        }

        layoutChild();
        mScrollDistance = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG_COUNT, "onMeasure count : " + ++mCountMeasure);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.w(TAG_COUNT, "onLayout count : " + ++mCountLayout);
        // if (mIsTV) {
        // mSpaceVertical = (int) (getHeight() - ((float) getItemWidth() * 1.5 *
        // 2)) / 2;
        // }

        if (mItemCount == 0) {
            return;
        }
        if (getChildCount() == 0) {
            fillView(mPinBottom, mPinTop);
            layoutChild();
            mScrollDistance = 0;
        } else {
            // int[] lastTop = new int[mColumn];
            // int[] firstBottom = new int[mColumn];
            // for (int i = 0; i < mColumn; i++) {
            // if (mListItemPosition[i].size() > 0) {
            // firstBottom[i] = mPinTop[i] +
            // getItemHeight(mListItemPosition[i].get(0));
            // lastTop[i] = mPinBottom[i] -
            // getItemHeight(mListItemPosition[i].getLast());
            // }
            //
            // }
            // Log.i(TAG_SCROLL, "after direct scroll distance is = " +
            // mScrollDistance);
            // fillView(firstBottom, lastTop);
        }

        // layoutChild();
        // mScrollDistance = 0;
    }

    private int getItemHeight(int position) {
        ItemInfo info = (ItemInfo) mAdapter.getItem(position);
        return calculateChildHeight(info.getWidth(), info.getHeight());
    }

    private int calculateChildHeight(int width, int height) {
        float ratio = (float) width / getItemWidth();
        float realHeight = height / ratio;
        return (int) realHeight;
    }

    private void fillView(int[] firstBottom, int[] lastTop) {
        if (mScrollDistance > 0) {
            pullDown(lastTop);
        } else {
            pullUp(firstBottom);
        }
    }

    private void pullDown(int[] lastTop) {
        // add item at the top
        {
            int i;
            boolean isReachTop = false;
            boolean isFirstItemZero = false;
            {
                for (i = mColumn - 1; i >= 0;) {
                    int j = 0;
                    for (j = i - 1; j >= 0; j--) {
                        if (mPinTop[i] < mPinTop[j]) {
                            i = j;
                            break;
                        }
                    }
                    if (j == -1) {
                        break;
                    }
                }
                if (mColumnsFirstPos[i] == 0) {
                    isFirstItemZero = true;
                }
            }
            if (isFirstItemZero) {
                if (mScrollDistance > Math.abs(mPinTop[i])) {
                    mScrollDistance = Math.abs(mPinTop[i]);
                    mScroller.forceFinished(true);
                }
            }
            // else
            {

                addInTopArea();

                int j;
                for (j = mColumn - 1; j >= 0;) {
                    int k = 0;
                    for (k = j - 1; k >= 0; k--) {
                        if (mPinTop[j] < mPinTop[k]) {
                            j = k;
                            break;
                        }
                    }
                    if (k == -1) {
                        break;
                    }
                }
                if (mScrollDistance + mPinTop[j] > 0) {
                    Log.i(TAG, "origin : " + mScrollDistance + "real distance : " + (mScrollDistance - mPinTop[j]));
                    mScrollDistance = -mPinTop[j];
                    isReachTop = true;
                    mScroller.forceFinished(true);
                }
            }
            for (int k = 0; k < mColumn; k++) {
                if (isReachTop) {
                    mPinTop[k] = 0;
                } else {
                    mPinTop[k] += mScrollDistance;
                }
            }
            Log.i(TAG_SCROLL, "mPinTop[" + 0 + "] = " + mPinTop[0]);
        }

        // clear item out range of bottom edge
        for (int i = 0; i < mColumn; i++) {
            int clearCount = 0;
            while (lastTop[i] + mScrollDistance > getHeight()) {
                // && mListItemPosition[i].size() > 2
                if (mCurrentViews.containsKey(mListItemPosition[i].getLast())) {
                    View trashView = mCurrentViews.remove(mListItemPosition[i].getLast());
                    mScrapViews.addFirst(trashView);
                    detachViewFromParent(trashView);
                    // removeViewInLayout(trashView);
                }
                int lastItemHeight = getItemHeight(mListItemPosition[i].getLast());
                mListItemPosition[i].removeLast();

                mPinBottom[i] -= (lastItemHeight + mSpaceVertical);

                lastTop[i] = mPinBottom[i] - getItemHeight(mListItemPosition[i].getLast());
                mLastItemPos--;
                clearCount++;
            }
            Log.e(TAG, "clear item  bottom edge count = " + clearCount);
        }
        for (int j = 0; j < mColumn; j++) {
            mPinBottom[j] += mScrollDistance;
        }
    }

    private void pullUp(int[] firstBottom) {
        // add item at the bottom
        {
            int i;

            if (mLastItemPos == mItemCount - 1) {
                // deal the situation when pinterest arrive the last item
                int j = 0;
                int k = 0;
                for (j = 0; j < mColumn;) {
                    for (k = j + 1; k < mColumn; k++) {
                        if (mPinBottom[j] < mPinBottom[k]) {
                            j = k;
                            break;
                        }
                    }
                    if (k == mColumn) {
                        break;
                    }
                }
                if (mPinBottom[j] + mScrollDistance - mSpaceVertical < getHeight()) {
                    Log.i(TAG, "origin : " + mScrollDistance + "real distance : " + (-Math.abs(mPinBottom[j] - getHeight())));
                    mScrollDistance = getHeight() - mPinBottom[j] + mSpaceVertical;
                    if (mPinterestCallBack != null) {
                        mPinterestCallBack.loadMore();
                    }
                    mScroller.forceFinished(true);
                }

            } else {
                for (i = mLastItemPos + 1; i < mItemCount; i++) {
                    if (!addToMinHeightColumn(i)) {
                        break;
                    }
                }
                if (i == mItemCount) {
                    int k = 0;
                    int j = 0;
                    for (j = 0; j < mColumn;) {
                        for (k = j + 1; k < mColumn; k++) {
                            if (mPinBottom[j] < mPinBottom[k]) {
                                j = k;
                                break;
                            }
                        }
                        if (k == mColumn) {
                            break;
                        }
                    }
                    // ///////////////////////////////
                    if (mPinBottom[j] + mScrollDistance - mSpaceVertical < getHeight()) {
                        Log.i(TAG, "origin : " + mScrollDistance + "real distance : " + (getHeight() - mPinBottom[j]));
                        mScrollDistance = getHeight() - mPinBottom[j] + mSpaceVertical;
                        Log.i(TAG, "mPinBottom[" + j + "] = " + mPinBottom[j]);
                        mScroller.forceFinished(true);
                        if (mPinterestCallBack != null) {
                            mPinterestCallBack.loadMore();
                        }
                    }
                }
            }
            for (int k = 0; k < mColumn; k++) {
                mPinBottom[k] += mScrollDistance;
            }
        }

        // clear item out range of top edge
        for (int i = 0; i < mColumn; i++) {
            while (firstBottom[i] + mSpaceVertical + mScrollDistance < 0) {
                Log.e(TAG, "firstBottom[i] + mSpaceVertical + mScrollDistance = " + (firstBottom[i] + mSpaceVertical + mScrollDistance));
                // && mListItemPosition[i].size() > 2
                int firstItemHeight = getItemHeight(mListItemPosition[i].getFirst());
                if (mCurrentViews.containsKey(mListItemPosition[i].getFirst())) {
                    View trashView = mCurrentViews.remove(mListItemPosition[i].getFirst());
                    mScrapViews.addFirst(trashView);
                    detachViewFromParent(trashView);
                    // removeViewInLayout(trashView);
                }
                mListItemPosition[i].removeFirst();
                if (mListItemPosition[i].size() == 0) {
                    mPinTop[i] = 0;
                    firstBottom[i] = 0;
                } else {
                    mPinTop[i] += firstItemHeight + mSpaceVertical;
                    firstBottom[i] = mPinTop[i] + getItemHeight(mListItemPosition[i].getFirst());
                    mColumnsFirstPos[i]++;
                }
            }
        }
        for (int j = 0; j < mColumn; j++) {
            mPinTop[j] += mScrollDistance;
        }
        Log.i(TAG_SCROLL, "pintop[0] = " + mPinTop[0] + " mScrollDistance = " + mScrollDistance);

    }

    /**
     * add the item at the bottom of the pinterest. Choose the min height column
     * if can be added.
     * 
     * @param
     * @return
     */
    private boolean addToMinHeightColumn(int position) {
        int i = 0;
        int j = 0;
        boolean isOutBottomEdge = true;
        for (i = 0; i < mColumn;) {
            // Log.i(TAG, "bottom list" + i + " : " + mPinBottom[i]);
            if (mPinBottom[i] + mScrollDistance - mSpaceVertical < getHeight()) {
                isOutBottomEdge = false;
                Log.i(TAG, "have space in list" + i);
            }
            for (j = i + 1; j < mColumn; j++) {
                if (mPinBottom[i] > mPinBottom[j]) {
                    i = j;
                    break;
                }
            }
            if (j == mColumn) {
                if (!isOutBottomEdge) {
                    ItemInfo info = (ItemInfo) mAdapter.getItem(position);
                    int itemHeight = calculateChildHeight(info.getWidth(),

                    info.getHeight());

                    mListItemPosition[i].add(position);
                    mPinBottom[i] += itemHeight + mSpaceVertical;
                    mLastItemPos++;
                    if (!mContainsMaps.containsKey(mLastItemPos)) {
                        mRadishPits[i].add(mLastItemPos);
                        mContainsMaps.put(mLastItemPos, mContainsMaps.size());
                    }

                    // Log.i(TAG, "addToMinHeightColumn size list" + i +
                    // " size : " + mList[i].size());
                }
                break;
            }
        }
        return !isOutBottomEdge;
    }

    /**
     * add the item at top of the pinterest.
     * 
     * @param
     * @return
     */
    private void addInTopArea() {
        int i;
        for (i = mColumn - 1; i >= 0;) {
            int j = 0;
            for (j = i - 1; j >= 0; j--) {
                if (mPinTop[i] < mPinTop[j]) {
                    i = j;
                    break;
                }
            }
            if (j == -1) {
                break;
            }
        }
        // mStartAddTop = mPinTop[i];

        int count;
        while (true) {
            count = 0;
            for (i = 0; i < mColumn; i++) {
                if (mPinTop[i] + mScrollDistance > 0) {
                    if (mColumnsFirstPos[i] > 0) {
                        mColumnsFirstPos[i]--;
                        int position = mRadishPits[i].get(mColumnsFirstPos[i]);
                        ItemInfo info = (ItemInfo) mAdapter.getItem(position);
                        int itemHeight = calculateChildHeight(info.getWidth(), info.getHeight());
                        // View view = addAndMeasure(-1, getItemWidth(),
                        // mRadishPits[i].get(mColumnsFirstPos[i]));
                        mPinTop[i] -= itemHeight + mSpaceVertical;
                        // mList[i].addFirst(view);
                        mListItemPosition[i].addFirst(position);
                    } else {
                        count++;
                    }
                } else {
                    count++;
                }
            }
            if (count > mColumn - 1) {
                break;
            }
        }

    }

    private int getItemWidth() {
        return (getWidth() - this.mSpaceHorizontal * (mColumn - 1)) / mColumn;
    }

    private void layoutChild() {
        Log.w(TAG, "mScrapViews.size() = " + mScrapViews.size());

        mActiveViews.clear();
        mActiveViews.putAll(mCurrentViews);
        mCurrentViews.clear();
        for (int i = 0; i < mColumn; i++) {
            layoutList(mListItemPosition[i], i, mPinTop[i]);
        }
        if (mActiveViews.size() > 0) {
            mScrapViews.addAll(mActiveViews.values());
            for (View view : mScrapViews) {
                // removeViewInLayout(view);
                detachViewFromParent(view);
            }
        }
        if (mIsFirstLayout && mItemCount > 0) {
            View firstView = mCurrentViews.get(mListItemPosition[0].get(0));
            firstView.requestFocus();
            // mItemEdgeIv.setAlpha(255);
            // mItemEdgeIv.bringToFront();
            // mItemEdgeIv.setBackground(getContext().getResources().getDrawable(R.drawable.item_edge));
            // mItemEdgeIv.measure(MeasureSpec.EXACTLY | getItemWidth(),
            // MeasureSpec.EXACTLY | firstView.getHeight());
            // LayoutParams layoutParams = new
            // LayoutParams(mItemEdgeIv.getMeasuredWidth(),
            // mItemEdgeIv.getMeasuredHeight());
            // addViewInLayout(mItemEdgeIv, -1, layoutParams, true);
            // mItemEdgeIv.layout(firstView.getLeft(), firstView.getTop(),
            // firstView.getRight(), firstView.getBottom());

            if (mItemEdgeView != null) {
                mItemEdgeView.measure(MeasureSpec.EXACTLY | getItemWidth(), MeasureSpec.EXACTLY | firstView.getHeight());
                LayoutParams layoutParams = new LayoutParams(mItemEdgeView.getMeasuredWidth(), mItemEdgeView.getMeasuredHeight());
                addViewInLayout(mItemEdgeView, -1, layoutParams, true);
                mItemEdgeView.layout(firstView.getLeft(), firstView.getTop(), firstView.getRight(), firstView.getBottom());
            }

            mFocusPosition = 0;
            mFocusPosition = 0;
            mFocusPosInList = 0;
            mIsFirstLayout = false;
        }
        Log.w(TAG, "after mScrapViews.size() = " + mScrapViews.size());
        changeFocusPosInList(mFoucsColumn, mFocusPosition);
        if (mItemEdgeView != null) {
            mItemEdgeView.bringToFront();
        }
    }

    private void layoutList(LinkedList<Integer> list, int column, int top) {
        int columnDistance = column * (getItemWidth() + mSpaceHorizontal);
        for (int i = 0; i < list.size(); i++) {
            View child = addAndMeasure(-1, getItemWidth(), list.get(i));
            int l = columnDistance;
            int t = top;
            int r = l + child.getMeasuredWidth();
            int b = top + child.getMeasuredHeight();
            top += child.getMeasuredHeight() + mSpaceVertical;
            child.layout(l, t, r, b);
            // child.setLeft(l);
            // child.setTop(t);
            // child.setRight(r);
            // child.setBottom(b);
        }
    }

    private View addAndMeasure(int direction, int itemSize, int position) {
        View view = null;
        boolean isNewItem = true;
        if (mActiveViews.get(position) != null) {
            view = mActiveViews.remove(position);
        } else {
            if (mScrapViews.size() > 0) {
                view = mScrapViews.removeLast();
                Log.i(TAG, "scrapView count : " + mScrapViews.size());
                isNewItem = false;
            }

            view = mAdapter.getView(view, position);

            ItemInfo info = (ItemInfo) mAdapter.getItem(position);
            int itemHeight = calculateChildHeight(info.getWidth(), info.getHeight());
            view.measure(MeasureSpec.EXACTLY | itemSize, MeasureSpec.EXACTLY | itemHeight);
            LayoutParams layoutParams = new LayoutParams(view.getMeasuredWidth(), view.getMeasuredHeight());
            if (isNewItem) {
                addViewInLayout(view, direction, layoutParams, true);
            } else {
                attachViewToParent(view, -1, layoutParams);
            }
        }
        mCurrentViews.put(position, view);
        // Log.i(TAG, "addAndMeasure pos:" + position +
        // "view in layout count : " + getChildCount());
        return view;
    }

    public interface PinterestCallBack {
        public void loadMore();

        public void refresh();
    }
}
