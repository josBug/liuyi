package com.example.demo.stuct;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class LYopRequest {

    private String ver;

    private String session;

    private String userName;

    private Object object;
}
