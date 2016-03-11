package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 3/10/16.
 */
@Entity
@Table(name = "nurse_friends")
public class NurseFriendsEntity {

    private long id;

    private long userId;

    private long friendId;

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
}
