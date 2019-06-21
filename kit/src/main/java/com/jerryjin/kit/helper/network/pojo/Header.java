package com.jerryjin.kit.helper.network.pojo;

/**
 * Author: Jerry
 * Generated at: 2019-06-22 00:40
 * GitHub: https://github.com/JerryJin93
 * Blog:
 * WeChat: enGrave93
 * Version:
 * Description:
 */
public class Header {
    private String name;
    private String value;

    public Header() {
    }

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Header(KVPair<String, String> p) {
        this.name = p.getKey();
        this.value = p.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
