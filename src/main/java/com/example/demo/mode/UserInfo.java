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
@Table(name = "tb_user")
public class UserInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name="id")
    private long id;

    @Column(name="user_name")
    private String userName;

    @Column(name="passwd")
    private String passwd;

    @Column(name="email")
    private String email;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name="email_code")
    private String emailCode;

    @Column(name="session")
    private String session;

    @Column(name="status")
    private int status;

    @Column(name = "expire_email_code_time")
    private LocalDateTime expireEmailCodeTime;

    @Column(name="created_at", insertable = false, updatable = false)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createAt;

    @Column(name="updated_at", insertable = false, updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    @Column(name = "expire_session_time")
    private LocalDateTime expireSessionTime;

    @Column(name = "session_key")
    private String sessionKey;

    @Column(name = "wx_open_id")
    private String wxOpenId;

    @Column(name = "wx_union_id")
    private String wxUnionId;
}
