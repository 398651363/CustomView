package com.my.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.my.customview.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by lzh on 2016/7/9.
 */
public class CustomTitleView extends View{
    private String mTitleContent;
    private int mTitleTextColor;
    private int mTitleTextSize;
    private Rect mBound;
    private TextPaint mPaint;

    public CustomTitleView(Context context) {
        this(context, null);
    }

    public CustomTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获得自定义样式的属性
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTitleView, defStyleAttr, 0);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CustomTitleView_titleContent:
                    mTitleContent = typedArray.getString(attr);
                    break;
                case R.styleable.CustomTitleView_titleTextColor:
                    mTitleTextColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomTitleView_titleTextSize:
                    mTitleTextSize = typedArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();

        //绘制文本
        mPaint = new TextPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTitleTextSize);
        mPaint.setColor(mTitleTextColor);
        mBound = new Rect();
        mPaint.getTextBounds(mTitleContent, 0, mTitleContent.length(), mBound);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleContent = randomTextContent();
                postInvalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        mPaint.setColor(mTitleTextColor);
        canvas.drawText(mTitleContent, (getWidth() - mBound.width()) / 2, (getHeight() + mBound.height()) / 2, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            mPaint.setTextSize(mTitleTextSize);
            mPaint.getTextBounds(mTitleContent, 0, mTitleContent.length(), mBound);
            float textWidth = mBound.width();
            int desired = (int)(getPaddingLeft() + textWidth + getPaddingRight());
            width = desired;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            mPaint.setTextSize(mTitleTextSize);
            mPaint.getTextBounds(mTitleContent, 0, mTitleContent.length(), mBound);
            float textHeight = mBound.height();
            int desired = (int)(getPaddingTop() + textHeight + getPaddingBottom());
            height = desired;
        }

        setMeasuredDimension(width, height);
    }

    private String randomTextContent() {
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4) {
            set.add(random.nextInt(10));
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Integer integer : set) {
            stringBuffer.append("" + String.valueOf(integer));
        }
        return stringBuffer.toString();
    }
}
