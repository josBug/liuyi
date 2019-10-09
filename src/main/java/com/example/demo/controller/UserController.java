package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.constant.WxConstant;
import com.example.demo.dao.PUserInfoHibernateDao;
import com.example.demo.entity.PasswordDto;
import com.example.demo.fragment.OperationFragment;
import com.example.demo.mode.UserInfo;
import com.example.demo.stuct.*;
import com.example.demo.util.PassWordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@RestController
public class UserController {

    @Autowired
    private PUserInfoHibernateDao pUserInfoHibernateDao;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OperationFragment operationFragment;

    @RequestMapping(value = "/",produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
    public ModelAndView sign() {
        ModelAndView modelAndView = new ModelAndView("index");
        return modelAndView;
    }

    @RequestMapping(value = "/registry",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo registry(@RequestBody LYopRequest lYopRequest) {
        CheckUserInfo checkUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(CheckUserInfo.class));

        if (checkUserInfo != null) {
            if (checkUserInfo.getPasswd() == null
                    || checkUserInfo.getPasswd().isEmpty()) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(500);
                responseDemo.setResult("failed");
                return responseDemo;
            }
            if (!operationFragment.checkParamValid(checkUserInfo.getUserName(), checkUserInfo.getEmail())) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(1000);
                responseDemo.setResult("failed");
                return responseDemo;
            }

            if (!PassWordUtil.checkPasswd(checkUserInfo.getPasswd())) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(1000);
                responseDemo.setResult("failed");
                return responseDemo;
            }
            boolean success = pUserInfoHibernateDao.registryUser(checkUserInfo.getUserName(), checkUserInfo.getPasswd(), checkUserInfo.getEmail());
            if (!success) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(500);
                responseDemo.setResult("failed");
                return responseDemo;
            }
            ResponseDemo responseDemo = new ResponseDemo();
            responseDemo.setCode(200);
            responseDemo.setResult("success");
            return responseDemo;
        }
        ResponseDemo responseDemo = new ResponseDemo();
        responseDemo.setCode(500);
        responseDemo.setResult("");
        return responseDemo;
    }

    @RequestMapping(value = "/check/user",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo checkUser(@RequestBody LYopRequest lYopRequest) {
        CheckUserInfo checkUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(CheckUserInfo.class));

        if (checkUserInfo != null) {
            if (!operationFragment.checkParamValid(checkUserInfo.getUserName(), checkUserInfo.getPasswd(), checkUserInfo.getEmailCode())) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(500);
                responseDemo.setResult("failed");
                return responseDemo;
            }
            String ksid = pUserInfoHibernateDao.checkUserInfo(checkUserInfo.getUserName(), checkUserInfo.getPasswd(), checkUserInfo.getEmailCode());
            if (null == ksid || ksid.isEmpty()) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(500);
                responseDemo.setResult("");
                return responseDemo;
            }
            ResponseDemo responseDemo = new ResponseDemo();
            responseDemo.setCode(200);
            responseDemo.setResult(ksid);
            return responseDemo;
        }
        ResponseDemo responseDemo = new ResponseDemo();
        responseDemo.setCode(500);
        responseDemo.setResult("");
        return responseDemo;
    }

