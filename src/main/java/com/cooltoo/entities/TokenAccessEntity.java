package com.cooltoo.entities;

import com.cooltoo.constants.UserType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yzzhao on 3/2/16.
 */
@Entity
@Table(name = "token_access")
public class TokenAccessEntity {
    private long id;

    private long userId;

    private UserType type;

    private Date timeCreated;

    private String token;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "user_id")
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "type")
    @Enumerated
    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    @Column(name = "time_created")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
