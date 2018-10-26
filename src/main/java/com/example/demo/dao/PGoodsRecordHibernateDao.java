package com.example.demo.dao;

import com.example.demo.mode.GoodsRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Repository
public class PGoodsRecordHibernateDao {

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

    public List<GoodsRecord> query(String sql, Map<String, Object> param, int offset, int limit) {
        Session session = sessionFactory.openSession();

        System.out.println(sql);
        Query query = null;
        try {
            Method createQuery = session.getClass().getMethod("createQuery", String.class);
            query = (Query) createQuery.invoke(session, sql);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        for (String key: param.keySet()) {
            query.setParameter(key, param.get(key));
        }
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        List<GoodsRecord> list = query.list();
        session.close();
        return list;
    }

    public void save(GoodsRecord goodsRecord) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.save(goodsRecord);
            session.flush();
            session.evict(goodsRecord);
        } catch (Exception e) {
            session.getTransaction().rollback();
        }

        session.getTransaction().commit();
        session.close();
    }

    public int count(String sql, Map<String, Object> param, int offset, int limit) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery(sql);
        for (String key: param.keySet()) {
            query.setParameter(key, param.get(key));
        }
        System.out.println("================" + query.toString());
        int count = ((Number)query.uniqueResult()).intValue();
        session.close();
        return count;
    }

    public void update(GoodsRecord goodsRecord) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            session.update(goodsRecord);
            session.flush();
            session.evict(goodsRecord);
        } catch (Exception e) {
            session.getTransaction().rollback();
        }

        session.getTransaction().commit();
        session.close();
    }

    public void delete(GoodsRecord goodsRecord) {
        Session session = sessionFactory.openSession();
        session.delete(goodsRecord);
        session.close();
    }
}
