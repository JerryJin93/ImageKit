package com.jerryjin.kit.img.loader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Author: Jerry
 * Generated at: 2019-06-28 23:41
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version:
 * Description:
 */
public class GlideImageLoader extends ImageLoader {
    @Override
    public void showImage(Context context, Object img, ImageView imageView) {
        Glide.with(context)
                .load(img)
                .into(imageView);
    }
}
