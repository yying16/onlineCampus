package com.campus.common.pojo;

/**
 * CRUD数据封装类
 */
public class ServiceData<T> {
    public final static int INSERT = 102;
    public final static int UPDATE = 204;
    public final static int DELETE = 306;
    public final static int SELECT = 408;
    private int method;
    private Object data;
    private String id;
    private String type;

    ServiceData() {

    }

    public ServiceData(int method, Object data,String type) {
        this.method = method;
        this.data = data;
        this.type = type;
    }

    public ServiceData(int method, Object data, String type, String id) {
        this.method = method;
        this.data = data;
        this.id = id;
        this.type = type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMethod() {
        return method;
    }

    public Object getData() {
        return data;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
