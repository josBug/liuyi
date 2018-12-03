package com.example.demo.dao;

import com.example.demo.mode.GoodsRecord;
import com.example.demo.stuct.StatictisModel;
import com.example.demo.stuct.BizCurrentMonth;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
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

    public GoodsRecord getById(Long id, Long userId) {
        Session session = sessionFactory.openSession();

        Query query = session.createQuery("FROM GoodsRecord WHERE id = :id and userId = :userId");
        query.setParameter("id", id);
        query.setParameter("userId", userId);
        List<GoodsRecord> goodsRecords = query.list();
        session.close();
        return CollectionUtils.isEmpty(goodsRecords) ? null : goodsRecords.get(0);
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
            return;
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
            return;
        }

        session.getTransaction().commit();
        session.close();
    }

    public void updateBatch(List<Long> ids, int value, String sql, Long userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery(sql);
            query.setParameter("value", value);
            query.setParameter("ids", ids);
            query.setParameter("userId", userId);
            query.executeUpdate();
        } catch (Exception e) {
            return;
        }

        session.getTransaction().commit();
        session.close();
    }

    public void updateExpress(List<Long> ids, String expressCode, String sql, Long userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery(sql);
            query.setParameter("ids", ids);
            query.setParameter("expressCode", expressCode);
            query.setParameter("userId", userId);
            query.executeUpdate();
        } catch (Exception e) {
            return;
        }

        session.getTransaction().commit();
        session.close();
    }

    public void delete(GoodsRecord goodsRecord) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(goodsRecord);
        session.getTransaction().commit();
        session.close();
    }

    public void deleteV2(List<Long> ids, Long userId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Query query = session.createQuery("DELETE FROM GoodsRecord WHERE id in (:ids) and userId = :userId");
            query.setParameter("ids", ids);
            query.setParameter("userId",userId);
            query.executeUpdate();
        }catch (Exception e) {
            return;
        }

        session.getTransaction().commit();
        session.close();
    }

    public BizCurrentMonth getMonthStatistic(String sql, Long userId) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery(sql);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusDays(now.getDayOfMonth() - 1).withHour(0).withMinute(0).withSecond(0);
        query.setParameter("userId", userId);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", now);

        System.out.println("================" + query.toString());
        List<Object[]> results = query.list();
        BizCurrentMonth bizCurrentMonth = new BizCurrentMonth();
        if (results.size() != 0) {
            Object[] res = results.get(0);
            if (res[0] == null) {
                return bizCurrentMonth;
            }
            bizCurrentMonth.setAmount((Integer) res[0]);
            bizCurrentMonth.setOldPrice((Double) res[1]);
            bizCurrentMonth.setTips((Double)res[2]);
            bizCurrentMonth.setCountPrice((Double)res[3]);
        }
        session.close();
        return bizCurrentMonth;
    }

}
