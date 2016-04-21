package com.cooltoo.constants;

import javax.swing.plaf.basic.BasicTreeUI;

/**
 * Created by hp on 2016/4/20.
 */
public enum ActivityStatus {
      DISABLE  // 活动不可用
    , ENABLE   // 活动可用
    , EDITING  // 活动编辑中
    ;

    public static ActivityStatus parseString(String status) {
        if (DISABLE.name().equalsIgnoreCase(status)) {
            return DISABLE;
        }
        else if (ENABLE.name().equalsIgnoreCase(status)) {
            return ENABLE;
        }
        else if (EDITING.name().equalsIgnoreCase(status)) {
            return EDITING;
        }
        return null;
    }
}
