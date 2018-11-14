package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.PGoodsRecordHibernateDao;
import com.example.demo.entity.OperationPlatform;
import com.example.demo.entity.OperationType;
import com.example.demo.fragment.OperationFragment;
import com.example.demo.mode.GoodsRecord;
import com.example.demo.mode.StatictisModel;
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

    @Autowired
    private OperationFragment operationFragment;

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
            Map<String, Object> param = new HashMap<>();
            String sql = operationFragment.constructSql(searchParam, param, OperationPlatform.PC, OperationType.SELECT);

            System.out.println(sql);
            System.out.println(JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.query(sql, param, searchParam.getOffset(), searchParam.getLimit());
        }
        return Collections.EMPTY_LIST;
    }

    @RequestMapping(value = "/search/app",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<GoodsRecord> searchApp(@RequestBody LYopRequest lYopRequest) {
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            Map<String, Object> param = new HashMap<>();
            String sql = operationFragment.constructSql(searchParam, param, OperationPlatform.APP, OperationType.SELECT);
            System.out.println(sql);
            System.out.println(JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.queryByLastId(sql, param, searchParam.getLimit());
        }
        return Collections.EMPTY_LIST;
    }

    @RequestMapping(value = "/search/count",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public int searchCount(@RequestBody LYopRequest lYopRequest) {
        System.out.println("search++++++++++++++++++++++++");
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            Map<String, Object> param = new HashMap<>();
            String sql = operationFragment.constructSql(searchParam, param, OperationPlatform.PC, OperationType.COUNT);
            System.out.println("count+++++++++" + sql);
            System.out.println("count+++++++++" + JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.count(sql, param, searchParam.getOffset(), searchParam.getLimit());
        }
        return 0;
    }

    @RequestMapping(value = "/search/statictis",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public StatictisModel searchStatictis(@RequestBody LYopRequest lYopRequest) {
        System.out.println("search++++++++++++++++++++++++");
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            Map<String, Object> param = new HashMap<>();
            String sql = operationFragment.constructSql(searchParam, param, OperationPlatform.PC, OperationType.SELECT_SUM);
            System.out.println("count+++++++++" + sql);
            System.out.println("count+++++++++" + JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.statictisGoods(sql, param);
        }
        return new StatictisModel();
    }

    @RequestMapping(value = "/update",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo update(@RequestBody LYopRequest lYopRequest) {
        OperationRequest operationRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(OperationRequest.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (operationRequest != null) {
            GoodsRecord goodsRecord = operationRequest.getGoodsRecords().get(0);
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


    @RequestMapping(value = "/update/batch",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo updateBatch(@RequestBody LYopRequest lYopRequest) {
        OperationRequest operationRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(OperationRequest.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (operationRequest != null) {
            List<GoodsRecord> goodsRecords = operationRequest.getGoodsRecords();
            goodsRecords.stream().forEach(goodsRecord -> {
                goodsRecord.setCountPrice(new BigDecimal((goodsRecord.getOldPrice() + goodsRecord.getTip()) * goodsRecord.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setUserName(lYopRequest.getUserName());
                pGoodsRecordHibernateDao.update(goodsRecord);
            });
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/update/batch/operator",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo updateBatchOperator(@RequestBody LYopRequest lYopRequest) {
        BatchOperator batchOperator = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(BatchOperator.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (batchOperator != null) {
            String sql = operationFragment.constructBatchSql(batchOperator.getBatchType());
            pGoodsRecordHibernateDao.updateBatch(batchOperator.getIds(), batchOperator.getValue(), sql);
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/update/batch/express",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo updateBatchExpress(@RequestBody LYopRequest lYopRequest) {
        ExpressRequest expressRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(ExpressRequest.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (expressRequest != null) {
            String sql = operationFragment.constructExpress();
            pGoodsRecordHibernateDao.updateExpress(expressRequest.getIds(), expressRequest.getExpressCode(), sql);
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/delete/batch",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo delete(@RequestBody LYopRequest lYopRequest) {
        BatchOperator batchOperator = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(BatchOperator.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (batchOperator != null) {
            pGoodsRecordHibernateDao.deleteV2(batchOperator.getIds());
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }
}
