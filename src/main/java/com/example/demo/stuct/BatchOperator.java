package com.example.demo.stuct;

import lombok.Data;

import java.util.List;

@Data
public class BatchOperator {

    private List<Long> ids;

    private BatchType batchType;

    private int value;
}
