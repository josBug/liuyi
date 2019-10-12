package com.example.demo.dao;

import com.example.demo.mode.UserInfo;
import com.example.demo.util.EmojiUtils;
import com.example.demo.util.PassWordUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(rollbackFor = Exception.class)
public class PUserInfoHibernateDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public String checkUserInfo(String userName, String passwd, String emailCode) {
        Session session = getCurrentSession();
        String ksid = null;
        Query query = session.createQuery("FROM UserInfo where userName = :userName and passwd = :passwd and emailCode = :emailCode and expireEmailCodeTime > :expireEmailCodeTime");
        query.setParameter("userName", userName);
        query.setParameter("passwd", passwd);
        query.setParameter("emailCode", emailCode);
        query.setParameter("expireEmailCodeTime", LocalDateTime.now());
        query.setMaxResults(1);
        List<UserInfo> list = query.list();
        if (list.size() != 1) {
            return null;
        }

        UUID uuid=UUID.randomUUID();
        ksid = uuid.toString();

        UserInfo userInfo = list.get(0);
        userInfo.setSession(ksid);
        userInfo.setStatus(1);
        userInfo.setExpireSessionTime(LocalDateTime.now().plusDays(3));
        session.update(userInfo);
        return ksid;
    }

    public UserInfo checkKsid(String ksid) {
        Session session = getCurrentSession();
        List<UserInfo> list = new ArrayList<>();
        try {
            Query query = session.createQuery("FROM UserInfo where session = :ksid and expireSessionTime > :expireSessionTime");
            query.setParameter("ksid", ksid);
            query.setParameter("expireSessionTime", LocalDateTime.now());
            query.setMaxResults(1);
            list = query.list();

            if (list.isEmpty()) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }


        return list.get(0);
    }

    public boolean checkOpenId(String openId) {
        Session session = getCurrentSession();
        List<UserInfo> list = new ArrayList<>();
        try {
            Query query = session.createQuery("FROM UserInfo where wxOpenId = :openId");
            query.setParameter("openId", openId);
            query.setMaxResults(1);
            list = query.list();

            if (list.isEmpty()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public UserInfo createUserByOpenId(String openId, String unionId, String sessionKey, String nickName) {
        Session session = getCurrentSession();
        UUID userUuid = UUID.randomUUID();
        String userName = userUuid.toString();
        String passwd = "JD123456";
        String ksid = null;

        UUID uuid=UUID.randomUUID();
        ksid = uuid.toString();

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setPasswd(passwd);
        userInfo.setEmail("dlv2014@sina.cn");
        userInfo.setEmailCode("");
        userInfo.setNickName(EmojiUtils.filterEmoji(nickName, "*"));
        userInfo.setSession(ksid);
        userInfo.setStatus(1);
        userInfo.setWxOpenId(openId);
        userInfo.setWxUnionId(unionId == null ? "" : unionId);
        userInfo.setSessionKey(sessionKey);
        userInfo.setExpireSessionTime(LocalDateTime.now().plusDays(3));
        session.save(userInfo);
        return userInfo;
    }


    public UserInfo updateUserByOpenId(String openId, String unionId, String sessionKey, String nickName) {
        Session session = getCurrentSession();
        List<UserInfo> list = new ArrayList<>();
        try {
            Query query = session.createQuery("FROM UserInfo where wxOpenId = :openId");
            query.setParameter("openId", openId);
            query.setMaxResults(1);
            list = query.list();

            if (list.isEmpty()) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        String ksid = null;

        UUID uuid=UUID.randomUUID();
        ksid = uuid.toString();
        UserInfo userInfo = list.get(0);
        userInfo.setEmailCode("");
        userInfo.setSession(ksid);
        userInfo.setStatus(1);
        userInfo.setNickName(EmojiUtils.filterEmoji(nickName, "*"));
        userInfo.setWxOpenId(openId);
        userInfo.setWxUnionId(unionId == null ? "" : unionId);
        userInfo.setSessionKey(sessionKey);
        userInfo.setExpireSessionTime(LocalDateTime.now().plusDays(3));
        session.update(userInfo);
        return userInfo;
    }

    public boolean checkLoginStatus(String userName, String ksid) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM UserInfo where userName = :userName and session = :ksid");
        query.setParameter("userName", userName);
        query.setParameter("ksid", ksid);
        query.setMaxResults(1);
        List<UserInfo> list = query.list();

        if (list.size() != 1) {
            return false;
        }

        UserInfo userInfo = list.get(0);
        if (userInfo.getStatus() == 1) {
            return false;
        }

        userInfo.setStatus(1);

        session.update(userInfo);
        return true;
    }

    public void loginOut(String userName, String ksid) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM UserInfo where userName = :userName and session = :ksid");
        query.setParameter("userName", userName);
        query.setParameter("ksid", ksid);
        query.setMaxResults(1);
        List<UserInfo> list = query.list();
        if (list.size() != 1) {
            return;
        }

        UserInfo userInfo = list.get(0);
        if (userInfo.getStatus() == 0) {
            return;
        }

        userInfo.setSession("");
        userInfo.setStatus(0);
        userInfo.setEmailCode("");

        session.update(userInfo);

    }

    public boolean registryUser(String userName, String passwd, String email) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM UserInfo where userName = :userName");
        query.setParameter("userName", userName);
        query.setMaxResults(1);
        List<UserInfo> list = query.list();
        if (list.size() != 0) {
            return false;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setPasswd(passwd);
        userInfo.setEmail(email);
        userInfo.setSession("");
        userInfo.setStatus(0);
        userInfo.setEmailCode("");
        userInfo.setNickName(userName);
        userInfo.setWxUnionId("");
        userInfo.setWxOpenId("");
        userInfo.setSessionKey("");
        userInfo.setExpireSessionTime(LocalDateTime.now().plusDays(2));

        session.save(userInfo);


        return true;
    }

    public void updateEmailCode(String userName, String passwd, String emailCode) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM UserInfo where userName = :userName and passwd = :passwd");
        query.setParameter("userName", userName);
        query.setParameter("passwd", passwd);
        query.setMaxResults(1);
        List<UserInfo> list = query.list();
        if (list.size() != 1) {
            return;
        }

        UserInfo userInfo = list.get(0);
        userInfo.setEmailCode(emailCode);
        userInfo.setExpireEmailCodeTime(LocalDateTime.now().plusSeconds(60));
        session.update(userInfo);

    }

    public UserInfo getUserInfoByPasswd(String userName, String passwd) {
        Session session = getCurrentSession();
        UserInfo userInfo = null;
        try {
            Query query = session.createQuery("FROM UserInfo where userName = :userName and passwd = :passwd");
            query.setParameter("userName", userName);
            query.setParameter("passwd", passwd);
            query.setMaxResults(1);
            List<UserInfo> list = query.list();
            if (list.size() != 1) {
                return null;
            }

            userInfo = list.get(0);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return userInfo;
    }

    public String getEmail(String tempSession) {
        Session session = getCurrentSession();
        UserInfo userInfo = null;
        try {
            Query query = session.createQuery("FROM UserInfo where session = :tempSession");
            query.setParameter("tempSession", tempSession);
            query.setMaxResults(1);
            List<UserInfo> list = query.list();
            if (list.size() != 1) {
                return "";
            }

            userInfo = list.get(0);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return userInfo.getEmail();
    }

    public Boolean updateNewPassword(String userName, String oldPasswd, String newPasswd) {
        Session session = getCurrentSession();
        UserInfo userInfo = null;

        try {
            Query query = session.createQuery("update UserInfo set passwd = :newPasswd where userName = :userName and passwd = :oldPasswd");
            query.setParameter("newPasswd", newPasswd);
            query.setParameter("userName", userName);
            query.setParameter("oldPasswd", oldPasswd);
            int res = query.executeUpdate();
            if (res <= 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
