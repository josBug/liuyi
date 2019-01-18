package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.PGoodsRecordHibernateDao;
import com.example.demo.entity.OperationPlatform;
import com.example.demo.entity.OperationType;
import com.example.demo.fragment.OperationFragment;
import com.example.demo.map.GoodsRecordMapperDao;
import com.example.demo.mode.GoodsRecord;
import com.example.demo.stuct.StatictisModel;
import com.example.demo.stuct.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.management.ServiceNotFoundException;
import java.math.BigDecimal;
import java.util.*;

@RestController
public class OperationController {


    @Autowired
    private PGoodsRecordHibernateDao pGoodsRecordHibernateDao;

    @Autowired
    private OperationFragment operationFragment;

    @Autowired
    private GoodsRecordMapperDao goodsRecordMapperDao;

    private static final ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/submit/json",produces = "application/json;charset=UTF-8",consumes = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo submitJson(@RequestBody LYopRequest lYopRequest) {
        ResponseDemo responseDemo = new ResponseDemo();
        System.out.println(JSONObject.toJSON(lYopRequest));
        Result result = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(Result.class));
        if (result != null) {
            result.getMessages().stream().forEach(message -> {
                if (!checkParam(message)) {
                    return;
                }
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
                goodsRecord.setIsPay(message.getIsPay());
                goodsRecord.setExpressCode("");
                goodsRecord.setSource(message.getSource());
                goodsRecord.setUserId(lYopRequest.getUserId());
                pGoodsRecordHibernateDao.save(goodsRecord);

            });
            responseDemo.setCode(200);
            responseDemo.setResult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setResult("failed");
        return responseDemo;
    }

    private Boolean checkParam(Message message) {
        if (message.getAmount() < 0 || message.getPrice() < 0 || message.getTip() < 0
                || message.getCode() == null || message.getColor() == null || message.getGoods() == null
                || message.getGoods().isEmpty() || message.getNames() == null || message.getNames().isEmpty()
                || message.getSource() == null) {
            return false;
        }
        if (message.getNames().indexOf("#") != -1 || message.getGoods().indexOf("#") != -1 || message.getSource().indexOf("#") != -1
                || message.getCode().indexOf("#") != -1 || message.getRemark().indexOf("#") != -1) {
            return false;
        }
        return true;
    }

    private Boolean checkParam(GoodsRecord message) {
        if (message.getAmount() < 0 || message.getOldPrice() < 0 || message.getTip() < 0
                || message.getCode() == null || message.getColor() == null || message.getGoodsName() == null
                || message.getGoodsName().isEmpty() || message.getName() == null || message.getName().isEmpty()
                || message.getSource() == null) {
            return false;
        }
        if (message.getName().indexOf("#") != -1 || message.getGoodsName().indexOf("#") != -1 || message.getSource().indexOf("#") != -1
                || message.getCode().indexOf("#") != -1 || message.getRemark().indexOf("#") != -1) {
            return false;
        }
        return true;
    }
    @RequestMapping(value = "/search",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<GoodsRecord> search(@RequestBody LYopRequest lYopRequest) throws Exception {
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            Map<String, Object> param = new HashMap<>();
            String sql = operationFragment.constructSql(searchParam, param, OperationPlatform.PC, OperationType.SELECT, lYopRequest.getUserId());

            System.out.println(sql);
            System.out.println(JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.query(sql, param, searchParam.getOffset(), searchParam.getLimit());
        }
        return Collections.EMPTY_LIST;
    }

    @RequestMapping(value = "/search/app",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<GoodsRecord> searchApp(@RequestBody LYopRequest lYopRequest) throws Exception {
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            Map<String, Object> param = new HashMap<>();
            String sql = operationFragment.constructSql(searchParam, param, OperationPlatform.APP, OperationType.SELECT, lYopRequest.getUserId());
            System.out.println(sql);
            System.out.println(JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.queryByLastId(sql, param, searchParam.getLimit());
        }
        return Collections.EMPTY_LIST;
    }

    @RequestMapping(value = "/search/count",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public int searchCount(@RequestBody LYopRequest lYopRequest) throws Exception {
        System.out.println("search++++++++++++++++++++++++");
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            Map<String, Object> param = new HashMap<>();
            String sql = operationFragment.constructSql(searchParam, param, OperationPlatform.PC, OperationType.COUNT, lYopRequest.getUserId());
            System.out.println("count+++++++++" + sql);
            System.out.println("count+++++++++" + JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.count(sql, param, searchParam.getOffset(), searchParam.getLimit());
        }
        return 0;
    }

    @RequestMapping(value = "/search/statictis",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public StatictisModel searchStatictis(@RequestBody LYopRequest lYopRequest) throws Exception {
        System.out.println("search++++++++++++++++++++++++");
        SearchParam searchParam = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(SearchParam.class));
        if (searchParam != null) {
            Map<String, Object> param = new HashMap<>();
            String sql = operationFragment.constructSql(searchParam, param, OperationPlatform.PC, OperationType.SELECT_SUM, lYopRequest.getUserId());
            System.out.println("count+++++++++" + sql);
            System.out.println("count+++++++++" + JSONObject.toJSON(param));
            return pGoodsRecordHibernateDao.statictisGoods(sql, param);
        }
        return new StatictisModel();
    }

    @RequestMapping(value = "/get/mapper",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public GoodsRecord getMapperGoods(@RequestBody LYopRequest lYopRequest) {
        Integer id = (Integer) lYopRequest.getObject();
        GoodsRecord goodsRecord = new GoodsRecord();
        goodsRecord.setId(Long.valueOf(id));
        goodsRecord.setUserId(lYopRequest.getUserId());
        return goodsRecordMapperDao.getByIdByMapperSelectProviderV2(goodsRecord);
    }

    @RequestMapping(value = "/update",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo update(@RequestBody LYopRequest lYopRequest) throws Exception {
        OperationRequest operationRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(OperationRequest.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (operationRequest != null) {
            GoodsRecord goodsRecord = operationRequest.getGoodsRecords().get(0);
            goodsRecord.setCountPrice(new BigDecimal((goodsRecord.getOldPrice() + goodsRecord.getTip()) * goodsRecord.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            GoodsRecord oldGoodsRecord = pGoodsRecordHibernateDao.getById(goodsRecord.getId(), lYopRequest.getUserId());
            if (oldGoodsRecord == null) {
                throw new ServiceNotFoundException("获取信息失败");
            }
            if (!checkParam(goodsRecord)) {
                responseDemo.setCode(500);
                responseDemo.setResult("failed");
                return responseDemo;
            }
            oldGoodsRecord.setRemark(goodsRecord.getRemark());
            oldGoodsRecord.setTip(goodsRecord.getTip());
            oldGoodsRecord.setOldPrice(goodsRecord.getOldPrice());
            oldGoodsRecord.setName(goodsRecord.getName());
            oldGoodsRecord.setCountPrice(goodsRecord.getCountPrice());
            oldGoodsRecord.setColor(goodsRecord.getColor());
            oldGoodsRecord.setCode(goodsRecord.getCode());
            oldGoodsRecord.setGoodsName(goodsRecord.getGoodsName());
            oldGoodsRecord.setAmount(goodsRecord.getAmount());
            pGoodsRecordHibernateDao.update(oldGoodsRecord);
            responseDemo.setCode(200);
            responseDemo.setResult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setResult("failed");
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
            responseDemo.setResult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setResult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/update/batch/operator",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo updateBatchOperator(@RequestBody LYopRequest lYopRequest) {
        BatchOperator batchOperator = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(BatchOperator.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (batchOperator != null) {
            String sql = operationFragment.constructBatchSql(batchOperator.getBatchType());
            pGoodsRecordHibernateDao.updateBatch(batchOperator.getIds(), batchOperator.getValue(), sql, lYopRequest.getUserId());
            responseDemo.setCode(200);
            responseDemo.setResult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setResult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/update/batch/express",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo updateBatchExpress(@RequestBody LYopRequest lYopRequest) {
        ExpressRequest expressRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(ExpressRequest.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (!operationFragment.checkParamValid(expressRequest.getExpressCode())) {
            responseDemo.setCode(500);
            responseDemo.setResult("failed");
            return responseDemo;
        }
        if (expressRequest != null) {
            String sql = operationFragment.constructExpress();
            pGoodsRecordHibernateDao.updateExpress(expressRequest.getIds(), expressRequest.getExpressCode(), sql, lYopRequest.getUserId());
            responseDemo.setCode(200);
            responseDemo.setResult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setResult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/delete/batch",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo delete(@RequestBody LYopRequest lYopRequest) {
        BatchOperator batchOperator = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(BatchOperator.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (batchOperator != null) {
            pGoodsRecordHibernateDao.deleteV2(batchOperator.getIds(), lYopRequest.getUserId());
            responseDemo.setCode(200);
            responseDemo.setResult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setResult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/month/statistic",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public BizCurrentMonth currentMonthStatistic(@RequestBody LYopRequest lYopRequest) {
        String sql = operationFragment.constructMonthStatistic();
        return pGoodsRecordHibernateDao.getMonthStatistic(sql, lYopRequest.getUserId());
    }
}
