package com.example.demo.fragment;

import com.example.demo.entity.OperationPlatform;
import com.example.demo.entity.OperationType;
import com.example.demo.stuct.BatchType;
import com.example.demo.stuct.SearchParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OperationFragment {

    public String constructSql(SearchParam searchParam, Map<String, Object> param, OperationPlatform operationPlatform, OperationType operationType) {
        String sql = null;
        if (operationType == OperationType.COUNT) {
            sql = "SELECT COUNT(1) FROM GoodsRecord ";
        } else if (operationType == OperationType.SELECT){
            sql = "FROM GoodsRecord ";
        } else if (operationType == OperationType.SELECT_SUM){
            sql = "SELECT SUM(tip) AS tips,SUM(amount) AS amounts,SUM(countPrice) AS countPrices,SUM(oldPrice) AS oldPrices FROM GoodsRecord ";
        }
        if (searchParam.getGoodsName() != null && !searchParam.getGoodsName().isEmpty()) {
            if (param.isEmpty()) {
                sql += "where ";
            }
            param.put("goodsName", "%" + searchParam.getGoodsName() + "%");
            sql += "goodsName like :goodsName";
        }
        if (searchParam.getName() != null && !searchParam.getName().isEmpty()) {
            if (param.isEmpty()) {
                sql += "where ";
            } else {
                sql += " and ";
            }
            param.put("name", "%" + searchParam.getName() + "%");
            sql += "name like :name";
        }
        if (searchParam.getIsPay() != -1) {
            if (param.isEmpty()) {
                sql += "where ";
            } else {
                sql += " and ";
            }
            param.put("isPay", searchParam.getIsPay());
            sql += "isPay = :isPay";
        }
        if (searchParam.getIsSend() != -1) {
            if (param.isEmpty()) {
                sql += "where ";
            } else {
                sql += " and ";
            }
            param.put("send", searchParam.getIsSend());
            sql += "send = :send";
        }
        if (searchParam.getStartTime() != null) {
            if (param.isEmpty()) {
                sql += "where ";
            } else {
                sql += " and ";
            }
            param.put("startTime", searchParam.getStartTime());
            sql += "createAt >= :startTime";
        }
        if (searchParam.getEndTime() != null) {
            if (param.isEmpty()) {
                sql += "where ";
            } else {
                sql += " and ";
            }
            param.put("endTime", searchParam.getEndTime());
            sql += "createAt <= :endTime";
        }
        if (operationPlatform == OperationPlatform.APP && operationType == OperationType.SELECT) {
            if (param.isEmpty()) {
                sql += "where ";
            } else {
                sql += " and ";
            }
            param.put("id", searchParam.getId());
            if (searchParam.getSort().equals("asc") || searchParam.getId() <= 0) {
                sql += "id > :id";
            } else {
                sql += "id < :id";
            }
        }
        sql += " order by createAt " + searchParam.getSort();
        return sql;
    }

    public String constructBatchSql(BatchType batchType) {
        String sql = "UPDATE GoodsRecord SET ";
        if (batchType == BatchType.PAY) {
            sql += "isPay = :value ";
        } else if (batchType == BatchType.SEND) {
            sql += "send = :value ";
        }
        sql += "WHERE id in (:ids)";
        return sql;
    }
}
