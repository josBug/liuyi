package com.example.demo.dao;

import com.example.demo.mode.GoodsRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

@Repository
@Table(name="tb_goods")
@Qualifier("goodsRepository")
public interface PGoodsRecordDao extends CrudRepository<GoodsRecord, Long> {

    @Override
    public GoodsRecord save(GoodsRecord u);

    @Query(value = "select g from GoodsRecord g")
    public List<GoodsRecord> queryAllByNoCondition();

    @Query(value = "select g from GoodsRecord g where g.goodsName like %:goodsName%")
    public List<GoodsRecord> search(@Param("goodsName") String goodsName);
}
