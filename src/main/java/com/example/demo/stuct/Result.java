package com.example.demo.stuct;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class Result {

    private List<Message> messages;
}
