package com.yuanmeng.cqboy.graduationview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

import com.yuanmeng.cqboy.graduationview.R;


/**
 * @author LichFaker on 16/3/12.
 * @Email lichfaker@gmail.com
 */
public abstract class BaseScaleView extends View {


    protected int mMax; // 最大刻度
    protected int mMin; // 最小刻度

    protected int mCountScale; // 滑动的总刻度

    protected int mScaleScrollViewRange;

    protected int mScaleMargin; // 刻度间距
    protected int mScaleHeight; // 刻度线的高度
    protected int mScaleMaxHeight; // 整刻度线高度

    protected int mRectWidth; // 总宽度
    protected int mRectHeight; // 高度

    protected Scroller mScroller;
    protected int mScrollLastX;

    protected int mTempScale; // 用于判断滑动方向
    protected int mMidCountScale; // 中间刻度

    protected double scaleValue;  // 默认值

    protected OnScrollListener mScrollListener;

    public interface OnScrollListener {
        void onScaleScroll(int scale);
    }

    public BaseScaleView(Context context) {
        super(context);
        init(null);
    }

    public BaseScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BaseScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        // 获取自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.scale);
        mMin = ta.getInteger(R.styleable.scale_view_min, 0);
        mMax = ta.getInteger(R.styleable.scale_view_max, 200);
        mScaleMargin = ta.getDimensionPixelOffset(R.styleable.scale_view_margin, 11);
        mScaleHeight = ta.getDimensionPixelOffset(R.styleable.scale_view_height, 16);
        ta.recycle();
        mScroller = new Scroller(getContext());
        initVar();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画笔
        Paint paint = new Paint();
        // 抗锯齿
        paint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paint.setDither(true);
        // 空心
        paint.setStyle(Paint.Style.STROKE);
        // 文字居中
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#e7e3e4"));  // 外框颜色
        onDrawLine(canvas, paint);
        paint.setColor(Color.parseColor("#b6b6b6"));  // 内容颜色
        onDrawScale(canvas, paint); // 画刻度
        paint.setColor(Color.parseColor("#FF4081"));  // 指针颜色
        paint.setStrokeWidth(1.5f);
        onDrawPointer(canvas, paint); // 画指针
        onDrawCover(canvas, paint); // 画覆盖层

        super.onDraw(canvas);
    }

    protected abstract void initVar();

    // 画线
    protected abstract void onDrawLine(Canvas canvas, Paint paint);

    // 画刻度
    protected abstract void onDrawScale(Canvas canvas, Paint paint);

    // 画指针
    protected abstract void onDrawPointer(Canvas canvas, Paint paint);

    // 画覆盖层
    protected abstract void onDrawCover(Canvas canvas, Paint paint);

    /**
     * 使用Scroller时需重写
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        // 判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 通过重绘来不断调用computeScroll
            invalidate();
        }
    }

    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
                dy);
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnScrollListener(OnScrollListener listener) {
        this.mScrollListener = listener;
    }
}
