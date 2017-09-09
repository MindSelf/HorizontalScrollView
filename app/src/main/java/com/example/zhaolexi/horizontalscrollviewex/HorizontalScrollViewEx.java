package com.example.zhaolexi.horizontalscrollviewex;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by ZHAOLEXI on 2017/7/12.
 */

public class HorizontalScrollViewEx extends ViewGroup {

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mChildrenSize;
    private int mChildWidth;
    private int mLastXIntercept;
    private int mLastYIntercept;
    private int mLastX;
    private int mLastY;
    private int mChildIndex;

    public HorizontalScrollViewEx(Context context) {
        this(context, null);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mScroller == null) {
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        mChildrenSize = childCount;

        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                final int childWidth = childView.getMeasuredWidth();
                mChildWidth = childWidth;
                childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        final int childCount = getChildCount();

        int measureWidth;
        int measureHeight;
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);


        if (childCount==0) {
            //childCount=0时根据LayoutParams中宽和高来做处理
            if (getLayoutParams().width > 0 && getLayoutParams().height > 0) {
                setMeasuredDimension(getLayoutParams().width, getLayoutParams().height);
            } else if (getLayoutParams().height == LayoutParams.WRAP_CONTENT || getLayoutParams().width == LayoutParams.WRAP_CONTENT) {
                setMeasuredDimension(0,0);
            }else{
                //MATCH_PARENT
                if (getLayoutParams().width < 0) {
                    setMeasuredDimension(widthSpecSize,getLayoutParams().height);
                }else if(getLayoutParams().height<0){
                    setMeasuredDimension(getLayoutParams().width,heightSpecSize);
                }else{
                    setMeasuredDimension(getLayoutParams().width,getLayoutParams().height);
                }
            }

            //需要重写AT_MOST模式，否则其默认值为其父类最大剩余空间
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            //放前面会出现nullPointerException
            final View childView = getChildAt(0);
            measureWidth = childView.getMeasuredWidth() * childCount;
            measureHeight = childView.getMeasuredHeight();
            setMeasuredDimension(measureWidth, measureHeight);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            final View childView = getChildAt(0);
            measureHeight = childView.getMeasuredHeight();
            setMeasuredDimension(widthSpecSize, measureHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            final View childView = getChildAt(0);
            measureWidth = childView.getMeasuredWidth() * childCount;
            setMeasuredDimension(measureWidth, heightSpecSize);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;

        //开始和结束都要记录位置
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        //外部拦截法
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                //这个优化不要，会导致滑动时所有触摸事件都被父控件拦截
                //                if (!mScroller.isFinished()) {
                //                    mScroller.abortAnimation();
                //                    intercepted=true;
                //                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;
                if (Math.abs(deltaX) - Math.abs(deltaY) > 20) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                //若拦截了，子控件不能响应点击事件
                intercepted = false;
                break;
            default:
                break;
        }

        mLastXIntercept = x;
        mLastYIntercept = y;
        //给onTouchEvent用
        mLastX = x;
        mLastY = y;

        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                scrollBy(-deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);  //统计1s内的速度
                float xVelocity = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocity) >= 50) {
                    //速度大于阈值,翻页
                    mChildIndex = xVelocity > 0 ? mChildIndex - 1 : mChildIndex + 1;
                } else {
                    //根据滑动的距离是否超过半页来判断是否翻页
                    mChildIndex = (scrollX + mChildWidth / 2) / mChildWidth;
                }
                //0<=mChildIndex<mChildSize-1
                mChildIndex = Math.max(0, Math.min(mChildIndex, mChildrenSize - 1));
                //scroller要滑动的距离
                int dx = mChildIndex * mChildWidth - scrollX;
                smoothScrollBy(dx, 0);
                mVelocityTracker.clear();
                break;
            default:
                break;
        }

        mLastX = x;
        mLastY = y;

        return true; //说明有消耗该事件
    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(getScrollX(), 0, dx, 0, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        //取消动画，以及进行一些回收操作
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }
}
