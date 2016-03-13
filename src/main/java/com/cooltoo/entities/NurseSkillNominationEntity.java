package com.cooltoo.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yzzhao on 3/13/16.
 */
@Entity
@Table(name = "nurse_skill_nomination")
public class NurseSkillNominationEntity {
    private long id;
    private long userId;
    private int skillId;
    private long nominatedId;
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
    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
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
}
