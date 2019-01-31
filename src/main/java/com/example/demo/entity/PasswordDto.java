package com.example.demo.entity;

import lombok.Data;

@Data
public class PasswordDto {

    private String oldPasswd;

    private String newPasswd;
}
