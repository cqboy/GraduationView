package com.yuanmeng.cqboy.graduationview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/6/29.
 */
public class RoundScaleView extends View {

    // 圆环，文字画笔
    private Paint mPaint, mPaintText;
    // 刻度数量
    private int maxColorNumber = 18;
    // 刻度之间弧度
    private float singlPoint = 9;
    private float lineWidth = 0.3f;
    // 圆环宽度
    private int ringWidth;
    // 文字大小
    private float textSize;
    // 刻度旋转度数
    private float rotation, preRotation;
    // 刻度文本最小值
    private int minGraduation;
    // 刻度文本间隔值
    private int graduationGap;
    // 当前刻度值
    private int scale;

    /**
     * -- 设置默认刻度值
     *
     * @param weight
     */
    public void setScaleValue(int weight) {

        int maxGraduation = (maxColorNumber - 1) * graduationGap
                + minGraduation;
        if (weight >= maxGraduation)
            rotation = 160;
        else if (weight <= minGraduation - graduationGap)
            rotation = -197;
        else {
            int value = weight
                    - (maxGraduation + minGraduation + graduationGap) / 2;
            rotation = value
                    / ((maxGraduation - minGraduation + graduationGap) / 360f);
        }
        if (changeListener != null)
            changeListener.onScaleChange(weight);
    }

    public static final int[] ATTR = {};

    private OnScaleChangeListener changeListener;

    public void setOnScaleChangeListener(OnScaleChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public RoundScaleView(Context context) {
        super(context);
        init(null);
    }

    public RoundScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RoundScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        // 自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, ATTR);
        ta.recycle();

        singlPoint = (float) 360 / (float) maxColorNumber;
        ringWidth = (int) (45 * getContext().getResources().getDisplayMetrics().density);
        textSize = 35;
        minGraduation = 30;
        graduationGap = 10;
        scale = maxColorNumber * graduationGap / 2 + minGraduation;

        mPaint = new Paint();
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        // 文字居中
        mPaint.setTextAlign(Paint.Align.CENTER);

        mPaintText = new Paint();
        mPaintText.setColor(Color.parseColor("#999999"));
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);
    }

    private int getDpValue(int w) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w,
                getContext().getResources().getDisplayMetrics());
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int roundWidth1 = 2;
        mPaint.setStrokeWidth(roundWidth1);
        mPaint.setColor(Color.parseColor("#e7e3e2"));

        int centre = getWidth() / 2; // 获取圆心的x坐标
        int radius = centre - roundWidth1 / 2; // 圆环的半径

        // 绘制半孤园
        mPaint.setStyle(Paint.Style.STROKE);
        RectF oval = new RectF(centre - radius, -radius, centre + radius,
                radius); // 用于定义的圆弧的形状和大小的界限
        canvas.drawArc(oval, 0, 180, false, mPaint);

        // 绘制半孤圆环
        mPaint.setColor(Color.parseColor("#fdfdfd"));
        mPaint.setStrokeWidth(ringWidth);
        oval = new RectF(oval.left + ringWidth / 2 + roundWidth1 / 2, oval.top
                + ringWidth / 2 + roundWidth1 / 2, oval.right - -roundWidth1
                / 2 - ringWidth / 2, oval.bottom - roundWidth1 / 2 - ringWidth
                / 2); // 用于定义的圆弧的形状和大小的界限
        canvas.drawArc(oval, 0, 180, false, mPaint);

        // 绘制刻度
        float start = 90f + rotation;
        float calibration = ringWidth / 2.8f;
        float calibrationWidth = ringWidth / 5f;
        mPaint.setStrokeWidth(calibrationWidth);
        RectF oval2 = new RectF(oval.left + calibration,
                oval.top + calibration, oval.right - calibration, oval.bottom
                - calibration); // 用于定义的圆弧的形状和大小的界限
        mPaint.setColor(Color.parseColor("#999999"));
        for (int i = 0; i < maxColorNumber; i++) {
            canvas.drawArc(oval2, start + singlPoint - lineWidth, lineWidth,
                    false, mPaint); // 绘制间隔快
            start = (start + singlPoint);
        }

        // 绘画刻度标识
        mPaint.setStrokeWidth(ringWidth / 4f);
        mPaint.setColor(Color.parseColor("#2fb27c"));
        canvas.drawArc(oval2, 90 - lineWidth, lineWidth, false, mPaint); // 绘制间隔快

        // 绘制刻度文字
        for (int i = 0; i < maxColorNumber; i++) {
            canvas.save();// 保存当前画布
            canvas.rotate(180 - (360 / maxColorNumber * i) + rotation, centre,
                    0);
            canvas.drawText(graduationGap * i + minGraduation + "", centre,
                    radius + textSize - ringWidth + calibrationWidth
                            + getDpValue(4), mPaintText);
            canvas.restore(); // 恢复当前画布
        }

        // 绘制半孤圆环
        mPaint.setColor(Color.parseColor("#e7e3e2"));
        mPaint.setStrokeWidth(roundWidth1);
        oval = new RectF(oval.left + ringWidth / 2 + roundWidth1 / 2, oval.top
                + ringWidth / 2 + roundWidth1 / 2, oval.right - -roundWidth1
                / 2 - ringWidth / 2, oval.bottom - roundWidth1 / 2 - ringWidth
                / 2); // 用于定义的圆弧的形状和大小的界限
        canvas.drawArc(oval, 0, 180, false, mPaint);

        // 绘制中心园
        radius = radius - ringWidth;
        mPaint.setColor(Color.parseColor("#2fb27c"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centre, 0, radius, mPaint); // 画出圆环

        // 绘制横线
        mPaint.setColor(Color.parseColor("#e7e3e2"));
        canvas.drawLine(0, 0, getWidth(), 0, mPaint);
    }

    /**
     * -- 手势控制
     */
    private double startX, startY, centerX, centerY;

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                centerX = getWidth() / 2;
                centerY = 0;
                startX = event.getX();
                startY = event.getY();
                preRotation = rotation;
                break;
            case MotionEvent.ACTION_MOVE:

                // 算出角度值
                // cos∠BAC=AB·AC/|AB||AC|
                double a = (startX - centerX) * (event.getX() - centerX)
                        + (startY - centerY) * (event.getY() - centerY);
                double b = Math.sqrt(Math.pow(startX - centerX, 2)
                        + Math.pow(startY - centerY, 2));
                double c = Math.sqrt(Math.pow(event.getX() - centerX, 2)
                        + Math.pow(event.getY() - centerY, 2));
                rotation = (float) (Math.acos(a / (b * c)) * 180 / Math.PI);
                if (event.getX() > startX)
                    rotation = -rotation;
                rotation += preRotation;
                System.out.println("---------rotation=" + rotation);
                // 根据旋转度数计算出刻度值
                int scalevalue = scale
                        + (int) (rotation % 360 / 360f * maxColorNumber * graduationGap);
                if (rotation < 0)
                    scalevalue--;
                int maxGraduation = (maxColorNumber - 1) * graduationGap
                        + minGraduation;
                if (scalevalue > maxGraduation) {
                    scalevalue = scalevalue % maxGraduation + minGraduation
                            - graduationGap;
                }
                if (scalevalue <= minGraduation - graduationGap) {
                    scalevalue = maxGraduation - (minGraduation - graduationGap)
                            + scalevalue;
                }
                // 刻度改变值回调
                if (changeListener != null)
                    changeListener.onScaleChange(scalevalue);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    /**
     * @author Administrator
     */
    public interface OnScaleChangeListener {

        void onScaleChange(int scale);
    }
}
