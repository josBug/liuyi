package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.StorageClass;
import com.example.demo.dao.PGoodsRecordHibernateDao;
import com.example.demo.entity.OperationPlatform;
import com.example.demo.entity.OperationType;
import com.example.demo.fragment.OperationFragment;
import com.example.demo.mode.GoodsRecord;
import com.example.demo.stuct.StatictisModel;
import com.example.demo.stuct.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.ServiceNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
                if (!checkParam(message)) {
                    return;
                }
                GoodsRecord goodsRecord = new GoodsRecord();
                goodsRecord.setAmount(message.getAmount());
                goodsRecord.setGoodsName(message.getGoods());
                goodsRecord.setCode(message.getCode());
                goodsRecord.setColor(message.getColor());
                goodsRecord.setSellPrice(new BigDecimal(message.getSellPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setCountPrice(new BigDecimal(message.getSellPrice() * message.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setName(message.getNames());
                goodsRecord.setOldPrice(new BigDecimal(message.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setSend(message.getSend());
                if (goodsRecord.getSellPrice() < goodsRecord.getOldPrice()) {
                    return;
                }
                goodsRecord.setTip(new BigDecimal(goodsRecord.getSellPrice()).subtract(new BigDecimal(goodsRecord.getOldPrice())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setRemark(message.getRemark());
                goodsRecord.setUserName(lYopRequest.getUserName());
                goodsRecord.setIsPay(message.getIsPay());
                goodsRecord.setExpressCode("");
                goodsRecord.setSource(message.getSource());
                goodsRecord.setBrandName(message.getBrandName());
                goodsRecord.setUserId(lYopRequest.getUserId());
                goodsRecord.setImageUrl(message.getImageUrl());
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
        if (message.getAmount() < 0 || message.getOldPrice() < 0 || message.getTip() < 0 || message.getSellPrice() < 0
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

    @RequestMapping(value = "/update",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo update(@RequestBody LYopRequest lYopRequest) throws Exception {
        OperationRequest operationRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(OperationRequest.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (operationRequest != null) {
            GoodsRecord goodsRecord = operationRequest.getGoodsRecords().get(0);
            if (goodsRecord.getSellPrice() < goodsRecord.getOldPrice()) {
                responseDemo.setCode(500);
                responseDemo.setResult("failed");
                return responseDemo;
            }
            goodsRecord.setCountPrice(new BigDecimal(goodsRecord.getSellPrice() * goodsRecord.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
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
            oldGoodsRecord.setTip(new BigDecimal(goodsRecord.getSellPrice()).subtract(new BigDecimal(goodsRecord.getOldPrice())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            oldGoodsRecord.setOldPrice(new BigDecimal(goodsRecord.getOldPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            oldGoodsRecord.setName(goodsRecord.getName());
            oldGoodsRecord.setCountPrice(goodsRecord.getCountPrice());
            oldGoodsRecord.setSellPrice(new BigDecimal(goodsRecord.getSellPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            oldGoodsRecord.setColor(goodsRecord.getColor());
            oldGoodsRecord.setCode(goodsRecord.getCode());
            oldGoodsRecord.setGoodsName(goodsRecord.getGoodsName());
            oldGoodsRecord.setAmount(goodsRecord.getAmount());
            oldGoodsRecord.setBrandName(goodsRecord.getBrandName());
            oldGoodsRecord.setImageUrl(goodsRecord.getImageUrl());
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
                goodsRecord.setSellPrice(new BigDecimal(goodsRecord.getSellPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setOldPrice(new BigDecimal(goodsRecord.getOldPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setCountPrice(new BigDecimal(goodsRecord.getSellPrice() * goodsRecord.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setTip(new BigDecimal(goodsRecord.getSellPrice()).subtract(new BigDecimal(goodsRecord.getOldPrice())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                goodsRecord.setUserName(lYopRequest.getUserName());
                goodsRecord.setBrandName(goodsRecord.getBrandName());
                goodsRecord.setImageUrl(goodsRecord.getImageUrl());
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

    @RequestMapping(value = "/goods/upload",method = RequestMethod.POST)
    public String uploadGoodsFile(@RequestParam("file") MultipartFile file) throws IOException {
// Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "LTAI4FcX2npMWL38FD3N322E";
        String accessKeySecret = "cw0i6FPJKqLlDZuvvwxhn8XNzNpKYm";

// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret);

// 上传Byte数组。
        byte[] content = file.getBytes();
        // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        metadata.setObjectAcl(CannedAccessControlList.PublicRead);
        ossClient.putObject("lywss", file.getOriginalFilename(), new ByteArrayInputStream(content), metadata);
// 关闭OSSClient。
        ossClient.shutdown();
        return getUrl(file.getOriginalFilename());
    }

    private String getUrl(String fileName) {
        return String.format("https://lywss.oss-cn-beijing.aliyuncs.com/%s?x-oss-process=image/resize,w_100");
    }
}
