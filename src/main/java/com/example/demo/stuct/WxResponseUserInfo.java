package com.example.demo.stuct;

import lombok.Data;

@Data
public class WxResponseUserInfo {

    private String nickName;

    private String userName;

    private String avatarUrl;

    private String session;

    private int code;
}
