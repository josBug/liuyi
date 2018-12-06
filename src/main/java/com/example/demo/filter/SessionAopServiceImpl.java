package com.example.demo.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.PUserInfoHibernateDao;
import com.example.demo.mode.UserInfo;
import com.example.demo.stuct.LYopRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class SessionAopServiceImpl{

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PUserInfoHibernateDao pUserInfoHibernateDao;

    @Pointcut("within(com.example.demo.controller.OperationController)")
    public void execuStart() {
    }


    @Before("execuStart()")
    public void checkSession(JoinPoint joinPoint) throws Exception {
        System.out.println("-------------------=============");
        Object[] objects = joinPoint.getArgs();
        LYopRequest lYopRequest = mapper.convertValue(objects[0], mapper.constructType(LYopRequest.class));
        if (null == lYopRequest.getSession() || lYopRequest.getSession().isEmpty()) {
            throw new Exception("session is null");
        }
        UserInfo userInfo = pUserInfoHibernateDao.checkKsid(lYopRequest.getSession());
        if (userInfo == null || userInfo.getUserName() == null || userInfo.getUserName().isEmpty()) {
            throw new Exception("请重新登录");
        }
        lYopRequest.setUserId(userInfo.getId());
        lYopRequest.setUserName(userInfo.getUserName());
        lYopRequest.setEmail(userInfo.getEmail());
    }
}
