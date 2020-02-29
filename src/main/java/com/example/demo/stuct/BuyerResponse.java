package com.example.demo.stuct;

import com.example.demo.mode.BuyerInfo;
import lombok.Data;

import java.util.List;

@Data
public class BuyerResponse {
    private String initial;

    private List<BuyerInfo> buyerInfos;
}
