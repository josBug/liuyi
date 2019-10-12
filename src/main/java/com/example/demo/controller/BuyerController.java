package com.example.demo.controller;

import com.example.demo.dao.PBuyerInfoHibernateDao;
import com.example.demo.mode.BuyerInfo;
import com.example.demo.stuct.*;
import com.example.demo.util.EmojiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BuyerController {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PBuyerInfoHibernateDao pBuyerInfoHibernateDao;

    @RequestMapping(value = "/add/buyer",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public BuyerInfo addBuyer(@RequestBody LYopRequest lYopRequest) {
        BuyerRequest buyerRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(BuyerRequest.class));
        BuyerInfo buyerInfo = pBuyerInfoHibernateDao.addBuyer(EmojiUtils.filterEmoji(buyerRequest.getName(), "*"), lYopRequest.getUserId());
        return buyerInfo;
    }

    @RequestMapping(value = "/search/buyer",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<BuyerInfo> searchBuyer(@RequestBody LYopRequest lYopRequest) {
        KeyWordRequest keyWordRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(KeyWordRequest.class));
        List<BuyerInfo> buyerInfos = pBuyerInfoHibernateDao.searchBuyer(EmojiUtils.filterEmoji(keyWordRequest.getKeyword(), "*"), lYopRequest.getUserId(), keyWordRequest.getOffset(), keyWordRequest.getLimit());
        return buyerInfos;
    }

    @RequestMapping(value = "/list/buyer",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<BuyerInfo> listBuyer(@RequestBody LYopRequest lYopRequest) {
        KeyWordRequest keyWordRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(KeyWordRequest.class));
        List<BuyerInfo> buyerInfos = pBuyerInfoHibernateDao.listBuyer(keyWordRequest.getOffset(), keyWordRequest.getLimit(), lYopRequest.getUserId());
        return buyerInfos;
    }
}
