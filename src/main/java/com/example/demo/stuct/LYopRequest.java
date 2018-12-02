package com.example.demo.stuct;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class LYopRequest {

    private String ver;

    private String session;

    private String userName;

    private Long userId;

    private Object object;
}
