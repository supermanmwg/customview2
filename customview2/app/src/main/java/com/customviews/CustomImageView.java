package com.customviews;

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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.example.supermanmwg.customview2.MainActivity;
import com.example.supermanmwg.customview2.R;

/**
 * Created by supermanmwg on 15-8-31.
 */
public class CustomImageView extends View {

    private Bitmap mImage;
    private int mImageScalce;
    private static final int IMAGE_SCALE_FITXY = 0;
    private static final int IMAGE_SCALE_CENTER = 1;
    private String mTitle;
    private int mTitleColor;
    private int mTitleSize;

    private Rect mRect;
    private Rect mTextBound;
    private Paint mPaint;

    private int mWidth;
    private int mHeight;

    public CustomImageView(Context context) {
        this(context,null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyleAttr, 0);

        int n = a.getIndexCount();

        for(int i = 0; i < n; i++) {
            int attr = a.getIndex(i);

            switch (attr) {
                case R.styleable.CustomImageView_image:
                    mImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
                    break;
                case R.styleable.CustomImageView_imageScaleType:
                    mImageScalce = a.getInt(attr, 0);
                    break;
                case R.styleable.CustomImageView_titleText:
                    mTitle = a.getString(attr);
                    break;
                case R.styleable.CustomImageView_titleTextSize:
                    mTitleSize =a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomImageView_titlteTextColor:
                    mTitleColor = a.getColor(attr, Color.BLACK);;
                    break;
            }
        }

        a.recycle();

        mRect = new Rect();
        mPaint = new Paint();
        mTextBound = new Rect();
        mPaint.setTextSize(mTitleSize);

        mPaint.getTextBounds(mTitle, 0, mTitle.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * set width
         */
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        if(MeasureSpec.EXACTLY == widthSpecMode) {
            mWidth = widthSpecSize;
            Log.d("TAG", "width is exactly!");
        } else {
            int desireByImg = getPaddingLeft() + getPaddingRight() + mImage.getWidth();
            int desireByTitle = getPaddingLeft() + getPaddingRight() + mTextBound.width();

            if(MeasureSpec.AT_MOST == widthSpecMode) {
                int desire = Math.max(desireByImg, desireByTitle);
                mWidth = Math.min(desire, widthSpecSize);
                Log.d("TAG", "width is at most!");
            }
        }

        /**
         * set height
         */
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if(MeasureSpec.EXACTLY == heightSpecMode) {
            mHeight = heightSpecSize;
            Log.d("TAG", "height is exactly!");
        } else {
            int desire = getPaddingBottom() + getPaddingTop() + mImage.getHeight() + mTextBound.height();
            if(MeasureSpec.AT_MOST == heightSpecMode) {
                mHeight = Math.min(desire, heightSpecSize);
            }
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * frame border
         */
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mRect.left = getPaddingLeft();
        mRect.right = mWidth - getPaddingRight();
        mRect.top = getPaddingTop();
        mRect.bottom = mHeight - getPaddingBottom();

        mPaint.setColor(mTitleColor);
        mPaint.setStyle(Paint.Style.FILL);

        /**
         * 当前设置的宽度小于字体需要的宽度时，将文字改成省略形式　xxx...
         */
        if(mTextBound.width() > mWidth) {
            TextPaint paint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mTitle, paint,
                                            (float)mWidth - getPaddingLeft() - getPaddingRight(),
                                             TextUtils.TruncateAt.END).toString();

        } else {
            /**
             * 正常情况下，将字体居中
             */
            canvas.drawText(mTitle, mWidth / 2 - mTextBound.width() * 1.0f / 2, mHeight - getPaddingBottom(), mPaint);
        }

        /**
         * 取消掉使用的字体块
         */
        mRect.bottom -= mTextBound.height();

        if(mImageScalce ==  IMAGE_SCALE_FITXY) {
            canvas.drawBitmap(mImage, null, mRect, mPaint);
        } else {
            mRect.left = mWidth /2 - mImage.getWidth()/2;
            mRect.right = mWidth/2 + mImage.getWidth()/2;
            mRect.top = (mHeight - mTextBound.height() - mImage.getHeight()) / 2;
            mRect.bottom = (mHeight - mTextBound.height() + mImage.getHeight()) / 2;

            canvas.drawBitmap(mImage, null, mRect, mPaint);
        }
    }
}
