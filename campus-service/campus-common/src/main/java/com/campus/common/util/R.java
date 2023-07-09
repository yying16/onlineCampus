package com.campus.common.util;

import java.io.Serializable;

/**
 * 请求响应体
 */
public class R<T> implements Serializable {
    private int code;
    private T data;
    private String msg;

    public void setData(T data) {
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> R ok() {
        R<T> r = new R<T>();
        r.setCode(0);
        return r;
    }

    public static <T> R ok(T data) {
        R<T> r = new R<T>();
        r.setCode(0);
        r.setData(data);
        return r;
    }

    public static <T> R ok(T data, String msg) {
        R<T> r = new R<T>();
        r.setCode(0);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

    public static <T> R failed() {
        R<T> r = new R<T>();
        r.setCode(1);
        return r;
    }

    public static <T> R failed(T data) {
        R<T> r = new R<T>();
        r.setCode(1);
        r.setData(data);
        return r;
    }

    public static <T> R failed(T data, String msg) {
        R<T> r = new R<T>();
        r.setCode(1);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
}
