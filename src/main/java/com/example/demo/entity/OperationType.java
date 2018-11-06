package com.example.demo.entity;

public enum OperationType {

    SELECT(0),
    SELECT_SUM(1),
    COUNT(2);

    private int code;

    OperationType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
