package com.jerryjin.kit.img.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jerryjin.kit.img.loader.GlideImageLoader;
import com.jerryjin.kit.img.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Author: Jerry
 * Generated at: 2019/5/2 13:31
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version: 1.0.0
 * Description: View group for 1 {@link CircleImageView} object and many {@link CrescentImageView} objects.
 */
public class PeekGallery extends ViewGroup {

    private static final int DEFAULT_SIZE = 100;

    private static final String TAG = "PeekGallery";
    private static final boolean DEBUG = true;
    private static final int ERROR_CODE = -1;
    private Context mContext;
    private List<ImageView> absImageViews = new ArrayList<>();
    private ImageLoader mImageLoader;
    private List<Object> mImages = new ArrayList<>();

    private PopupWindow mPeekWindow;
    private int intersectionLen = ERROR_CODE;

    private int mAvailableWidth;

    private Handler handler = new Handler(Looper.getMainLooper());

    public PeekGallery(@NonNull Context context) {
        this(context, null);
    }

    public PeekGallery(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PeekGallery(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int height;

        mAvailableWidth = widthSpecSize - getPaddingLeft() - getPaddingRight();
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        if (DEBUG) {
            Log.d(TAG, "measure children.");
        }

        int childCount = getChildCount();
        int maxLineHeight = 0;
        int childWidth = getChildWidth();
        if (childCount > 0) {
            maxLineHeight = getChildAt(0).getMeasuredHeight();
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (maxLineHeight < child.getMeasuredHeight()) {
                maxLineHeight = child.getMeasuredHeight();
            }
        }

        height = maxLineHeight + getPaddingTop() + getPaddingBottom();
        if (DEBUG) {
            Log.d(TAG, "maxLineHeight: " + maxLineHeight);
            Log.d(TAG, "height: " + height);
        }

        int newWidth = getPaddingLeft() + childWidth + (childCount - 1) * (childWidth - intersectionLen) + getPaddingRight();

        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else if ((widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED)
                && (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED)) {
            setMeasuredDimension(newWidth, height);
        } else {
            setMeasuredDimension(widthSpecSize, height);
        }
        // layout child -> trigger {@link CrescentImageView#computeIntersectionLen()}
        // layout -> sizedChange -> onSizeChanged
        performOnLayout();
    }

    /**
     * It has to be invoked after {@link ViewGroup#measureChildren(int, int)}.
     *
     * @return The suitable width of each child for PeekGallery.
     */
    private int getChildWidth() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        } else {
            return getChildAt(0).getMeasuredWidth();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        performOnLayout();
    }

    private void performOnLayout() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }

        if (DEBUG) {
            Log.d(TAG, "perform onLayout children.");
        }

        mAvailableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        final int maxRight = mAvailableWidth + getPaddingLeft();
        if (DEBUG) {
            Log.d(TAG, "measuredWidth: " + getMeasuredWidth());
            Log.d(TAG, "maxRight: " + maxRight);
        }

        int mTop = getPaddingTop();
        int mLeft;
        int maxHeight = 0;
        int overallWidth = getPaddingLeft() + getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (maxHeight == 0) {
                maxHeight = child.getMeasuredHeight();
            } else {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            mLeft = getPaddingLeft() + (i != 0 ? i * (childWidth - (intersectionLen = ((CrescentImageView) child).getIntersectionLen())) : 0);
            child.layout(mLeft, mTop, mLeft + childWidth, mTop + childHeight);
            if (DEBUG) {
                Log.d(TAG, "left: " + mLeft + ", top: " + mTop + ", right: " + (mLeft + childWidth) + ", bottom: " + (mTop + childHeight));
            }

            // use CircleImageView for now.
            if (child instanceof CircleImageView) {
                overallWidth += childWidth;
            } else if (child instanceof CrescentImageView) {
                overallWidth += childWidth - ((CrescentImageView) child).getIntersectionLen();
            }

            if (DEBUG) {
                Log.d(TAG, "overallWidth: " + overallWidth);
            }

            if (overallWidth > maxRight) {
                if (DEBUG) {
                    Log.d(TAG, "break at " + i);
                }
                // ensure the last view is intact.
                child.setVisibility(GONE);
                break;
            }
        }
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mPeekWindow = new PopupWindow(mContext);
        if (attrs != null) {
//            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PeekGallery);
//            mGap = ta.getDimensionPixelSize(R.styleable.PeekGallery_gap, 0);
//            ta.recycle();
        } else {

        }

    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;
    }

    private PeekGallery.LayoutParams generateLayoutParams(int width, int height) {
        return new PeekGallery.LayoutParams(width, height);
    }

    public void addImage(Object img, int pos, int width, int height) {
        if (mImages != null) {
            if (mImageLoader == null) {
                mImageLoader = new GlideImageLoader();
            }
            ImageView imageView;
            PeekGallery.LayoutParams params = generateLayoutParams(width, height);
            if (pos == 0) {
                imageView = new CircleImageView(mContext);
                // params1.leftMargin = pos * (100 - (intersectionLen = ((CrescentImageView) tmp).getIntersectionLen()));
            } else {
                imageView = new CrescentImageView(mContext);
            }
            imageView.setLayoutParams(params);
            mImageLoader.showImage(mContext, img, imageView);
            addView(imageView);
            absImageViews.add(imageView);
        }
    }

    public void setImages(List<Object> images) {
        setImages(images, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public void setImages(List<Object> images, int width, int height) {
        if (images == null) {
            Log.e(TAG, "Null given images.");
            return;
        }
        clear();
        for (int i = 0; i < images.size(); i++) {
            addImage(images.get(i), i, width, height);
        }
        requestLayout();
    }

    public void clear() {
        if (absImageViews.size() == 0) {
            Log.i(TAG, "No image inside, skip.");
            return;
        }
        absImageViews.clear();
        removeAllViews();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
