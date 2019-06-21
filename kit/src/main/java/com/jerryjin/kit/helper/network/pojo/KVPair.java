package com.jerryjin.kit.helper.network.pojo;

import java.util.Objects;

/**
 * Author: Jerry
 * Generated at: 2019-06-22 00:37
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version: 1.0.0
 * Description:
 */
public class KVPair<K, V> {

    private K key;
    private V value;

    public KVPair() {
    }

    public KVPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KVPair<?, ?> kvPair = (KVPair<?, ?>) o;
        return key.equals(kvPair.key) &&
                value.equals(kvPair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "KVPair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
