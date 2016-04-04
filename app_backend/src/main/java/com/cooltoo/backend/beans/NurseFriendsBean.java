package com.cooltoo.backend.beans;

import com.cooltoo.constants.AgreeType;

import java.util.Date;

/**
 * Created by yzzhao on 3/10/16.
 */
public class NurseFriendsBean {
    private long id;
    private long userId;
    private long friendId;
    private AgreeType isAgreed;
    private Date dateTime;
    private String headPhotoUrl;
    private String friendName;
    private boolean isFriend;

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

    public AgreeType getIsAgreed() {
        return this.isAgreed;
    }

    public void setIsAgreed(AgreeType isAgreed) {
        this.isAgreed = isAgreed;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getHeadPhotoUrl() {
        return headPhotoUrl;
    }

    public void setHeadPhotoUrl(String headPhotoUrl) {
        this.headPhotoUrl = headPhotoUrl;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }
}
