package com.cooltoo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/15.
 */
public enum BadgeGrade {
      LEVEL1   // 徽章等级
    , LEVEL2
    , LEVEL3
    ;

    public static BadgeGrade parseString(String grade) {
        if (LEVEL1.name().equalsIgnoreCase(grade)) {
            return LEVEL1;
        }
        else if (LEVEL2.name().equalsIgnoreCase(grade)) {
            return LEVEL2;
        }
        else if (LEVEL3.name().equalsIgnoreCase(grade)) {
            return LEVEL3;
        }
        return null;
    }


    public static List<String> getAllValues() {
        List<String> allEnums = new ArrayList<String>();
        allEnums.add(LEVEL1.name());
        allEnums.add(LEVEL2.name());
        allEnums.add(LEVEL3.name());
        return allEnums;
    }
}
