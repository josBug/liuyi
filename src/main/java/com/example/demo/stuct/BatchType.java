package com.example.demo.stuct;

public enum BatchType {

    SEND(0, "是否发货"),
    PAY(1, "是否付款");

    private int code;

    private String desc;

    BatchType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
