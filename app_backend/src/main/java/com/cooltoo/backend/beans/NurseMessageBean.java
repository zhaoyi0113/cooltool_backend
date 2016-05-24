package com.cooltoo.backend.beans;

import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.MessageType;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.SuggestionStatus;
import com.cooltoo.constants.UserType;

import java.util.Date;

/**
 * Created by lg380357 on 2016/3/7.
 */
public class NurseMessageBean {

    private long id;
    private long reasonId;
    private String content;
    private long userId;
    private UserType userType;
    private String userName;
    private String profileImageUrl;
    private long abilityId;
    private SocialAbilityType abilityType;
    private String abilityName;
    private MessageType type;
    private SpecificSocialAbility abilityApproved;
    private Date time;
    private SuggestionStatus status;

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

    public SuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(SuggestionStatus status) {
        this.status = status;
    }

    public long getReasonId() {
        return reasonId;
    }

    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public SpecificSocialAbility getAbilityApproved() {
        return abilityApproved;
    }

    public void setAbilityApproved(SpecificSocialAbility abilityApproved) {
        this.abilityApproved = abilityApproved;
    }

    public String toString() {

        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", reasonId=").append(reasonId);
        msg.append(", content=").append(content);
        msg.append(", type=").append(type);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", userId=").append(userId);
        msg.append(", userType").append(userType);
        msg.append(", userName=").append(userName);
        msg.append(", profileImageUrl=").append(profileImageUrl);
        msg.append(", abilityId=").append(abilityId);
        msg.append(", abilityName=").append(abilityName);
        msg.append(", abilityType=").append(abilityType);
        msg.append(", abilityApproved=").append(abilityApproved);
        msg.append(" ]");
        return msg.toString();
    }
}
