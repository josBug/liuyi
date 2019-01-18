package com.example.demo.map;

import com.example.demo.mode.GoodsRecord;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;

public interface GoodsRecordMapperDao {

    @Select("SELECT * FROM tb_goods WHERE id = #{id} AND user_id = #{userId}")
    @Results(id = "goodsReCord", value = {
            @Result(property = "id", column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT, id = true),
            @Result(property = "goodsName", column = "goods_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "code", column = "code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "color", column = "color", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "remark", column = "remark", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "userName", column = "user_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "source", column = "source", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "expressCode", column = "express_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createAt", column = "created_at", javaType = LocalDateTime.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "updateAt", column = "updated_at", javaType = LocalDateTime.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "amount", column = "amount", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "oldPrice", column = "old_price", javaType = Double.class, jdbcType = JdbcType.DOUBLE),
            @Result(property = "tip", column = "tip", javaType = Double.class, jdbcType = JdbcType.DOUBLE),
            @Result(property = "send", column = "send", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "countPrice", column = "count_price", javaType = Double.class, jdbcType = JdbcType.DOUBLE),
            @Result(property = "isPay", column = "is_pay", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "userId", column = "user_id", javaType = Long.class, jdbcType = JdbcType.BIGINT)
    })
    public GoodsRecord getByIdByMapper(@Param("id") Long id, @Param("userId") Long userId);
}
