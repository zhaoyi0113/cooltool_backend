package com.cooltoo.constants;

/**
 * Created by zhaolisong on 16/4/13.
 */
public enum OccupationSkillStatus {
      DISABLE  // 禁用技能
    , ENABLE   // 启用技能
    ;

    public static OccupationSkillStatus parseStatus(String status) {
        if (DISABLE.name().equalsIgnoreCase(status)) {
            return DISABLE;
        }
        else if (ENABLE.name().equalsIgnoreCase(status)) {
            return ENABLE;
        }
        return null;
    }
}
