package com.jerryjin.kit.img.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.jerryjin.kit.R;
import com.jerryjin.kit.math.MathHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Jerry
 * Generated at: 2019/5/2 10:36
 * WeChat: enGrave93
 * Description: 人有悲欢离合，月有阴晴圆缺。
 */
public class CrescentImageView extends AbsImageView {

    public static final int DIRECTION_MODE_COARSE = 0;
    public static final int DIRECTION_MODE_FINE = 1;

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_TOP = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_BOTTOM = 3;

    public static final int ERROR_CODE = -1;
    /**
     * In pixels.
     */
    public static final int DEFAULT_OFFSET = 5;
    private static final String TAG = "CrescentImageView";
    private static final boolean DEBUG = true;
    private static final float DEFAULT_CRESCENT_RATIO = -0.2F;
    private static final float DEFAULT_CRESCENT_OFFSET_RATIO_OF_WIDTH = 0.08F;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    /**
     * Radius.
     */
    private int mRadius;

    @DirectionMode
    private int mCrescentDirectionMode;
    private Rect mRect;

    /**
     * A rectangle for drawing an oval.
     */
    private RectF mClipRect = new RectF();
    /**
     * From 0 to 360 degrees.
     */
    private float mCrescentAngle;
    /**
     * The distance between centers of two circles.
     * And the angle has something to do with this spec.
     */
    private int ccDistance;

    /**
     * There are three circumstances:
     * <ol type="1">
     * <li>No intersection.</li>
     * <li>Partly engaged.</li>
     * <li>Overlapped.</li>
     * </ol>
     *
     * <p>
     * No intersection part, restP as its name, it equals (ccDistance - intersectionLen) / 2 when they have intersection
     * , while the radius of two circles are totally same.
     * </p>
     * <br/>
     * <p>
     * What if they don't? In this case, intersectionLen = 0, restP = mRadius.
     * </p>
     * <br/>
     * <p>How to make sure there is no intersection?</p>
     * <p>
     * It's clear that there is no intersection when the absolute value of mCrescentOffsetX or mCrescentOffsetY equals mRadius or mRadius * 3.
     * </p>
     * <br/>
     * <p>How to make sure the two circles are partly engaged?</p>
     * <p>
     * It's quite easy that they are in this state when the intersectionLen doesn't equals zero
     * and the absolute value of mCrescentOffsetX or mCrescentOffset equals mRadius or mRadius * 3.
     * </p>
     * <br/>
     * <p>How to make sure the two circles are overlapped?</p>
     * <p>
     * It's clear that the are overlapped only when the ccDistance equals zero.
     * </p>
     * What we want is intersectionLen.
     * You should give us a ratio(See {@link #mCrescentRatio}) of restP, -> intersectionLen + {@link #DEFAULT_OFFSET} will be the margins in {@link PeekGallery}.
     * This view will be a RoundImageView when the intersectionState is {@link State#NO_INTERSECTION}, or be transformed to it as users invoked.
     */
    private int intersectionLen = ERROR_CODE;

    private int restP = ERROR_CODE;

    /**
     * See {@link State}.
     */
    @IntersectionState
    private int intersectionState;

    /**
     * L: if it's negative.
     * <br/>
     * R: if it's positive.
     */
    @FloatRange(from = -1.0, to = 1.0)
    private float mCrescentRatio;

    /**
     * The ratio of width divided by height.
     * 1 indicates circle.
     * <br/>
     * width : height.
     */
    private float mOvalRatio = 1;


    private int mCrescentOffsetX;
    private int mCrescentOffsetY;
    private boolean lock1;
    private float mInnerRatio;

    private int mPreviousCrescentOffsetX;
    private int mPreviousCrescentOffsetY;
    private int mCrescentDirection;

