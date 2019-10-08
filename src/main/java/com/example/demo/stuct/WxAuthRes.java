package com.example.demo.stuct;

import lombok.Data;

@Data
public class WxAuthRes {

    private String openid;

    private String session_key;

    private String unionid;

    private int errcode;

    private String errmsg;
}
