package com.cooltoo.backend.beans;

import com.cooltoo.constants.MessageType;
import com.cooltoo.constants.SocialAbilityType;

import java.util.Date;

/**
 * Created by lg380357 on 2016/3/7.
 */
public class MessageBean {

    private long id;
    private String content;
    private MessageType type;
    private Date time;
    private long userId;
    private String userName;
    private String profileImageUrl;
    private long abilityId;
    private SocialAbilityType abilityType;
    private String abilityName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public long getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(long abilityId) {
        this.abilityId = abilityId;
    }

    public SocialAbilityType getAbilityType() {
        return abilityType;
    }

    public void setAbilityType(SocialAbilityType abilityType) {
        this.abilityType = abilityType;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public void setAbilityName(String abilityName) {
        this.abilityName = abilityName;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String toString() {

        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", content=").append(content);
        msg.append(", type=").append(type);
        msg.append(", time=").append(time);
        msg.append(", userId=").append(userId);
        msg.append(", userName=").append(userName);
        msg.append(", profileImageUrl=").append(profileImageUrl);
        msg.append(", abilityId=").append(abilityId);
        msg.append(", abilityName=").append(abilityName);
        msg.append(", abilityType=").append(abilityType);
        msg.append(" ]");
        return msg.toString();
    }
}
