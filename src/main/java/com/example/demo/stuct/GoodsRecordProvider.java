package com.example.demo.stuct;

import com.example.demo.mode.GoodsRecord;
import org.apache.ibatis.jdbc.SQL;

public class GoodsRecordProvider {

    private static final String ALL_FIELDS = "id,goods_name,code,color,name,remark,user_name,source,express_code,created_at,updated_at,amount,old_price,tip,send,count_price,is_pay,user_id";

    private static final String TABLE = "tb_goods";

    public String getByIdByMapperSelectProvider() {
        SQL sql = new SQL().SELECT(ALL_FIELDS).FROM(TABLE);
        sql.WHERE("id = #{id}").AND().WHERE("user_id = #{userId}");
        return sql.toString();
    }

    public String getByIdByMapperSelectProviderV2(GoodsRecord goodsRecord) {
        SQL sql = new SQL().SELECT(ALL_FIELDS).FROM(TABLE);
        sql.WHERE("id = #{goodsRecord.id}");
        sql.WHERE("user_id = #{goodsRecord.userId}");
        return sql.toString();
    }
}
