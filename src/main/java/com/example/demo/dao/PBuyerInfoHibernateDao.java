package com.example.demo.dao;

import com.example.demo.mode.BuyerInfo;
import com.example.demo.mode.UserInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(rollbackFor = Exception.class)
public class PBuyerInfoHibernateDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public BuyerInfo addBuyer(String name, Long userId, String initial) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM BuyerInfo where name = :name and userId = :userId");
        query.setParameter("name", name);
        query.setParameter("userId", userId);
        query.setMaxResults(1);
        List<BuyerInfo> list = query.list();
        if (CollectionUtils.isEmpty(list)) {
            BuyerInfo buyerInfo = new BuyerInfo();
            buyerInfo.setName(name);
            buyerInfo.setUserId(userId);
            buyerInfo.setInitial(initial);
            session.save(buyerInfo);
            return buyerInfo;
        }
        BuyerInfo buyerInfo = list.get(0);
        session.update(buyerInfo);
        return buyerInfo;
    }

    public List<BuyerInfo> searchBuyer(String keyword, Long userId, int offset, int limit) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM BuyerInfo where name like :keyword and userId = :userId order by initial asc");
        query.setParameter("keyword", "%" + keyword + "%");
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        List<BuyerInfo> list = query.list();
        return list;
    }

    public List<BuyerInfo> listBuyer(int offset, int limit, Long userId) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM BuyerInfo where userId = :userId order by initial asc");
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        List<BuyerInfo> list = query.list();
        return list;
    }
}
