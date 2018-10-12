package com.example.demo.dao;

import com.example.demo.mode.GoodsRecord;
import com.example.demo.stuct.SearchParam;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class PGoodsRecordHibernateDao {

    private static SessionFactory sessionFactory;

    static {
        Configuration config = new Configuration().configure();
        sessionFactory = config.buildSessionFactory();
    }

    public List<GoodsRecord> query(String sql, Map<String, Object> param, int offset, int limit) {
        Session session = sessionFactory.openSession();

        System.out.println(sql);
        Query query = session.createQuery(sql);
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
