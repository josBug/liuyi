package com.example.demo.stuct;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "book")
public class Book {

    private String author;

    private String name;

    private String pingyin;
}
