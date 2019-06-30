package com.jerryjin.kit.img.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import com.jerryjin.kit.R;
import com.jerryjin.kit.network.FastHttp;
import com.jerryjin.kit.network.interfaces.CallbackImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Author: Jerry
 * Generated at: 2019-06-22 00:03
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version: 1.0.0
 * Description: A kind of {@link android.widget.ImageView} for displaying gif image.
 */
public class GifImageView extends pl.droidsonroids.gif.GifImageView {

    private static final String TAG = "FlexibleGifImageView";
    private static final boolean DEBUG = false;

    private static final int RECEIVE_IMG_FAILURE = 0;
    private static final int RECEIVE_IMG_FAILURE_EMPTY_BODY = 2;
    private static final int RECEIVE_IMG_SUCCESSFULLY = 1;

    private static final int EMPTY_GIF = -1;
    private static final float DEFAULT_GIF_SPEED = 1.0f;

    private float mGifSpeed;
    private InnerHandler handler;

    public GifImageView(Context context) {
        this(context, null);
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        handler = new InnerHandler(Looper.getMainLooper(), this);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GifImageView);
            int resId = ta.getResourceId(R.styleable.GifImageView_gifSrc, EMPTY_GIF);
            mGifSpeed = ta.getFloat(R.styleable.GifImageView_gifSpped, DEFAULT_GIF_SPEED);
            if (resId != EMPTY_GIF) {
                Drawable drawable = getResources().getDrawable(resId);
                if (drawable instanceof GifDrawable) {
                    GifDrawable mDrawable = (GifDrawable) drawable;
                    mDrawable.setSpeed(mGifSpeed);
                    setImageDrawable(mDrawable);
                }
            }
            ta.recycle();
        } else {
            mGifSpeed = DEFAULT_GIF_SPEED;
        }
    }

    private boolean isShowingGif() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            Log.i(TAG, "Haven't set image resource yet.");
            return false;
        }
        boolean isShowingGif = drawable instanceof GifDrawable;
        if (DEBUG) {
            Log.d(TAG, "I'm showing gif image.");
        }
        return isShowingGif;
    }

    public boolean canPause() {
        if (isShowingGif()) {
            boolean canBePaused = ((GifDrawable) getDrawable()).canPause();
            if (DEBUG) {
                Log.d(TAG, "I CAN be paused.");
            }
            return canBePaused;
        } else {
            if (DEBUG) {
                Log.d(TAG, "I CAN'T be paused.");
            }
            return false;
        }
    }

    public void pause() {
        if (canPause()) {
            ((GifDrawable) getDrawable()).pause();
        }
    }

    public void start() {
        if (isShowingGif()) {
            ((GifDrawable) getDrawable()).start();
        }
    }

    public void seekToFrame(int frameIndex) {
        if (isShowingGif()) {
            ((GifDrawable) getDrawable()).seekToFrame(frameIndex);
        }
    }

    public void stop() {
        if (isShowingGif()) {
            ((GifDrawable) getDrawable()).stop();
        }
    }

    public int getLoopCount() {
        if (isShowingGif()) {
            return ((GifDrawable) getDrawable()).getLoopCount();
        } else {
            return 0;
        }
    }

    public boolean isPlaying() {
        if (isShowingGif()) {
            boolean isPlaying = ((GifDrawable) getDrawable()).isPlaying();
            if (DEBUG) {
                Log.d(TAG, "I am being played.");
            }
            return isPlaying;
        } else {
            if (DEBUG) {
                Log.d(TAG, "I am not being played.");
            }
            return false;
        }
    }

    public void setGifResource(byte[] resource) {
        try {
            GifDrawable drawable = new GifDrawable(resource);
            setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void setGifResource(InputStream resource) {
        try {
            GifDrawable drawable = new GifDrawable(resource);
            setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void setGifResource(Resources resources, int resId) {
        try {
            GifDrawable drawable = new GifDrawable(resources, resId);
            setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void setGifFile(File file) {
        try {
            GifDrawable drawable = new GifDrawable(file);
            setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void setGifFile(String filePath) {
        if (DEBUG) {
            Log.d(TAG, "gif path: " + filePath);
        }
        try {
            GifDrawable drawable = new GifDrawable(filePath);
            setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void setGifUrl(String url) {
        if (DEBUG) {
            Log.d(TAG, "gif url: " + url);
        }
        FastHttp
                .getInstance()
                .setUrl(url)
                .executeAsync(new CallbackImpl() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        if (DEBUG) {
                            Request request = call.request();
                            Log.d(TAG, "request string:\n" + request.toString());
                        }
                        Message message = handler.obtainMessage(RECEIVE_IMG_FAILURE);
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        super.onResponse(call, response);
                        if (DEBUG) {
                            Request request = call.request();
                            Log.d(TAG, "request string:\n" + request.toString());
                        }
                        ResponseBody body = response.body();
                        Message message = Message.obtain();
                        if (body != null) {
                            message.what = RECEIVE_IMG_SUCCESSFULLY;
                            message.obj = body.bytes();
                        } else {
                            message.what = RECEIVE_IMG_FAILURE_EMPTY_BODY;
                        }
                        handler.sendMessage(message);
                    }
                });
    }

    /**
     * Set speed factor of the {@link GifDrawable} in this view.
     * @param factor Speed factor, for example, 1.0f means normal speed, 0.5f means half speed and 2.0f means double speed, etc.
     */
    public void setSpeed(float factor) {
        if (isShowingGif()) {
            mGifSpeed = factor;
            ((GifDrawable) getDrawable()).setSpeed(factor);
        }
    }

    private static class InnerHandler extends Handler {

        private GifImageView instance;

        InnerHandler(Looper looper, GifImageView instance) {
            super(looper);
            this.instance = instance;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_IMG_FAILURE:
                    Log.e(TAG, "Network failure.");
                    break;
                case RECEIVE_IMG_FAILURE_EMPTY_BODY:
                    Log.e(TAG, "Empty response body.");
                    break;
                case RECEIVE_IMG_SUCCESSFULLY:
                    byte[] imgBytes = (byte[]) msg.obj;
                    try {
                        GifDrawable drawable = new GifDrawable(imgBytes);
                        instance.setImageDrawable(drawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "#InnerHandler" + e.getMessage());
                    }
                    break;
            }
        }
    }
}