//    @RequestMapping(value = "/check/status",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
//    public ResponseDemo checkStatus(@RequestBody LYopRequest lYopRequest) {
//        CheckUserInfo checkUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(CheckUserInfo.class));
//        ResponseDemo responseDemo = new ResponseDemo();
//        if (checkUserInfo != null) {
//            if (checkUserInfo.getKsid() == null
//                    || checkUserInfo.getKsid().isEmpty()
//                    || checkUserInfo.getUserName() == null
//                    || checkUserInfo.getUserName().isEmpty()) {
//                responseDemo.setCode(500);
//                responseDemo.setResult("failed");
//                return responseDemo;
//            }
//
//            boolean status = pUserInfoHibernateDao.checkLoginStatus(checkUserInfo.getUserName(), checkUserInfo.getKsid());
//            if (status) {
//                responseDemo.setCode(200);
//                responseDemo.setResult("success");
//            } else {
//                responseDemo.setCode(500);
//                responseDemo.setResult("failed");
//            }
//
//            return responseDemo;
//        }
//        responseDemo.setCode(500);
//        responseDemo.setResult("failed");
//        return responseDemo;
//    }

    @RequestMapping(value = "/loginout",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo loginout(@RequestBody LYopRequest lYopRequest) {
        CheckUserInfo checkUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(CheckUserInfo.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (checkUserInfo != null) {
            if (checkUserInfo.getKsid() == null
                    || checkUserInfo.getKsid().isEmpty()
                    || checkUserInfo.getUserName() == null
                    || checkUserInfo.getUserName().isEmpty()) {
                responseDemo.setCode(500);
                responseDemo.setResult("failed");
                return responseDemo;
            }

            pUserInfoHibernateDao.loginOut(checkUserInfo.getUserName(), checkUserInfo.getKsid());
            responseDemo.setCode(200);
            responseDemo.setResult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setResult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/send/code",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo sendEmailCode(@RequestBody LYopRequest lYopRequest) {
        CheckUserInfo checkUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(CheckUserInfo.class));
        ResponseDemo responseDemo = new ResponseDemo();
        UserInfo userInfoByPasswd = pUserInfoHibernateDao.getUserInfoByPasswd(checkUserInfo.getUserName(), checkUserInfo.getPasswd());
        if (userInfoByPasswd == null) {
            responseDemo.setCode(404);
            responseDemo.setResult("failed");
            return responseDemo;
        }
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.host", "smtp.sina.cn");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties);
        session.setDebug(true);
        try {
            String emailCode = PassWordUtil.createPassWord(4);
            Message msg = getMimeMessage(session, userInfoByPasswd.getEmail(), emailCode);
            Transport transport = session.getTransport();
            transport.connect("dlv2014@sina.cn", "pm138319");
            transport.sendMessage(msg,msg.getAllRecipients());
            transport.close();
            pUserInfoHibernateDao.updateEmailCode(userInfoByPasswd.getUserName(), userInfoByPasswd.getPasswd(), emailCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseDemo.setCode(200);
        responseDemo.setResult("success");
        return responseDemo;
    }

    private MimeMessage getMimeMessage(Session session, String email, String emailCode) throws Exception{
        //创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress("dlv2014@sina.cn"));
        /**
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(email));
        //设置邮件主题
        msg.setSubject("简代邮箱验证码","UTF-8");
        //设置邮件正文
        msg.setContent(emailCode, "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());

        return msg;
    }

    @RequestMapping(value = "/get/email",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo getEmail(@RequestBody LYopRequest lYopRequest) {
        ResponseDemo responseDemo = new ResponseDemo();
        String email = pUserInfoHibernateDao.getEmail(lYopRequest.getSession());
        responseDemo.setCode(200);
        responseDemo.setResult(email);
        return responseDemo;
    }

    @RequestMapping(value = "/update/passwd",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo updateNewPassword(@RequestBody LYopRequest lYopRequest) {
        ResponseDemo responseDemo = new ResponseDemo();
        PasswordDto passwordDto = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(PasswordDto.class));
        if (!operationFragment.checkParamValid(passwordDto.getNewPasswd())) {
            responseDemo.setCode(500);
            return responseDemo;
        }
        Boolean res = pUserInfoHibernateDao.updateNewPassword(passwordDto.getUserName(), passwordDto.getOldPasswd(), passwordDto.getNewPasswd());
        if (res) {
            responseDemo.setCode(200);
        } else {
            responseDemo.setCode(500);
        }
        return responseDemo;
    }

    @RequestMapping(value = "/wx/login",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public WxResponseUserInfo wxLogin(@RequestBody LYopRequest lYopRequest) throws Exception {
        WxUserInfo wxUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(WxUserInfo.class));
        if (wxUserInfo == null) {
            throw new Exception("用户信息不足");
        }

        if (StringUtils.isEmpty(wxUserInfo.getCode())) {
            throw new Exception("用户信息不足");
        }

        String wxUrl = String.format(WxConstant.WX_AUTH_SESSION_URL, WxConstant.WX_APPID, WxConstant.WX_SECRET, wxUserInfo.getCode());
        ResponseEntity<String> forEntity = restTemplate.getForEntity(wxUrl, String.class);
        String body = forEntity.getBody();
        WxAuthRes wxAuthRes = JSONObject.parseObject(body, WxAuthRes.class);
        if (wxAuthRes == null) {
            throw new Exception("获取用户信息失败");
        }
        UserInfo userInfo = null;
        if (pUserInfoHibernateDao.checkOpenId(wxAuthRes.getOpenid())) {
            userInfo = pUserInfoHibernateDao.updateUserByOpenId(wxAuthRes.getOpenid(), wxAuthRes.getUnionid(), wxAuthRes.getSession_key(), wxUserInfo.getNickName());
        } else {
            userInfo = pUserInfoHibernateDao.createUserByOpenId(wxAuthRes.getOpenid(), wxAuthRes.getUnionid(), wxAuthRes.getSession_key(), wxUserInfo.getNickName());
        }
        if (userInfo == null) {
            throw new Exception("获取用户信息失败");
        }
        WxResponseUserInfo wxResponseUserInfo = new WxResponseUserInfo();
        wxResponseUserInfo.setSession(userInfo.getSession());
        wxResponseUserInfo.setUserName(userInfo.getUserName());
        wxResponseUserInfo.setCode(200);
        return wxResponseUserInfo;
    }
}
