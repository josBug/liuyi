package com.example.demo.stuct;

import lombok.Data;

import java.util.List;

@Data
public class ExpressRequest {

    private List<Long> ids;

    private String expressCode;
}
