package com.jerryjin.kit.helper.network.pojo;

import android.util.ArrayMap;

import java.util.Map;

import okhttp3.Headers;

/**
 * Author: Jerry
 * Generated at: 2019-06-22 00:30
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version:
 * Description:
 */
public class HeaderParams {

    private Map<String, String> headers = new ArrayMap<>();

    public HeaderParams() {
    }

    public HeaderParams add(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HeaderParams add(KVPair<String, String> kvPair) {
        headers.put(kvPair.getKey(), kvPair.getValue());
        return this;
    }

    public Headers build() {
        return Headers.of(headers);
    }
}
