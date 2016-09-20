package com.yuanmeng.cqboy.graduationview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

/**
 * 水平滚动刻度尺
 */
public class HorizontalScaleView extends BaseScaleView {

    public HorizontalScaleView(Context context) {
        super(context);
    }

    public HorizontalScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScaleView(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initVar() {
        mRectWidth = (mMax - mMin) * mScaleMargin;
        mRectHeight = mScaleHeight * 8;
        mScaleMaxHeight = mScaleHeight * 2;

        // 设置layoutParams
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                mRectWidth, mRectHeight);
        this.setLayoutParams(lp);
    }

    /**
     * -- 设置默认刻度值
     *
     * @param age
     */
    public void setScaleValue(int age) {
        Calendar today = Calendar.getInstance();
        int scaleValue = today.get(Calendar.YEAR) - age;
        if (scaleValue >= mMax)
            scaleValue = mMax;
        else if (scaleValue <= mMin)
            scaleValue = mMax;
        scaleValue = (scaleValue - mMin) * mScaleMargin;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float mViewWeight = (metrics.widthPixels - 10 * metrics.density) / 2;
        scaleValue -= mViewWeight;
        // scaleValue -= getMeasuredHeight() / 2;
        int finalX = (int) Math.floor(scaleValue - (mScaleMargin - mViewWeight
                % mScaleMargin));
        ;

        // 设置默认值
        mScroller.setFinalX(finalX);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.MeasureSpec.makeMeasureSpec(mRectHeight,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
        mScaleScrollViewRange = getMeasuredWidth();
        mTempScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
        mMidCountScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        canvas.drawLine(0, mRectHeight - paint.getStrokeWidth() - 1,
                mRectWidth, mRectHeight - paint.getStrokeWidth() - 1, paint);
        canvas.drawLine(0, 0, mRectWidth, 0, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {

        paint.setTextSize(mRectHeight / 4);
        for (int i = 0, k = mMin; i <= mMax - mMin; i++) {
            canvas.drawLine(i * mScaleMargin, mRectHeight, i * mScaleMargin,
                    k % 10 == 0 ? mRectHeight - mScaleMaxHeight : mRectHeight
                            - mScaleHeight, paint);
            // 整值文字
            canvas.drawText(String.valueOf(k), i * mScaleMargin, mRectHeight
                    - mScaleMaxHeight - 20, paint);
            k += 1;
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {

        // 每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;
        // 根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalX = mScroller.getCurrX();
        // 滑动的刻度
        int tmpCountScale = (int) Math.rint((double) finalX
                / (double) mScaleMargin); // 四舍五入取整
        // 总刻度
        mCountScale = tmpCountScale + countScale + mMin;
        if (mScrollListener != null) { // 回调方法
            mScrollListener.onScaleScroll(mCountScale);
        }
        canvas.drawLine(countScale * mScaleMargin + finalX, mRectHeight,
                countScale * mScaleMargin + finalX, mRectHeight
                        - mScaleMaxHeight - mScaleHeight / 2, paint);
    }

    @Override
    protected void onDrawCover(Canvas canvas, Paint paint) {

        // 绘画左侧阴影
        int weight = 150;
        int startX = mScroller.getCurrX();
        LinearGradient mGradient = new LinearGradient(startX, 0, weight
                + startX, 0, new int[]{Color.WHITE, Color.TRANSPARENT},
                null, Shader.TileMode.CLAMP);
        paint.setShader(mGradient);
        paint.setStyle(Paint.Style.FILL);// 充满
        canvas.drawRect(startX, 0, +weight + startX, getHeight(), paint);

        // 绘画右侧阴影
        startX = getWidth() - weight + mScroller.getCurrX();
        mGradient = new LinearGradient(startX, 0, weight + startX, 0,
                new int[]{Color.TRANSPARENT, Color.WHITE}, null,
                Shader.TileMode.CLAMP);
        paint.setShader(mGradient);
        canvas.drawRect(startX, 0, +weight + startX, getHeight(), paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataX = mScrollLastX - x;
                if (mCountScale - mTempScale < 0) { // 向右边滑动
                    if (mCountScale <= mMin && dataX <= 0) // 禁止继续向右滑动
                        return super.onTouchEvent(event);
                } else if (mCountScale - mTempScale > 0) { // 向左边滑动
                    if (mCountScale >= mMax && dataX >= 0) // 禁止继续向左滑动
                        return super.onTouchEvent(event);
                }
                smoothScrollBy(dataX, 0);
                mScrollLastX = x;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:

                if (mCountScale < mMin)
                    mCountScale = mMin;
                if (mCountScale > mMax)
                    mCountScale = mMax;
                int finalX = (mCountScale - mMidCountScale) * mScaleMargin;
                mScroller.setFinalX(finalX); // 纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

}
