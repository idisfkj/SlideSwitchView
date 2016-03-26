package com.idisfkj.slideswitchview.view;

/**
 * Created by idisfkj on 16/3/24.
 * Email : idisfkj@qq.com.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.idisfkj.slideswitchview.R;


public class SlideSwitchView extends View {

    private static final int DEFAULT_THEME_COLOR = Color.BLUE;
    private static final boolean DEFAULT_ISOPEN = true;
    private static final int DEFAULT_SHAPE = 1;
    private static final int pandding = 6;

    private int theme_color;
    private boolean isOpen;
    private int shape;

    private int front_start_left = pandding;
    private int front_left;
    private int min_left;
    private int max_left;
    private int alpha;

    private Rect backRect;
    private Rect frontRect;
    private RectF backCicleRectf;
    private RectF frontCicleRectf;

    private Paint mPaint;
    private SlideSwitchListener mSlideSwitchListener;
    private boolean slideable = true;
    private int startX;
    private int lastX;
    private int distance;

    public SlideSwitchView(Context context) {
        this(context, null);
    }

    public SlideSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.slideswitchview);
        theme_color = ta.getColor(R.styleable.slideswitchview_themeColor, DEFAULT_THEME_COLOR);
        isOpen = ta.getBoolean(R.styleable.slideswitchview_isOpen, DEFAULT_ISOPEN);
        shape = ta.getInt(R.styleable.slideswitchview_shape, DEFAULT_SHAPE);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasureInt(280, widthMeasureSpec);
        int height = getMeasureInt(140, heightMeasureSpec);
        if (shape == 2) {
            if (width < height) {
                width = height * 2;
            }
        }
        setMeasuredDimension(width, height);
        initValue();
    }

    /**
     * 初始化参数值
     */
    public void initValue() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        backRect = new Rect(0, 0, width, height);
        frontRect = new Rect();
        backCicleRectf = new RectF();
        frontCicleRectf = new RectF();
        min_left = pandding;
        if (shape == 1) {
            max_left = width / 2;
        } else {
            //宽度－直径与pandding
            max_left = width - (height - 2 * pandding) - pandding;
        }

        if (isOpen) {
            front_left = max_left;
            alpha = 255;
        } else {
            front_left = pandding;
            alpha = 0;
        }
        front_start_left = front_left;
    }


    /**
     * 获取真正的尺寸
     *
     * @param DefaultSize
     * @param measureSpec
     * @return
     */
    public int getMeasureInt(int DefaultSize, int measureSpec) {
        int result;
        int size = MeasureSpec.getSize(measureSpec);
        int model = MeasureSpec.getMode(measureSpec);
        if (model == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = DefaultSize;
            if (model == MeasureSpec.AT_MOST) {
                result = Math.min(size, result);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw Rect
        if (shape == 1) {
            mPaint.setColor(Color.GRAY);
            canvas.drawRect(backRect, mPaint);
            mPaint.setColor(theme_color);
            mPaint.setAlpha(alpha);
            canvas.drawRect(backRect, mPaint);
            mPaint.setColor(Color.WHITE);
            frontRect.set(front_left, pandding, front_left + getMeasuredWidth() / 2 - pandding
                    , getMeasuredHeight() - pandding);
            canvas.drawRect(frontRect, mPaint);
        } else {
            //draw Cicle
            float mRadius;
            mRadius = backRect.height() / 2 - pandding;
            mPaint.setColor(Color.GRAY);
            backCicleRectf.set(backRect);
            canvas.drawRoundRect(backCicleRectf, mRadius, mRadius, mPaint);
            mPaint.setColor(theme_color);
            mPaint.setAlpha(alpha);
            canvas.drawRoundRect(backCicleRectf, mRadius, mRadius, mPaint);
            frontCicleRectf.set(front_left, pandding, front_left + getMeasuredHeight() - 2 * pandding
                    , getMeasuredHeight() - pandding);
            mPaint.setColor(Color.WHITE);
            canvas.drawRoundRect(frontCicleRectf, mRadius, mRadius, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!slideable) {
            return super.onTouchEvent(event);
        }
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                lastX = (int) event.getRawX();
                distance = lastX - startX;
                int offset = distance + front_start_left;
                offset = offset > max_left ? max_left : offset;
                offset = offset < min_left ? min_left : offset;
                if (offset >= min_left && offset <= max_left) {
                    front_left = offset;
                    alpha = (int) (255 * (float) front_left / (float) max_left);
                    initloop();
                }
                break;
            case MotionEvent.ACTION_UP:
                int totalX = (int) (event.getRawX() - startX);
                front_start_left = front_left;
                boolean toRight;
                toRight = front_start_left > max_left / 2 ? true : false;
                // 点击时改变状态
                if (Math.abs(totalX) < 3) {
                    toRight = !toRight;
                }
                startToMove(toRight);
                break;
            default:
                break;
        }


        return true;
    }

    private void startToMove(final boolean toRight) {
        //实例化
        ValueAnimator valueAnimator = ValueAnimator.ofInt(front_left, toRight ? max_left : min_left);
        //设置插值器
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(500);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                front_left = (int) animation.getAnimatedValue();
                alpha = (int) (255 * (float) front_left / (float) max_left);
                initloop();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (toRight) {
                    isOpen = true;
                    if (mSlideSwitchListener != null)
                        mSlideSwitchListener.onOpen();
                    front_start_left = max_left;
                } else {
                    isOpen = false;
                    if (mSlideSwitchListener != null)
                        mSlideSwitchListener.onClose();
                    front_start_left = min_left;
                }
            }
        });
    }

    private void initloop() {
        //Looper.myLooper()获得当前的线程的Looper对象
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //只能在主线程调用
            invalidate();
        } else {
            //在工作者线程调用
            postInvalidate();
        }
    }

    public void setSlideable(boolean slideable) {
        this.slideable = slideable;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public void setState(boolean isOpen) {
        this.isOpen = isOpen;
        initValue();
        initloop();
        if (mSlideSwitchListener != null) {
            if (isOpen) {
                mSlideSwitchListener.onOpen();
            } else {
                mSlideSwitchListener.onClose();
            }
        }
    }

    public void setSlideSwitchListener(SlideSwitchListener sideSwitchListener) {
        mSlideSwitchListener = sideSwitchListener;
    }

    public interface SlideSwitchListener {
        public void onOpen();

        public void onClose();

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        //存储数据
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean("isOpen", this.isOpen);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //获取存储的数据
        if (state instanceof Bundle) {
            this.isOpen = ((Bundle) state).getBoolean("isOpen");
            state = ((Bundle) state).getParcelable("instaceState");
        }
        super.onRestoreInstanceState(state);
    }
}
