package com.example.demo.dao;

import com.example.demo.mode.UserInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@Repository
public class PUserInfoHibernateDao {

    private SessionFactory sessionFactory;

    @PostConstruct
    public void initSessionFactory() {
        Configuration config = new Configuration().configure();
        sessionFactory = config.buildSessionFactory();
    }

    @PreDestroy
    public void destorySession() {
        sessionFactory.close();
    }

    public String checkUserInfo(String userName, String passwd, String emailCode) {
        Session session = sessionFactory.openSession();

        Query query = session.createQuery("FROM UserInfo where userName = :userName and passwd = :passwd and emailCode = :emailCode");
        query.setParameter("userName", userName);
        query.setParameter("passwd", passwd);
        query.setParameter("emailCode", emailCode);
        query.setMaxResults(1);
        List<UserInfo> list = query.list();
        if (list.size() != 1) {
            return null;
        }

        UUID uuid=UUID.randomUUID();
        String ksid = uuid.toString();
        UserInfo userInfo = list.get(0);
        userInfo.setSession(ksid);
        userInfo.setStatus(1);

        session.beginTransaction();
        session.update(userInfo);
        session.getTransaction().commit();

        session.close();
        return ksid;
    }

    public UserInfo checkKsid(String ksid) {
        Session session = sessionFactory.openSession();

        Query query = session.createQuery("FROM UserInfo where session = :ksid");
        query.setParameter("ksid", ksid);
        query.setMaxResults(1);
        List<UserInfo> list = query.list();

        if (list.isEmpty()) {
            return null;
        }

        session.close();
        return list.get(0);
    }

    public boolean checkLoginStatus(String userName, String ksid) {
        Session session = sessionFactory.openSession();

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

        session.beginTransaction();
        session.update(userInfo);
        session.getTransaction().commit();

        session.close();
        return true;
    }

    public void loginOut(String userName, String ksid) {
        Session session = sessionFactory.openSession();

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

        session.beginTransaction();
        session.update(userInfo);
        session.getTransaction().commit();

        session.close();
    }

    public boolean registryUser(String userName, String passwd, String email) {
        Session session = sessionFactory.openSession();

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


        session.beginTransaction();
        session.save(userInfo);
        session.getTransaction().commit();

        session.close();
        return true;
    }

    public void updateEmailCode(String userName, String passwd, String emailCode) {
        Session session = sessionFactory.openSession();

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

        session.beginTransaction();
        session.update(userInfo);
        session.getTransaction().commit();

        session.close();
    }

    public UserInfo getUserInfoByPasswd(String userName, String passwd) {
        Session session = sessionFactory.openSession();

        Query query = session.createQuery("FROM UserInfo where userName = :userName and passwd = :passwd");
        query.setParameter("userName", userName);
        query.setParameter("passwd", passwd);
        query.setMaxResults(1);
        List<UserInfo> list = query.list();
        if (list.size() != 1) {
            return null;
        }

        UserInfo userInfo = list.get(0);
        session.close();
        return userInfo;
    }

}
