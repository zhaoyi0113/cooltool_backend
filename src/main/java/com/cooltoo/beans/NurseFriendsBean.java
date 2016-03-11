package com.cooltoo.beans;

/**
 * Created by yzzhao on 3/10/16.
 */
public class NurseFriendsBean {
    private long id;
    private long userId;
    private long friendId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }
}
