package com.japg.mastermoviles.opengl10;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.japg.mastermoviles.opengl10.util.Utils;

public class SwipeView extends View implements GestureDetector.OnGestureListener {
    private final static float DEFAULT_WIDTH_DP  = 100;
    private final static float DEFAULT_HEIGHT_DP = 100;

    private OnSwipeListener mOnSwipeListener;
    private GestureDetectorCompat mGestureDetector;

    public SwipeView(Context context) {
        super(context);
        init();
    }

    public SwipeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwipeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetectorCompat(getContext(), this);
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        mOnSwipeListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = Utils.convertDpToPixel(DEFAULT_WIDTH_DP, getContext());
        int height = Utils.convertDpToPixel(DEFAULT_HEIGHT_DP, getContext());

        if (widthMode == MeasureSpec.EXACTLY || (widthMode == MeasureSpec.AT_MOST && width > widthSize)) {
            width = widthSize;
        }

        if (heightMode == MeasureSpec.EXACTLY || (heightMode == MeasureSpec.AT_MOST && height > heightSize)) {
            height = heightSize;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mOnSwipeListener != null) {
            return mOnSwipeListener.onSwipeEvent(distanceX, distanceY);
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnSwipeListener {
        boolean onSwipeEvent(float distanceX, float distanceY);
    }
}
