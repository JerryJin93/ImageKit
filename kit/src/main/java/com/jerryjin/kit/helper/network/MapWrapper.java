package com.jerryjin.kit.helper.network;

import android.util.Log;

import com.jerryjin.kit.helper.network.pojo.KVPair;

import java.util.List;
import java.util.Map;

/**
 * Author: Jerry
 * Generated at: 2019-06-22 00:43
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version: 1.0.0
 * Description:
 */
@SuppressWarnings("WeakerAccess")
public class MapWrapper {

    private static final String TAG = "MapWrapper";


    private MapWrapper() {
    }

    public static <K, V> void add(Map<K,V> map, KVPair<K, V> bean) {
        if (map != null) {
            map.put(bean.getKey(), bean.getValue());
        }
    }

    public static <K, V> void addAll(Map<K,V> map, List<KVPair<K, V>> beans) {
        if (beans == null) {
            Log.e(TAG, "Null list.");
            return;
        }
        for (KVPair<K, V> pair : beans) {
            add(map, pair);
        }
    }

}
