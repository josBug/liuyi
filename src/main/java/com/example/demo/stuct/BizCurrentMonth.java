package com.example.demo.stuct;

import lombok.Data;

@Data
public class BizCurrentMonth {

    private Integer amount = 0;

    private Double tips = 0.0;

    private Double oldPrice = 0.0;

    private Double countPrice = 0.0;
}
