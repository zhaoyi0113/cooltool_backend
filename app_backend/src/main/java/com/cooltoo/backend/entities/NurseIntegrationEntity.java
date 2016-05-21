package com.cooltoo.backend.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.UserType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yzzhao on 5/21/16.
 */
@Entity
@Table(name = "nurse_integration")
public class NurseIntegrationEntity {

    private long id;

    private long userId;

    private UserType userType;

    //根据abilityType和abilityId决定reasonId,如果abilityType是community,abilityId是SpeakType.SMUG的id,则reasonId就是nurse_speak表中对应的发言ID
    private long reasonId;

    // 技能表中的ID,如果abilityType是community,该ID就是speak_type表中的id;如果是skill, 就是occupation_skill表中的id
    private long abilityId;

    private SocialAbilityType abilityType;

    private int point;

    private Date timeCreated;

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

    @Column(name = "user_type")
    @Enumerated
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Column(name = "reason_id")
    public long getReasonId() {
        return reasonId;
    }

    public void setReasonId(long reasonId) {
        this.reasonId = reasonId;
    }

    @Column(name = "ability_id")
    public long getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(long abilityId) {
        this.abilityId = abilityId;
    }

    @Column(name = "ability_type")
    @Enumerated
    public SocialAbilityType getAbilityType() {
        return abilityType;
    }

    public void setAbilityType(SocialAbilityType abilityType) {
        this.abilityType = abilityType;
    }

    @Column(name = "point")
    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    @Column(name = "time_created")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "status")
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }
}
