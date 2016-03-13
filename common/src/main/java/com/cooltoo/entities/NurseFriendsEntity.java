package com.cooltoo.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yzzhao on 3/10/16.
 */
@Entity
@Table(name = "nurse_friends")
public class NurseFriendsEntity {

    private long id;

    private long userId;

    private long friendId;

    private Date dateTime;

    @Id
    @Column(name = "id")
    @GeneratedValue
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

    @Column(name = "friend_id")
    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    @Column(name = "date_time")
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
