package com.example.demo.entity;

public enum ErrorCodeDefine {
    OK(200, "成功"),
    FAILED(500, "失败");

    private int code;

    private String desc;


    ErrorCodeDefine(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
