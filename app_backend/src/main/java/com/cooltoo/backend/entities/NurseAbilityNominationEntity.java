package com.cooltoo.backend.entities;

import com.cooltoo.constants.SocialAbilityType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yzzhao on 3/13/16.
 */
@Entity
@Table(name = "nursego_nurse_ability_nomination")
public class NurseAbilityNominationEntity {
    private long id;
    private long userId;
    private long nominatedId;
    private int abilityId;
    private SocialAbilityType abilityType;
    private Date dateTime;

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

    @Column(name = "skill_id")
    public int getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    @Column(name = "nomiated_user_id")
    public long getNominatedId() {
        return nominatedId;
    }

    public void setNominatedId(long nominatedId) {
        this.nominatedId = nominatedId;
    }

    @Column(name = "date_time")
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }


    @Column(name = "skill_type")
    @GeneratedValue
    public SocialAbilityType getAbilityType() {
        return abilityType;
    }

    public void setAbilityType(SocialAbilityType abilityType) {
        this.abilityType = abilityType;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("userId=").append(userId).append(", ");
        msg.append("abilityId=").append(abilityId).append(", ");
        msg.append("nominatedId=").append(nominatedId).append(", ");
        msg.append("dateTime=").append(dateTime).append(", ");
        msg.append("abilityType=").append(abilityType).append("]");
        return msg.toString();
    }
}
