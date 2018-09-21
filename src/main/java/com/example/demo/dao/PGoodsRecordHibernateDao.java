package com.example.demo.dao;

import com.example.demo.mode.GoodsRecord;
import com.example.demo.stuct.SearchParam;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PGoodsRecordHibernateDao {

    public List<GoodsRecord> query(String sql, Map<String, Object> param, int offset, int limit) {
        Configuration config = new Configuration().configure();
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery(sql);
        for (String key: param.keySet()) {
            query.setParameter(key, param.get(key));
        }
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        List<GoodsRecord> list = query.list();
        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
        return list;
    }

    public int count(String sql, Map<String, Object> param, int offset, int limit) {
        Configuration config = new Configuration().configure();
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query query = session.createQuery(sql);
        for (String key: param.keySet()) {
            query.setParameter(key, param.get(key));
        }

        int count = ((Number)query.uniqueResult()).intValue();
        session.getTransaction().commit();

        session.close();
        sessionFactory.close();
        return count;
    }

    public void update(GoodsRecord goodsRecord) {
        Configuration config = new Configuration().configure();
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.update(goodsRecord);
        session.getTransaction().commit();

        session.close();
        sessionFactory.close();
    }

    public void delete(GoodsRecord goodsRecord) {
        Configuration config = new Configuration().configure();
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.delete(goodsRecord);
        session.getTransaction().commit();

        session.close();
        sessionFactory.close();
    }
}
