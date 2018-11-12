package com.example.demo.stuct;

import com.example.demo.mode.GoodsRecord;
import lombok.Data;

import java.util.List;

@Data
public class OperationRequest {
    private List<GoodsRecord> goodsRecords;
}
