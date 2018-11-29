package com.example.demo.mode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_goods")
public class GoodsRecord {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="id")
    private long id;

    @Column(name="goods_name")
    private String goodsName;

    @Column(name="code")
    private String code;

    @Column(name="color")
    private String color;

    @Column(name="amount")
    private long amount;

    @Column(name="old_price")
    private double oldPrice;

    @Column(name="name")
    private String name;

    @Column(name="tip")
    private double tip;

    @Column(name="send")
    private int send;

    @Column(name="count_price")
    private double countPrice;

    @Column(name="is_pay")
    private int isPay;

    @Column(name="remark")
    private String remark;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "source")
    private String source;

    @Column(name = "express_code")
    private String expressCode;

    @Column(name="created_at", insertable = false, updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;

    @Column(name="updated_at", insertable = false, updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
}
