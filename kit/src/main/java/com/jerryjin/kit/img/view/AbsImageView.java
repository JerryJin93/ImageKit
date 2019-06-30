package com.jerryjin.kit.img.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.jerryjin.kit.graphics.BitmapHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Jerry
 * Generated at: 2019/5/2 11:39
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version: 0.0.1
 * Description:
 */
public abstract class AbsImageView extends ImageView {


    private static final String TAG = "AbsImageView";
    private static final boolean DEBUG = true;
    protected Matrix mMatrix;
    @ScaleTypeB
    protected int mScaleType;

    public AbsImageView(Context context) {
        this(context, null);
    }

    public AbsImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cInit(context, attrs);
    }

    private void cInit(Context context, AttributeSet attrs) {
        init(context, attrs);
        mMatrix = new Matrix();
        mScaleType = ScaleType.CENTER_CROP;
    }

    protected abstract void init(Context context, AttributeSet attrs);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    protected int getMinimumMeasuredDimen() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight());
    }

    protected int measureWidth(int widthMeasureSpec) {
        return getMinimumMeasuredDimen();
    }

    protected int measureHeight(int heightMeasureSpec) {
        return getMinimumMeasuredDimen();
    }

    protected Bitmap innerDrawableToBitmap(Drawable drawable) {
        return BitmapHelper.drawableToBitmap(drawable, TAG);
    }

    protected Bitmap getRoundBitmap(Bitmap source, Paint mPaint) {
        if (source == null) {
            Log.e(TAG, "Method getRoundBitmap has been invoked. Null given bitmap.");
            return null;
        }

        int width = source.getWidth();
        int height = source.getHeight();

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int squareEdgeLen;
        Bitmap bitmap;
        if (width > measuredWidth || height > measuredHeight) {
            Log.i(TAG, "scaled.");
            squareEdgeLen = Math.min(measuredWidth, measuredHeight);
            bitmap = Bitmap.createScaledBitmap(source, squareEdgeLen, squareEdgeLen, false);
        } else {
            // TODO: 2019/6/9 FitCenter
            Log.i(TAG, "not scaled.");
            bitmap = source;
            squareEdgeLen = Math.min(width, height);
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        resetPaint(mPaint);

        canvas.drawCircle(squareEdgeLen / 2f, squareEdgeLen / 2f, squareEdgeLen / 2f, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, mPaint);

        return output;
    }

    protected Bitmap getRoundCornerBitmap(Bitmap source, Paint mPaint, RectF rcBound, int borderRadius) {
        if (source == null) {
            Log.e(TAG, "Method getRoundCornerBitmap has been invoked. Null given bitmap.");
            return null;
        }
        int width = source.getWidth();
        int height = source.getHeight();
        int measuredWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int measuredHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        boolean fits = width == measuredWidth && height == measuredHeight;

        float whRatio = width * 1f / height;

        Bitmap bitmap;

        if (!fits) {
            if (DEBUG) {
                Log.d(TAG, "scaling in demand.");
            }

            if (mScaleType == ScaleType.FIT_XY) {
                mMatrix.reset();
                bitmap = Bitmap.createScaledBitmap(source, measuredWidth, measuredHeight, false);
            } else if (mScaleType == ScaleType.CENTER) {
                bitmap = source;
                mMatrix.setTranslate(Math.round(measuredWidth - width) * 0.5f, Math.round(measuredHeight - height) * 0.5f);
            } else if (mScaleType == ScaleType.CENTER_CROP) {
                float scale;
                float dx = 0, dy = 0;

                // width / height > measuredWidth / measuredHeight
                // height / measuredHeight > width / measuredWidth
                if (width * measuredHeight > height * measuredWidth) {
                    scale = measuredHeight * 1f / height;
                    dx = (measuredWidth - scale * width) * 0.5f;
                } else {
                    scale = measuredWidth * 1f / width;
                    dy = (measuredHeight - scale * height) * 0.5f;
                }
                mMatrix.setScale(scale, scale);
                mMatrix.postTranslate(dx, dy);
                bitmap = source;
            } else if (mScaleType == ScaleType.CENTER_INSIDE) {
                float scale;
                float dx, dy;

                if (width < measuredWidth && height <= measuredHeight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) measuredWidth / (float) width, (float) measuredHeight / (float) height);
                }
                dx = Math.round((measuredWidth - scale * width) * 0.5f);
                dy = Math.round((measuredHeight - scale * height) * 0.5f);
                mMatrix.setScale(scale, scale);
                mMatrix.postTranslate(dx, dy);
                bitmap = source;
            } else {
                // default: FIT_CENTER
                bitmap = source;
            }

        } else {
            if (DEBUG) {
                Log.d(TAG, "fit, and scaling is not required.");
            }
            bitmap = source;
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(output);

        resetPaint(mPaint);

        canvas.drawRoundRect(rcBound, borderRadius, borderRadius, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        if (!mMatrix.isIdentity()) {
            canvas.concat(mMatrix);
        }
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        return output;
    }


    private void resetPaint(Paint mPaint) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
    }

    public void setScaleType(@ScaleTypeB int scaleType) {
        this.mScaleType = scaleType;
        invalidate();
    }

    public interface ScaleType {
        int CENTER = 0;
        int CENTER_CROP = 1;
        int CENTER_INSIDE = 2;
        int FIT_CENTER = 3;
        int FIT_END = 4;
        int FIT_START = 5;
        int FIT_XY = 6;
        int MATRIX = 7;
    }

    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @IntDef({ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE,
            ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.FIT_START, ScaleType.FIT_XY, ScaleType.MATRIX})
    public @interface ScaleTypeB {
    }
}
