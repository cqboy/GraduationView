package com.yuanmeng.cqboy.graduationview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.math.BigDecimal;

/**
 * 竖形滚动刻度尺
 */
public class VerticalScaleView extends BaseScaleView {

    public VerticalScaleView(Context context) {
        super(context);
    }

    public VerticalScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScaleView(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initVar() {
        mRectHeight = (mMax - mMin) * mScaleMargin;
        mRectWidth = mScaleHeight * 8;
        mScaleMaxHeight = mScaleHeight * 2;

        // 设置layoutParams
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                mRectWidth, mRectHeight);
        this.setLayoutParams(lp);
    }

    /**
     * -- 设置默认刻度值
     *
     * @param height
     */
    public void setScaleValue(int height) {
        scaleValue = height;
        if (scaleValue >= mMax)
            scaleValue = mMax;
        else if (scaleValue <= mMin)
            scaleValue = mMax;
        scaleValue = (scaleValue - mMin) * mScaleMargin;
        //
        scaleValue -= 360 * getResources().getDisplayMetrics().density / 2;
        // scaleValue -= getMeasuredHeight() / 2;
        // 设置默认值
        mScroller.setFinalY((int) scaleValue);
        postInvalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // int width = MeasureSpec.makeMeasureSpec(mRectWidth,
        // MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScaleScrollViewRange = getMeasuredHeight();
        mTempScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
        mMidCountScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        canvas.drawLine(0, 0, 0, mRectHeight, paint);
        canvas.drawLine(getWidth() - paint.getStrokeWidth() - 1, 0, getWidth()
                - paint.getStrokeWidth() - 1, mRectHeight, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        paint.setTextSize(mRectWidth / 4);

        for (int i = 0, k = mMin; i <= mMax - mMin; i++) {
            if (i % 10 == 0) { // 整值
                canvas.drawLine(0, i * mScaleMargin, mScaleMaxHeight, i
                        * mScaleMargin, paint);
                // 整值文字(身高--换算成m，取后面两位小数点)
                canvas.drawText(
                        new BigDecimal(k / 100f).setScale(2,
                                BigDecimal.ROUND_HALF_UP).toString(),
                        mScaleMaxHeight + 40,
                        i * mScaleMargin + paint.getTextSize() / 3, paint);
                k += 10;
            } else {
                canvas.drawLine(0, i * mScaleMargin, mScaleHeight, i
                        * mScaleMargin, paint);
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {

        // 每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;
        // 根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalY = mScroller.getCurrY();
        // 滑动的刻度
        int tmpCountScale = (int) Math.rint((double) finalY
                / (double) mScaleMargin); // 四舍五入取整
        // 总刻度
        mCountScale = tmpCountScale + countScale + mMin;
        if (mScrollListener != null) { // 回调方法
            mScrollListener.onScaleScroll(mCountScale);
        }
        canvas.drawLine(0, countScale * mScaleMargin + finalY, mScaleMaxHeight
                + mScaleHeight / 2, countScale * mScaleMargin + finalY, paint);
    }

    @Override
    protected void onDrawCover(Canvas canvas, Paint paint) {

        // 第一个,第二个参数表示渐变起点 可以设置起点终点在对角等任意位置
        // 第三个,第四个参数表示渐变终点
        // 第五个参数表示渐变颜色
        // 第六个参数可以为空,表示坐标,值为0-1 new float[] {0.25f, 0.5f, 0.75f, 1 }
        // 1，如果这是空的，颜色均匀分布，沿梯度线。
        // 第七个表示平铺方式
        // 1 ,CLAMP重复最后一个颜色至最后
        // 2 ,MIRROR重复着色的图像水平或垂直方向已镜像方式填充会有翻转效果
        // 2 ,REPEAT重复着色的图像水平或垂直方向

        // 绘画左侧阴影
        int weight = 150;
        int startY = getScrollY();
        LinearGradient mGradient = new LinearGradient(0, startY, 0, weight
                + startY, new int[]{Color.WHITE, Color.TRANSPARENT}, null,
                Shader.TileMode.CLAMP);
        paint.setShader(mGradient);
        paint.setStyle(Paint.Style.FILL);// 充满
        canvas.drawRect(0, startY, getWidth(), weight + startY, paint);

        // // 绘画右侧阴影
        startY = getHeight() - weight + getScrollY();
        mGradient = new LinearGradient(0, startY, 0, weight + startY,
                new int[]{Color.TRANSPARENT, Color.WHITE}, null,
                Shader.TileMode.CLAMP);
        paint.setShader(mGradient);
        canvas.drawRect(0, startY, getWidth(), weight + startY, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataY = mScrollLastX - y;
                if (mCountScale - mTempScale < 0) { // 向下边滑动
                    if (mCountScale <= mMin && dataY <= 0) // 禁止继续向下滑动
                        return super.onTouchEvent(event);
                } else if (mCountScale - mTempScale > 0) { // 向上边滑动
                    if (mCountScale >= mMax && dataY >= 0) // 禁止继续向上滑动
                        return super.onTouchEvent(event);
                }
                smoothScrollBy(0, dataY);
                mScrollLastX = y;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
                if (mCountScale < mMin)
                    mCountScale = mMin;
                if (mCountScale > mMax)
                    mCountScale = mMax;
                int finalY = (mCountScale - mMidCountScale) * mScaleMargin;
                mScroller.setFinalY(finalY); // 纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}
