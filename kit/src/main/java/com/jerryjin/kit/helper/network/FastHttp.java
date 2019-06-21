package com.jerryjin.kit.helper.network;

import com.jerryjin.kit.helper.network.pojo.KVPair;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Author: Jerry
 * Generated at: 2019-06-22 00:04
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version:
 * Description:
 */
public class FastHttp {

    private OkHttpClient client;
    private FastHttpOptions httpOptions;

    private FastHttp() {
    }

    public static FastHttp getInstance() {
        return Holder.instance;
    }

    private void ensureHttpOptions() {
        if (httpOptions == null) {
            httpOptions = new FastHttpOptions();
        }
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    private Request buildRequest() {
        Request request = new Request.Builder()
                .addHeader()
                .build();
        return request;
    }

    public FastHttp setHttpOptions(FastHttpOptions fastHttpOptions) {
        this.httpOptions = fastHttpOptions;
        return Holder.instance;
    }

    public FastHttp setUrl(String url) {
        ensureHttpOptions();
        this.httpOptions.setUrlStr(url);
        return Holder.instance;
    }

    public void runAsync(CallbackImpl callback) {


    }

    public void runSync() {

    }


    private static class Holder {
        private static final FastHttp instance = new FastHttp();
    }
}
