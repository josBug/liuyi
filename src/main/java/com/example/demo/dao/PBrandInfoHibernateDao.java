package com.example.demo.dao;

import com.example.demo.mode.BrandInfo;
import com.example.demo.mode.BuyerInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class PBrandInfoHibernateDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public BrandInfo addBrand(String name, Long userId, String source) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM BrandInfo where name = :name and userId = :userId");
        query.setParameter("name", name);
        query.setParameter("userId", userId);
        query.setMaxResults(1);
        List<BrandInfo> list = query.list();
        if (CollectionUtils.isEmpty(list)) {
            BrandInfo brandInfo = new BrandInfo();
            brandInfo.setName(name);
            brandInfo.setUserId(userId);
            brandInfo.setSource(source);
            session.save(brandInfo);
            return brandInfo;
        }
        BrandInfo brandInfo = list.get(0);
        session.update(brandInfo);
        return brandInfo;
    }

    public List<BrandInfo> searchBrand(String keyword, Long userId, int offset, int limit) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM BrandInfo where name like :keyword and userId = :userId");
        query.setParameter("keyword", "%" + keyword + "%");
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        List<BrandInfo> list = query.list();
        return list;
    }

    public List<BrandInfo> listBrand(int offset, int limit, Long userId) {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM BuyerInfo where userId = :userId");
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        List<BrandInfo> list = query.list();
        return list;
    }
}
