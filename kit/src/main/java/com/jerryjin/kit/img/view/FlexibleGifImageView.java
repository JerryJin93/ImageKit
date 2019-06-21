package com.jerryjin.kit.img.view;

import android.content.Context;
import android.util.AttributeSet;

import com.jerryjin.kit.helper.network.FastHttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

/**
 * Author: Jerry
 * Generated at: 2019-06-22 00:03
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version:
 * Description:
 */
public class FlexibleGifImageView extends GifImageView {
    public FlexibleGifImageView(Context context) {
        super(context);
    }

    public FlexibleGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlexibleGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        FastHttp
                .getInstance()
                .setUrl("http://www.baidu.com")
                .runAsync(new FastHttp.CallbackImpl() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        super.onResponse(call, response);

                    }
                });
    }
}
