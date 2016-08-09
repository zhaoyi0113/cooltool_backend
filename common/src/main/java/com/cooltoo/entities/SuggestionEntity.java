package com.cooltoo.entities;

import com.cooltoo.constants.PlatformType;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.UserType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Entity
@Table(name = "nursego_user_suggestion")
public class SuggestionEntity {
    private long   id;
    private long   userId;
    private String suggestion;
    private Date   timeCreated;
    private ReadingStatus status;
    private UserType userType;
    private String userName;
    private PlatformType platform;
    private String version;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return this.id;
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

    @Column(name = "suggestion")
    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Column(name = "create_time")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "status")
    @Enumerated
    public ReadingStatus getStatus() {
        return status;
    }

    public void setStatus(ReadingStatus status) {
        this.status = status;
    }

    @Column(name = "user_type")
    @Enumerated
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Column(name = "user_name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "platform")
    @Enumerated
    public PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformType platform) {
        this.platform = platform;
    }

    @Column(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append(" id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", userType=").append(userType);
        msg.append(", platform=").append(platform);
        msg.append(", version=").append(version);
        msg.append(", suggestion=").append(suggestion);
        msg.append(", status").append(status);
        msg.append(", timeCreated=").append(timeCreated);
        msg.append("] ");
        return msg.toString();
    }
}
