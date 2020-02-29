package com.example.demo.controller;

import com.example.demo.dao.PBuyerInfoHibernateDao;
import com.example.demo.mode.BuyerInfo;
import com.example.demo.stuct.*;
import com.example.demo.util.CharaUtil;
import com.example.demo.util.EmojiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class BuyerController {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PBuyerInfoHibernateDao pBuyerInfoHibernateDao;

    @RequestMapping(value = "/add/buyer",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public BuyerInfo addBuyer(@RequestBody LYopRequest lYopRequest) {
        BuyerRequest buyerRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(BuyerRequest.class));
        String upperCase = CharaUtil.getUpperCase(EmojiUtils.filterEmoji(buyerRequest.getName(), "*"), false);
        BuyerInfo buyerInfo = pBuyerInfoHibernateDao.addBuyer(EmojiUtils.filterEmoji(buyerRequest.getName(), "*"), lYopRequest.getUserId(), String.valueOf(upperCase.charAt(0)));
        return buyerInfo;
    }

    @RequestMapping(value = "/search/buyer",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<BuyerResponse> searchBuyer(@RequestBody LYopRequest lYopRequest) {
        KeyWordRequest keyWordRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(KeyWordRequest.class));
        List<BuyerInfo> buyerInfos = pBuyerInfoHibernateDao.searchBuyer(EmojiUtils.filterEmoji(keyWordRequest.getKeyword(), "*"), lYopRequest.getUserId(), keyWordRequest.getOffset(), keyWordRequest.getLimit());
        Map<String, List<BuyerInfo>> collect = buyerInfos.stream().collect(Collectors.groupingBy(BuyerInfo::getInitial));
        return collect.entrySet().stream().map(entry -> {
            BuyerResponse buyerResponse = new BuyerResponse();
            buyerResponse.setInitial(entry.getKey());
            buyerResponse.setBuyerInfos(entry.getValue());
            return buyerResponse;
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/list/buyer",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<BuyerResponse> listBuyer(@RequestBody LYopRequest lYopRequest) {
        KeyWordRequest keyWordRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(KeyWordRequest.class));
        List<BuyerInfo> buyerInfos = pBuyerInfoHibernateDao.listBuyer(keyWordRequest.getOffset(), keyWordRequest.getLimit(), lYopRequest.getUserId());
        Map<String, List<BuyerInfo>> collect = buyerInfos.stream().collect(Collectors.groupingBy(BuyerInfo::getInitial));
        return collect.entrySet().stream().map(entry -> {
            BuyerResponse buyerResponse = new BuyerResponse();
            buyerResponse.setInitial(entry.getKey());
            buyerResponse.setBuyerInfos(entry.getValue());
            return buyerResponse;
        }).collect(Collectors.toList());
    }
}