    /**
     * True if it's used in {@link PeekGallery}, false otherwise;
     * <br/>
     * <b>IF</b> this value were to set to false, the {@link #mClipOffset} would be useless.
     */
    private boolean usedInPeekGallery;
    /**
     * <p>
     * The horizontal offset between two adjacent views.
     * And each of them is either {@link RoundImageView}(temporarily replaced by {@link de.hdodenhof.circleimageview.CircleImageView}) or {@link CrescentImageView}.
     * </p>
     * <br/>
     * <p>
     * For instance, two adjacent views seem linked when this value equals zero.
     * </p>
     * <br/>
     * This value is given by invokers. And the radius of a new circle to clip this view equals the original radius of this view plus this.
     */
    private int mClipOffset;

    public CrescentImageView(Context context) {
        this(context, null);
    }

    public CrescentImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrescentImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Initialize this view.
     *
     * @param context      The given context.
     * @param attributeSet The attribute set fetched by parsing xml file.
     */
    protected void init(Context context, AttributeSet attributeSet) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        if (attributeSet != null) {
            TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.CrescentImageView);
            mCrescentRatio = ta.getFloat(R.styleable.CrescentImageView_crescentHeightRatio, DEFAULT_CRESCENT_RATIO);
            mCrescentOffsetX = ta.getDimensionPixelSize(R.styleable.CrescentImageView_crescentOffset, 0);
            mCrescentDirection = ta.getInteger(R.styleable.CrescentImageView_crescentDirection, DIRECTION_LEFT);
            ta.recycle();
        } else {
            mCrescentRatio = DEFAULT_CRESCENT_RATIO;
            mCrescentOffsetX = 0;
            mCrescentDirectionMode = DIRECTION_MODE_COARSE;
            mCrescentDirection = DIRECTION_LEFT;
        }
    }

    /**
     * If current instance is the child of PeekGallery, {@link PeekGallery#onLayout(boolean, int, int, int, int)} is on demand to make sure
     * the correct position it lays in.
     */
    private void sInvalidate() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent instanceof PeekGallery) {
            PeekGallery peekGallery = (PeekGallery) parent;
            peekGallery.requestLayout();
        } else {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMinimumMeasuredDimen();
    }

    @Override
    protected int measureWidth(int widthMeasureSpec) {
        if (mRect != null) {
            // Exactly mode
            if (DEBUG) {
                Log.d(TAG, "width: " + mRect.width());
            }
            return mRect.width() + getPaddingLeft() + getPaddingRight();
        } else {
            return super.measureWidth(widthMeasureSpec);
        }
    }

    @Override
    protected int measureHeight(int heightMeasureSpec) {
        if (mRect != null) {
            // Exactly mode
            if (DEBUG) {
                Log.d(TAG, "height: " + mRect.height());
            }
            return mRect.height() + getPaddingTop() + getPaddingBottom();
        } else {
            return super.measureHeight(heightMeasureSpec);
        }
    }

    public void setSize(Rect rect) {
        this.mRect = rect;
        requestLayout();
        //this.mRect = null;
    }

    public void setSizeRatio(float ratio) {
        if (mRect == null) {
            this.mRect = new Rect();
        } else {
            rstRect();
        }
        mRect.left = getLeft();
        mRect.top = getTop();
        mRect.right = (int) (getRight() * ratio);
        mRect.bottom = (int) (getBottom() * ratio);
        requestLayout();
        //this.mRect = null;
    }

    private void rstRect() {
        if (mRect != null) {
            mRect.setEmpty();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mRadius = mWidth / 2;
        usedInPeekGallery = getParent() instanceof PeekGallery;
        if (DEBUG) {
            Log.d(TAG, "used in PeekGallery: " + usedInPeekGallery);
        }
        if (mCrescentOffsetX == 0) {
            mCrescentOffsetX = (int) (DEFAULT_CRESCENT_OFFSET_RATIO_OF_WIDTH * mWidth);
        }
        if (mCrescentOffsetY == 0) {
            mCrescentOffsetY = (int) (DEFAULT_CRESCENT_OFFSET_RATIO_OF_WIDTH * mHeight);
        }

        float wRatio = w * 1f / oldw;
        float hRatio = h * 1f / oldh;
        if (DEBUG) {
            Log.d(TAG, "wRatio: " + wRatio + ", hRatio: " + hRatio);
        }
        mInnerRatio = wRatio == hRatio ? wRatio : (wRatio + hRatio) / 2;

        if (!lock1) {
            mPreviousCrescentOffsetX = mCrescentOffsetX;
            mPreviousCrescentOffsetY = mCrescentOffsetY;
            lock1 = true;
        }
        ensureOffsets();
        resolveDirection();
        mClipRect.left = getLeft();
        mClipRect.top = getTop();
        mClipRect.right = getRight();
        mClipRect.bottom = getBottom();
        mClipRect.offset(mCrescentOffsetX, mCrescentOffsetY);
        computeIntersectionLen();
        Log.e(TAG, "onSizeChanged.");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (DEBUG) {
            Log.d(TAG, "onDraw.");
        }
        if (drawable == null) {
            return;
        }
        Bitmap sourceBitmap = getSourceBitmap(getRoundBitmap(innerDrawableToBitmap(drawable), mPaint));
        mPaint.reset();
        if (sourceBitmap != null) {
            canvas.drawBitmap(sourceBitmap, 0, 0, mPaint);
        } else {
            super.onDraw(canvas);
        }
    }

    private void resolveDirection() {
        if (mCrescentDirectionMode == DIRECTION_MODE_COARSE) {
            switch (mCrescentDirection) {
                case DIRECTION_LEFT:
                    mCrescentOffsetY = (int) (mWidth / 2f);
                    if (mCrescentOffsetX > 0) {
                        mCrescentOffsetX = -mCrescentOffsetX;
                    }
                    break;
                case DIRECTION_TOP:
                    mCrescentOffsetX = (int) (mWidth / 2f);
                    if (mCrescentOffsetY > 0) {
                        mCrescentOffsetY = -mCrescentOffsetY;
                    }
                    break;
                case DIRECTION_RIGHT:
                    mCrescentOffsetY = (int) (mWidth / 2f);
                    mCrescentOffsetX = mWidth + mPreviousCrescentOffsetX;
                    break;
                case DIRECTION_BOTTOM:
                    mCrescentOffsetX = (int) (mWidth / 2f);
                    mCrescentOffsetY = mHeight + mPreviousCrescentOffsetY;
                    break;
            }
        } else {
            // to do...
        }
    }

    private void ensureOffsets() {
        if (mInnerRatio != Double.POSITIVE_INFINITY) {
            mCrescentOffsetX *= mInnerRatio;
            mCrescentOffsetY *= mInnerRatio;
            mPreviousCrescentOffsetX *= mInnerRatio;
            mPreviousCrescentOffsetY *= mInnerRatio;
        }
    }

    public float getCrescentRatio() {
        return mCrescentRatio;
    }

    public void setCrescentRatio(@FloatRange(from = -1, to = 1) float crescentRatio) {
        this.mCrescentRatio = crescentRatio;
        invalidate();
    }

    private boolean checkCCDistance() {
        return ccDistance != 0;
    }

    private void computeIntersectionLen() {
        ccDistance = MathHelper.computeDistance(new Point(mCrescentOffsetX, mCrescentOffsetY), new Point(mWidth / 2, mHeight / 2));
        if (DEBUG) {
            Log.d(TAG, "ccDistance: " + ccDistance);
        }
        if (mCrescentDirection == DIRECTION_LEFT || mCrescentDirection == DIRECTION_RIGHT) {
            if (Math.abs(mCrescentOffsetX) == mRadius || Math.abs(mCrescentOffsetX) == mRadius * 3) {
                intersectionLen = 0;
                intersectionState = State.NO_INTERSECTION;
                return;
            }
        } else if (mCrescentDirection == DIRECTION_TOP || mCrescentDirection == DIRECTION_BOTTOM) {
            if (Math.abs(mCrescentOffsetY) == mRadius || Math.abs(mCrescentOffsetY) == mRadius * 3) {
                intersectionLen = 0;
                intersectionState = State.NO_INTERSECTION;
                return;
            }
        }
        if (ccDistance == 0) {
            intersectionLen = mWidth;
            intersectionState = State.OVERLAPPED;
            return;
        }
        intersectionState = State.PARTLY_ENGAGED;
        intersectionLen = mCrescentRatio < 0 ? ccDistance - (restP = (int) (Math.abs(mCrescentRatio) * mRadius)) * 2
                : mWidth - (restP = (int) (mCrescentRatio * mRadius - mRadius));
        if (DEBUG) {
            Log.d(TAG, "intersectionLen: " + intersectionLen);
        }
    }

    private void computeClipRect() {

    }

    public int getRestP() {
        return restP;
    }

    public int getIntersectionLen() {
        return intersectionLen;
    }

    @IntersectionState
    public int getIntersectionState() {
        return intersectionState;
    }

    @CrescentDirection
    public int getCrescentDirection() {
        return mCrescentDirection;
    }

    public void setCrescentDirection(@CrescentDirection int direction) {
        this.mCrescentDirection = direction;
        invalidate();
    }

    @DirectionMode
    public int getDirectionMode() {
        return mCrescentDirectionMode;
    }

    public void setDirectionMode(@DirectionMode int directionMode) {
        this.mCrescentDirectionMode = directionMode;
        invalidate();
    }

    public boolean isUsedInPeekGallery() {
        return usedInPeekGallery;
    }

    public int getClipOffset() {
        return mClipOffset;
    }

    public void setClipOffset(int mClipOffset) {
        this.mClipOffset = mClipOffset;
        sInvalidate();
    }

    private Bitmap getOutlineBitmap() {
        // TODO: 2019/5/22 当前View描边，用于提示选中的状态，NDK实现(微分算子Sobel.)
        return null;
    }

    private Bitmap getSourceBitmap(Bitmap source) {
        if (source == null) {
            Log.e(TAG, "Method getSourceBitmap has been invoked. Null given bitmap.");
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(source);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, 0, 0, mPaint);
        if (DEBUG) {
            Log.d(TAG, "crescentOffsetX: " + mCrescentOffsetX + ", crescentOffsetY: " + mCrescentOffsetY);
        }
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawCircle(mCrescentOffsetX, mCrescentOffsetY, mRadius, mPaint);
        // drawOval
        //canvas.drawOval(mClipRect, mPaint);
        return bitmap;
    }

    @SuppressWarnings("WeakerAccess")
    @IntDef({DIRECTION_LEFT, DIRECTION_TOP, DIRECTION_RIGHT, DIRECTION_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    public @interface CrescentDirection {
    }

    @SuppressWarnings("WeakerAccess")
    @IntDef({DIRECTION_MODE_COARSE, DIRECTION_MODE_FINE})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    public @interface DirectionMode {
    }

    @SuppressWarnings("WeakerAccess")
    @IntDef({State.NO_INTERSECTION, State.PARTLY_ENGAGED, State.OVERLAPPED})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    public @interface IntersectionState {
    }

    /**
     * A configuration class for describing the state of the current CrescentImageView.
     */
    static final class State {
        /**
         * There is no intersection between two circles. <s>The view will contains nothing but void.</s>
         * It indicates that this view is equivalent to {@link RoundImageView}.
         */
        static final int NO_INTERSECTION = 0;
        /**
         * The two circles have intersection, but they're not overlapped.
         */
        static final int PARTLY_ENGAGED = 1;
        /**
         * It's strongly discouraged because of no meaning. It indicates that this view is equivalent to {@link RoundImageView}.
         */
        static final int OVERLAPPED = 2;

    }

    // TODO: 2019/5/24 1. 纵向offset 2. 任意角度旋转 3. use oval clipping 4. 精度不够

}
