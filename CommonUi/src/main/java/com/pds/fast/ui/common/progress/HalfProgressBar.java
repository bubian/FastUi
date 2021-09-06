package com.pds.fast.ui.common.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.pds.fast.ui.common.R;

/**
 * 自定义带圆点的进度条
 */
public class HalfProgressBar extends View {

    /**
     * 当前进度
     */
    private int progress = 150;
    /**
     * 最大进度
     */
    private int maxProgress = 360;
    //设置进度条背景宽度
    private float progressStrokeWidth = 15;
    //设置进度条进度宽度
//    private float marxArcStorkeWidth = 6;
    //设置进度条圆点的宽度
    private float circularDotWidth = 30;


    /**
     * 画笔对象的引用
     */
    private Paint paintBackGround;
    private Paint paintProgress;
    private Paint paintDot;
    private float progressTextSize;

    public synchronized int getProgress() {
        return progress;
    }

    /**
     * Android提供了Invalidate方法实现界面刷新，但是Invalidate不能直接在线程中调用，因为他是违背了单线程模型：Android UI操作并不是线程安全的，并且这些操作必须在UI线程中调用。
     * 而postInvalidate()在工作者线程中被调用 使用postInvalidate则比较简单，不需要handler，直接在线程中调用postInvalidate即可。
     *
     * @param progress 传过来的进度
     */
    public void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        }
        if (progress > maxProgress) {
            progress = maxProgress;
        }
        if (progress <= maxProgress) {
            this.progress = progress;
            postInvalidate();
        }
    }


    private RectF oval;
    private int roundProgressColor;
    private int roundColor;
    private int circularDotColor;
    private int progressTextColor;

    public HalfProgressBar(Context context) {
        super(context);
    }

    public HalfProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HalfProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paintBackGround = new Paint();
        paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintProgress.setStrokeCap(Paint.Cap.ROUND);
        paintDot = new Paint();
        oval = new RectF();
        //这是自定义view 必须要写的
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MyHalfProgressBar);
        roundProgressColor = mTypedArray.getColor(R.styleable.MyHalfProgressBar_roundProgressColor1, Color.YELLOW);
        roundColor = mTypedArray.getColor(R.styleable.MyHalfProgressBar_roundColor1, Color.RED);
        circularDotColor = mTypedArray.getColor(R.styleable.MyHalfProgressBar_circularDotColor1, Color.BLUE);
        progressTextSize = mTypedArray.getDimension(R.styleable.MyHalfProgressBar_progressTextSize, 15);
        progressTextColor = mTypedArray.getInteger(R.styleable.MyHalfProgressBar_progressTextColor, Color.GREEN);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getWidth();
        paintBackGround.setAntiAlias(false); // 设置画笔为抗锯齿
        paintBackGround.setColor(roundColor); // 设置画笔颜色
        paintBackGround.setStrokeWidth(progressStrokeWidth); // 线宽
        paintBackGround.setStyle(Paint.Style.STROKE);

        //进度条颜色
        paintProgress.setColor(roundProgressColor);
        paintProgress.setStrokeWidth(progressStrokeWidth);
        paintProgress.setAntiAlias(false); // 设置画笔为抗锯齿
        paintProgress.setStyle(Paint.Style.STROKE);

        oval.left = circularDotWidth / 2; //
        oval.top = circularDotWidth / 2; //
        oval.right = width - circularDotWidth / 2; //
        oval.bottom = width - circularDotWidth / 2; //
        float banjing = (width / 2 - progressStrokeWidth / 2);//半径
        //调整圆背景的大小
        canvas.drawArc(oval, 360, 360, false, paintBackGround); // 绘制红丝圆圈，即进度条背景
        Log.i("banjing", ":" + banjing);
        Log.i("banjing", "width:" + width);

        canvas.drawArc(oval, -90, progress, false, paintProgress); // 绘制进度圆弧，这里是蓝色


        //画圆点
        paintDot.setColor(circularDotColor);
        paintDot.setAntiAlias(true); // 设置画笔为抗锯齿
        paintDot.setStyle(Paint.Style.FILL);
        paintDot.setStrokeWidth(circularDotWidth);
        //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式Cap.ROUND,或方形样式Cap.SQUARE
        paintDot.setStrokeCap(Paint.Cap.ROUND);
        double a = 0;
        float x = 0;
        float y = 0;
        float v = 0;
        float v1 = 0;
        float realAngle = progress ;

        if (realAngle < 90) {
            a = (Math.PI / (double) 180) * (90 - realAngle);
            v = (float) (Math.sin(a)) * banjing;
            v1 = (float) (Math.cos(a)) * banjing;
            x = width / 2 + progressStrokeWidth / 2 - v;
            y = width / 2 + progressStrokeWidth / 2 - v1;
        }
        if (realAngle == 90) {
            x = width / 2;
            y = circularDotWidth / 2;
        }
        if (realAngle > 90 && realAngle < 180) {
            a = (Math.PI / (double) 180) * (180 - realAngle);
            v = (float) (Math.sin(a)) * banjing;
            v1 = (float) (Math.cos(a)) * banjing;
            if (realAngle >= 150) {
                x = width / 2 - progressStrokeWidth / 2 + v1;
                y = width / 2 - v;
            } else {
                x = width / 2 + v1;
                y = width / 2 + progressStrokeWidth / 2 - v;
            }

        }
        if (realAngle == 180) {
            x = width - progressStrokeWidth;
            y = width / 2;
        }
        if (realAngle > 180 && realAngle < 270) {
            a = (Math.PI / (double) 180) * (270 - progress);
            v = (float) (Math.sin(a)) * banjing;
            v1 = (float) (Math.cos(a)) * banjing;
            if (realAngle >= 240) {
                x = width / 2 - progressStrokeWidth / 2 + v;
                y = width / 2 - progressStrokeWidth / 2 + v1;
            } else {
                x = width / 2 - progressStrokeWidth / 2 + v;
                y = width / 2 + v1;
            }

        }
        if (realAngle == 270) {
            //            a = (Math.PI / (double) 180) * (270-progress);
            //            v = (float) (Math.sin(a)) * banjing;
            //            v1 = (float) (Math.cos(a)) * banjing;
            x = width / 2;
            y = width - progressStrokeWidth;
        }
        if (realAngle > 270) {
            a = (Math.PI / (double) 180) * (360 - realAngle);
            v = (float) (Math.sin(a)) * banjing;
            v1 = (float) (Math.cos(a)) * banjing;
            x = width / 2 + progressStrokeWidth / 2 - v1;
            y = width / 2 - progressStrokeWidth / 2 + v;
        }
        canvas.drawPoint(x, y, paintDot);

        //绘制中间文本
        String testProgress = ((int) progress * 100 / 360) + "%";
        TextPaint mPaint = new TextPaint();
        mPaint.setStrokeWidth(7);
        mPaint.setTextSize(progressTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setColor(progressTextColor);
        mPaint.setTextAlign(Paint.Align.LEFT);
        Rect bounds = new Rect();
        mPaint.getTextBounds(testProgress, 0, testProgress.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(testProgress, getMeasuredWidth() / 2 - bounds.width() / 2, baseline, mPaint);


    }
}
