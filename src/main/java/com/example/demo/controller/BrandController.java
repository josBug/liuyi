package com.example.demo.controller;

import com.example.demo.dao.PBrandInfoHibernateDao;
import com.example.demo.dao.PBuyerInfoHibernateDao;
import com.example.demo.mode.BrandInfo;
import com.example.demo.mode.BuyerInfo;
import com.example.demo.stuct.BrandRequest;
import com.example.demo.stuct.BuyerRequest;
import com.example.demo.stuct.KeyWordRequest;
import com.example.demo.stuct.LYopRequest;
import com.example.demo.util.EmojiUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BrandController {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PBrandInfoHibernateDao pBrandInfoHibernateDao;

    @RequestMapping(value = "/add/brand",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public BrandInfo addBuyer(@RequestBody LYopRequest lYopRequest) {
        BrandRequest brandRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(BrandRequest.class));
        BrandInfo brandInfo = pBrandInfoHibernateDao.addBrand(EmojiUtils.filterEmoji(brandRequest.getName(), "*"), lYopRequest.getUserId(), brandRequest.getSource());
        return brandInfo;
    }

    @RequestMapping(value = "/search/brand",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<BrandInfo> searchBuyer(@RequestBody LYopRequest lYopRequest) {
        KeyWordRequest keyWordRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(KeyWordRequest.class));
        List<BrandInfo> brandInfos = pBrandInfoHibernateDao.searchBrand(EmojiUtils.filterEmoji(keyWordRequest.getKeyword(), "*"), lYopRequest.getUserId(), keyWordRequest.getOffset(), keyWordRequest.getLimit());
        return brandInfos;
    }

    @RequestMapping(value = "/list/brand",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public List<BrandInfo> listBuyer(@RequestBody LYopRequest lYopRequest) {
        KeyWordRequest keyWordRequest = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(KeyWordRequest.class));
        List<BrandInfo> brandInfos = pBrandInfoHibernateDao.listBrand(keyWordRequest.getOffset(), keyWordRequest.getLimit(), lYopRequest.getUserId());
        return brandInfos;
    }
}
