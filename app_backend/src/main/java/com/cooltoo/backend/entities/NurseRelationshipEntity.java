package com.cooltoo.backend.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RelationshipType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/5/30.
 */
@Entity
@Table(name = "nursego_nurse_relationship")
public class NurseRelationshipEntity {

    private long id;
    private long userId;
    private long relativeUserId;
    private RelationshipType relationType;
    private Date time;
    private CommonStatus status;

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

    @Column(name = "relative_user_id")
    public long getRelativeUserId() {
        return relativeUserId;
    }

    public void setRelativeUserId(long relativeUserId) {
        this.relativeUserId = relativeUserId;
    }

    @Column(name = "relationship")
    @Enumerated
    public RelationshipType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationshipType relationType) {
        this.relationType = relationType;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", userId=").append(userId);
        msg.append(", relativeUserId=").append(relativeUserId);
        msg.append(", relationType=").append(relationType);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
