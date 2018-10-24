package com.example.demo.controller;

import com.example.demo.dao.PUserInfoHibernateDao;
import com.example.demo.stuct.CheckUserInfo;
import com.example.demo.stuct.LYopRequest;
import com.example.demo.stuct.ResponseDemo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping(value = "/",produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
    public ModelAndView sign() {
        ModelAndView modelAndView = new ModelAndView("index");
        return modelAndView;
    }

    @RequestMapping(value = "/registry",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo registry(@RequestBody LYopRequest lYopRequest) {
        CheckUserInfo checkUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(CheckUserInfo.class));

        if (checkUserInfo != null) {
            if (checkUserInfo.getUserName() == null
                    || checkUserInfo.getUserName().isEmpty()
                    || checkUserInfo.getPasswd() == null
                    || checkUserInfo.getPasswd().isEmpty()
                    || checkUserInfo.getEmail() == null
                    || checkUserInfo.getEmail().isEmpty()) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(500);
                responseDemo.setRessult("failed");
                return responseDemo;
            }
            boolean success = pUserInfoHibernateDao.registryUser(checkUserInfo.getUserName(), checkUserInfo.getPasswd(), checkUserInfo.getEmail());
            if (!success) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(500);
                responseDemo.setRessult("failed");
                return responseDemo;
            }
            ResponseDemo responseDemo = new ResponseDemo();
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        ResponseDemo responseDemo = new ResponseDemo();
        responseDemo.setCode(500);
        responseDemo.setRessult("");
        return null;
    }

    @RequestMapping(value = "/check/user",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo checkUser(@RequestBody LYopRequest lYopRequest) {
        CheckUserInfo checkUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(CheckUserInfo.class));

        if (checkUserInfo != null) {
            if (checkUserInfo.getUserName() == null
                    || checkUserInfo.getUserName().isEmpty()
                    || checkUserInfo.getPasswd() == null
                    || checkUserInfo.getPasswd().isEmpty()) {
                ResponseDemo responseDemo = new ResponseDemo();
                responseDemo.setCode(500);
                responseDemo.setRessult("failed");
                return responseDemo;
            }

            String ksid = pUserInfoHibernateDao.checkUserInfo(checkUserInfo.getUserName(), checkUserInfo.getPasswd(), checkUserInfo.getEmailCode());
            ResponseDemo responseDemo = new ResponseDemo();
            responseDemo.setCode(200);
            responseDemo.setRessult(ksid);
            return responseDemo;
        }
        ResponseDemo responseDemo = new ResponseDemo();
        responseDemo.setCode(500);
        responseDemo.setRessult("");
        return responseDemo;
    }

    @RequestMapping(value = "/check/status",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo checkStatus(@RequestBody LYopRequest lYopRequest) {
        CheckUserInfo checkUserInfo = mapper.convertValue(lYopRequest.getObject(), mapper.constructType(CheckUserInfo.class));
        ResponseDemo responseDemo = new ResponseDemo();
        if (checkUserInfo != null) {
            if (checkUserInfo.getKsid() == null
                    || checkUserInfo.getKsid().isEmpty()
                    || checkUserInfo.getUserName() == null
                    || checkUserInfo.getUserName().isEmpty()) {
                responseDemo.setCode(500);
                responseDemo.setRessult("failed");
                return responseDemo;
            }

            boolean status = pUserInfoHibernateDao.checkLoginStatus(checkUserInfo.getUserName(), checkUserInfo.getKsid());
            if (status) {
                responseDemo.setCode(200);
                responseDemo.setRessult("success");
            } else {
                responseDemo.setCode(500);
                responseDemo.setRessult("failed");
            }

            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }

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
                responseDemo.setRessult("failed");
                return responseDemo;
            }

            pUserInfoHibernateDao.loginOut(checkUserInfo.getUserName(), checkUserInfo.getKsid());
            responseDemo.setCode(200);
            responseDemo.setRessult("success");
            return responseDemo;
        }
        responseDemo.setCode(500);
        responseDemo.setRessult("failed");
        return responseDemo;
    }

    @RequestMapping(value = "/send/code",produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
    public ResponseDemo sendEmailCode(@RequestBody LYopRequest lYopRequest) {
        Properties properties = new Properties();
        ResponseDemo responseDemo = new ResponseDemo();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.host", "smtp.sina.cn");
        properties.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties);
        session.setDebug(true);
        try {
            Message msg = getMimeMessage(session);
            Transport transport = session.getTransport();
            transport.connect("dlv2014@sina.cn", "pm138319");
            transport.sendMessage(msg,msg.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseDemo.setCode(200);
        responseDemo.setRessult("success");
        return responseDemo;
    }

    private MimeMessage getMimeMessage(Session session) throws Exception{
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
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress("775986641@qq.com"));
        //设置邮件主题
        msg.setSubject("邮件主题","UTF-8");
        //设置邮件正文
        msg.setContent("简单的纯文本邮件！", "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());

        return msg;
    }
}
