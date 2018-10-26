package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.PGoodsRecordHibernateDao;
import com.example.demo.mode.GoodsRecord;
import com.example.demo.stuct.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
public class OperationController {


    @Autowired
    private PGoodsRecordHibernateDao pGoodsRecordHibernateDao;

    private static final ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/submit/json",produces = "application/json;charset=UTF-8",consumes = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo submitJson(@RequestBody LYopRequest lYopRequest) {
        ResponseDemo responseDemo = new ResponseDemo();
        System.out.println(JSONObject.toJSON(lYopRequest));
        Result result = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(Result.class));
        if (result != null) {
            result.getMessages().stream().forEach(message -> {
                GoodsRecord goodsRecord = new GoodsRecord();
                goodsRecord.setAmount(message.getAmount());
                goodsRecord.setGoodsName(message.getGoods());
                goodsRecord.setCode(message.getCode());
                goodsRecord.setColor(message.getColor());
                goodsRecord.setCountPrice(new BigDecimal((message.getPrice() + message.getTip()) * message.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setName(message.getNames());
                goodsRecord.setOldPrice(new BigDecimal(message.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setSend(message.getSend());
                goodsRecord.setTip(new BigDecimal(message.getTip()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setRemark(message.getRemark());
                goodsRecord.setUserName(lYopRequest.getUserName());
                goodsRecord.setIsPay(0);
                pGoodsRecordHibernateDao.save(goodsRecord);

            });
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/search",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<GoodsRecord> search(@RequestBody LYopRequest lYopRequest) {
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            String sql = "FROM GoodsRecord ";
            Map<String, Object> param = new HashMap<>();
            if (searchParam.getGoodsName() != null && !searchParam.getGoodsName().isEmpty()) {
                if (param.isEmpty()) {
                    sql += "where ";
                }
                param.put("goodsName", searchParam.getGoodsName() + "%");
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
            sql += " order by createAt desc";
            System.out.println(sql);
            System.out.println(JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.query(sql, param, searchParam.getOffset(), searchParam.getLimit());
        }
        return Collections.EMPTY_LIST;
    }

    @RequestMapping(value = "/search/count",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public int searchCount(@RequestBody LYopRequest lYopRequest) {
        System.out.println("search++++++++++++++++++++++++");
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            String sql = "SELECT COUNT(1) FROM GoodsRecord ";
            Map<String, Object> param = new HashMap<>();
            if (searchParam.getGoodsName() != null && !searchParam.getGoodsName().isEmpty()) {
                if (param.isEmpty()) {
                    sql += "where ";
                }
                param.put("goodsName", searchParam.getGoodsName() + "%");
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
            sql += " order by createAt desc";
            System.out.println("count+++++++++" + sql);
            System.out.println("count+++++++++" + JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.count(sql, param, searchParam.getOffset(), searchParam.getLimit());
        }
        return 0;
    }

    @RequestMapping(value = "/update",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo update(@RequestBody LYopRequest lYopRequest) {
        OperationRequest operationRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(OperationRequest.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (operationRequest != null) {
            GoodsRecord goodsRecord = operationRequest.getGoodsRecord();
            goodsRecord.setCountPrice(new BigDecimal((goodsRecord.getOldPrice() + goodsRecord.getTip()) * goodsRecord.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            goodsRecord.setUserName(lYopRequest.getUserName());
            pGoodsRecordHibernateDao.update(goodsRecord);
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/delete",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo delete(@RequestBody LYopRequest lYopRequest) {
        OperationRequest operationRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(OperationRequest.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (operationRequest != null) {
            GoodsRecord goodsRecord = operationRequest.getGoodsRecord();
            pGoodsRecordHibernateDao.delete(goodsRecord);
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }
}
