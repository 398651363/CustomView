package com.my.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.my.customview.R;

/**
 * Created by lzh on 2016/7/9.
 */
public class CustomImageView extends View{
    private String mTitleContent;
    private int mTitleTextColor;
    private int mTitleTextSize;
    private int mImageScaleType;
    private int mWidth;
    private int mHeight;
    private Bitmap mImage;
    private Paint mPaint;
    private Rect mBound;
    private Rect mRect;
    /**
     * 图片的缩放模式
     */
    private static final int IMAGE_SCALE_FITXY = 0;
    private static final int IMAGE_SCALE_CENTER = 1;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyleAttr, 0);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CustomImageView_titleContent:
                    mTitleContent = typedArray.getString(attr);
                    break;
                case R.styleable.CustomImageView_titleTextColor:
                    mTitleTextColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomImageView_titleTextSize:
                    mTitleTextSize = typedArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomImageView_imageSource:
                    mImage = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));
                    break;
                case R.styleable.CustomImageView_imageScaleType:
                    mImageScaleType = typedArray.getInt(attr, 0);
                    break;
            }
        }
        typedArray.recycle();

        mBound = new Rect();
        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTitleTextSize);
        mPaint.getTextBounds(mTitleContent, 0, mTitleContent.length(), mBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {// match_parent , accurate
            mWidth = specSize;
        } else {
            // 由图片决定的宽
            int desireByImg = getPaddingLeft() + getPaddingRight() + mImage.getWidth();
            // 由字体决定的宽
            int desireByTitle = getPaddingLeft() + getPaddingRight() + mBound.width();
            if (specMode == MeasureSpec.AT_MOST) {// wrap_content
                int desire = Math.max(desireByImg, desireByTitle);
                mWidth = Math.min(desire, specSize);
            }
        }

        //设置高度
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {// match_parent , accurate
            mHeight = specSize;
        } else {
            int desire = getPaddingTop() + getPaddingBottom() + mImage.getHeight() + mBound.height();
            if (specMode == MeasureSpec.AT_MOST) {// wrap_content
                mHeight = Math.min(desire, specSize);
            }
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mRect.left = getPaddingLeft();
        mRect.right = getPaddingRight();
        mRect.top = getPaddingTop();
        mRect.bottom = mHeight - getPaddingBottom();

        mPaint.setColor(mTitleTextColor);
        mPaint.setStyle(Paint.Style.FILL);

        /**
         * 当前设置的宽度小于字体需要的宽度，将字体改为xxx...
         */
        if (mBound.width() > mWidth) {
            TextPaint paint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mTitleContent, paint, (float) mWidth - getPaddingLeft() - getPaddingRight(),
                    TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg, getPaddingLeft(), mHeight - getPaddingBottom(), mPaint);
        } else {
            //正常情况，将字体居中
            canvas.drawText(mTitleContent, mWidth / 2 - mBound.width() * 1.0f / 2, mHeight - getPaddingBottom(), mPaint);
        }

        //取消使用掉的快
        mRect.bottom -= mBound.height();

        if (mImageScaleType == IMAGE_SCALE_FITXY) {
            canvas.drawBitmap(mImage, null, mRect, mPaint);
        } else {
            //计算居中的矩形范围
            mRect.left = mWidth / 2 - mImage.getWidth() / 2;
            mRect.right = mWidth / 2 + mImage.getWidth() / 2;
            mRect.top = (mHeight - mBound.height()) / 2 - mImage.getHeight() / 2;
            mRect.bottom = (mHeight - mBound.height()) / 2 + mImage.getHeight() / 2;

            canvas.drawBitmap(mImage, null, mRect, mPaint);
        }
    }
}
