package com.example.demo.entity;

public enum OperationPlatform {

    APP(0),
    PC(1);

    private int code;

    OperationPlatform(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
