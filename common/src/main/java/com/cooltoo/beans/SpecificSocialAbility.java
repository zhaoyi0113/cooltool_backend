package com.cooltoo.beans;

import com.cooltoo.constants.SocialAbilityType;

import java.util.Properties;

/**
 * Created by zhaolisong on 16/5/6.
 */
public class SpecificSocialAbility {

    public static final String Speak_Type = "speak_type";

    private int abilityId;
    private String abilityName;
    private int factor;
    private SocialAbilityType abilityType;
    private Properties otherProperties;

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

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public void addProperty(String key, Object value) {
        if (null==otherProperties) {
            otherProperties = new Properties();
        }
        otherProperties.put(key, value);
    }

    public Object getProperty(String key) {
        if (null==otherProperties) {
            return null;
        }
        return otherProperties.get(key);
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("abilityId=").append(abilityId);
        msg.append(", abilityName=").append(abilityName);
        msg.append(", abilityType=").append(abilityType);
        msg.append(", factor=").append(factor);
        msg.append(", otherProperties=").append(otherProperties);
        msg.append("]");
        return msg.toString();
    }
}
