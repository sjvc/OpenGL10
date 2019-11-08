package com.japg.mastermoviles.opengl10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.japg.mastermoviles.opengl10.util.Utils;

public class SwipeView extends View implements GestureDetector.OnGestureListener {
    private final static float   DEFAULT_WIDTH_DP       = 100f;
    private final static float   DEFAULT_HEIGHT_DP      = 100f;
    private final static float   TEXT_SIZE_SP           = 40f;
    private final static float   TEXT_STROKE_WIDTH_DP   = 6f;
    private final static String  TEXT_COLOR             = "#0069c0";
    private final static String  TEXT_STROKE_COLOR      = "#ffffff";
    private final static String  BG_COLOR               = "#2196f3";
    private final static String  LINES_COLOR            = "#ffffff";
    private final static float   LINES_SEPARATION_DP    = 9f;
    private final static float   LINES_STROKE_WIDTH_DP  = 1f;

    private OnSwipeListener mOnSwipeListener;
    private GestureDetectorCompat mGestureDetector;

    private Paint mLinePaint;
    private int mLinesSeparation;
    private int mLinesStart = 0;

    private Paint mTextPaint;
    private Paint mTextStrokePaint;
    private String mText = null;
    private String mInstructionsText;

    private Paint mBackgroundPaint;

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

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.parseColor(LINES_COLOR));
        mLinePaint.setStrokeWidth(Utils.convertDpToPixel(LINES_STROKE_WIDTH_DP, getContext()));

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.parseColor(TEXT_COLOR));
        mTextPaint.setTextSize(Utils.convertDpToPixel(TEXT_SIZE_SP, getContext()));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setStyle(Paint.Style.FILL);

        mTextStrokePaint = new Paint();
        mTextStrokePaint.setColor(Color.parseColor(TEXT_STROKE_COLOR));
        mTextStrokePaint.setTextSize(Utils.convertDpToPixel(TEXT_SIZE_SP, getContext()));
        mTextStrokePaint.setAntiAlias(true);
        mTextStrokePaint.setFakeBoldText(true);
        mTextStrokePaint.setStyle(Paint.Style.STROKE);
        mTextStrokePaint.setStrokeWidth(Utils.convertDpToPixel(TEXT_STROKE_WIDTH_DP, getContext()));

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.parseColor(BG_COLOR));

        mLinesSeparation = Utils.convertDpToPixel(LINES_SEPARATION_DP, getContext());
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        mOnSwipeListener = listener;
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public void setInstructionsText(String text) {
        mInstructionsText = text;
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mBackgroundPaint);

        for (int i=0, width=getMeasuredWidth(), height=getMeasuredHeight(); i<width; i++) {
            canvas.drawLine(i * mLinesSeparation + mLinesStart, 0, i * mLinesSeparation + mLinesStart, height, mLinePaint);
        }

        if (mText != null && mText.length() > 0) {
            float textWidth = mTextPaint.measureText(mText);
            int x = (int)(getMeasuredWidth()/2 - textWidth/2);
            int y = (int) ((getMeasuredHeight() / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
            canvas.drawText(mText, x, y, mTextStrokePaint);
            canvas.drawText(mText, x, y, mTextPaint);
        }
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
        if (mInstructionsText != null && mInstructionsText.length() > 0) {
            Toast.makeText(getContext(), mInstructionsText, Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Crear efecto de desplazamiento de las líneas
        mLinesStart = (mLinesStart - (int)distanceX) % mLinesSeparation;
        invalidate();

        // Disparar evento
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
