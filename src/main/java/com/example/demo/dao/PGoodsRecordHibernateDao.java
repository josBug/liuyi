package com.example.demo.dao;

import com.example.demo.mode.GoodsRecord;
import com.example.demo.mode.StatictisModel;
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

    public List<GoodsRecord> queryByLastId(String sql, Map<String, Object> param, int limit) {
        Session session = sessionFactory.openSession();

        System.out.println(sql);
        Query query = session.createQuery(sql);

        for (String key: param.keySet()) {
            query.setParameter(key, param.get(key));
        }
        query.setMaxResults(limit);
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

    public StatictisModel statictisGoods(String sql, Map<String, Object> param) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery(sql);
        for (String key: param.keySet()) {
            query.setParameter(key, param.get(key));
        }
        System.out.println("================" + query.toString());
        List<Object[]> results = query.list();
        StatictisModel statictisModel = new StatictisModel();
        if (results.size() != 0) {
            Object[] res = results.get(0);
            if (res[0] == null) {
                return statictisModel;
            }
            statictisModel.setTips((Double)res[0]);
            statictisModel.setAmounts((Long) res[1]);
            statictisModel.setCountPrices((Double)res[2]);
            statictisModel.setOldPrices((Double)res[3]);
        }
        session.close();
        return statictisModel;
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

    public void updateBatch(List<Long> ids, int value, String sql) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery(sql);
            query.setParameter("value", value);
            query.setParameter("ids", ids);
            query.executeUpdate();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }

        session.getTransaction().commit();
        session.close();
    }

    public void updateExpress(List<Long> ids, String expressCode, String sql) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery(sql);
            query.setParameter("ids", ids);
            query.setParameter("expressCode", expressCode);
            query.executeUpdate();
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
