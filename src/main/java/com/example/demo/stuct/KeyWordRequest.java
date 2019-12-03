package com.example.demo.stuct;

import lombok.Data;

@Data
public class KeyWordRequest {

    private String keyword;

    private int offset;

    private int limit;

    private long id;
}
