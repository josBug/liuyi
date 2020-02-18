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

    public String constructSql(SearchParam searchParam, Map<String, Object> param, OperationPlatform operationPlatform, OperationType operationType, Long userId) throws Exception {
        String sql = null;
        if (operationType == OperationType.COUNT) {
            sql = "SELECT COUNT(1) FROM GoodsRecord ";
        } else if (operationType == OperationType.SELECT){
            sql = "FROM GoodsRecord ";
        } else if (operationType == OperationType.SELECT_SUM){
            sql = "SELECT SUM(tip*amount) AS tips,SUM(amount) AS amounts,SUM(countPrice) AS countPrices,SUM(oldPrice*amount) AS oldPrices FROM GoodsRecord ";
        }
        checkParam(searchParam);
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
        if (searchParam.getBrandName() != null && !searchParam.getBrandName().isEmpty()) {
            if (param.isEmpty()) {
                sql += "where ";
            } else {
                sql += " and ";
            }
            param.put("brandName", "%" + searchParam.getBrandName() + "%");
            sql += "brandName like :brandName";
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
        if (userId != null) {
            if (param.isEmpty()) {
                sql += "where ";
            } else {
                sql += " and ";
            }
            param.put("userId", userId);
            sql += "userId = :userId";
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
        StringBuffer sql = new StringBuffer("UPDATE GoodsRecord SET ");
        if (batchType == BatchType.PAY) {
            sql.append("isPay = :value ");
        } else if (batchType == BatchType.SEND) {
            sql.append("send = :value ");
        }
        sql.append("WHERE id in (:ids) ");
        if (batchType == BatchType.PAY) {
            sql.append("and isPay != :value ");
        } else if (batchType == BatchType.SEND) {
            sql.append("and send != :value ");
        }
        sql.append("and userId = :userId");
        return sql.toString();
    }

    public String constructExpress() {
        StringBuffer sql = new StringBuffer("UPDATE GoodsRecord SET expressCode = :expressCode WHERE id in (:ids) and userId = :userId");
        return sql.toString();
    }

    public String constructMonthStatistic() {
        return new StringBuffer("SELECT SUM(amount) as amounts,SUM(oldPrice) as oldPrices,SUM(tip) as tips,SUM(countPrice) as countPrices FROM GoodsRecord WHERE userId = :userId AND createAt >= :startTime AND createAt <= :endTime").toString();
    }

    private void checkParam(SearchParam searchParam) throws Exception{
        if (searchParam.getGoodsName() != null && searchParam.getGoodsName().indexOf("#") != -1) {
            throw new Exception("包含非法字符");
        }

        if (searchParam.getName() != null && searchParam.getName().indexOf("#") != -1) {
            throw new Exception("包含非法字符");
        }

        if (searchParam.getSort() != null && searchParam.getSort().indexOf("#") != -1) {
            throw new Exception("包含非法字符");
        }
    }

    public Boolean checkParamValid(String... args) {
        for (String arg: args) {
            if (arg == null || arg.isEmpty() || arg.indexOf("#") != -1) {
                return false;
            }
        }
        return true;
    }
}
