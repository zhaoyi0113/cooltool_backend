package com.cooltoo.beans;

import com.cooltoo.constants.SocialAbilityType;

/**
 * Created by zhaolisong on 16/5/6.
 */
public class SpecificSocialAbility {
    private int abilityId;
    private String abilityName;
    private SocialAbilityType abilityType;

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

    public int getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("abilityId=").append(abilityId);
        msg.append(", abilityName=").append(abilityName);
        msg.append(", abilityType=").append(abilityType);
        msg.append("]");
        return msg.toString();
    }
}
