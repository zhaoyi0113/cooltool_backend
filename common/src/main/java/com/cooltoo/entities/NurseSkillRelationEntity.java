package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 3/13/16.
 */
@Entity
@Table(name = "nurse_occupation_skill")
public class NurseSkillRelationEntity {

    private int id;
    private long userId;
    private int skillId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
