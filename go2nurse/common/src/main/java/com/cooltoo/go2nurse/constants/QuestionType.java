package com.cooltoo.go2nurse.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
public enum QuestionType {
    OTHER,
    SINGLE_SELECTION,  // 单选
    MULTI_SELECTION    // 多选
    ;

    public static QuestionType parseString(String type) {
        QuestionType enumeration = null;
        if (OTHER.name().equalsIgnoreCase(type)) {
            enumeration = OTHER;
        }
        else if (SINGLE_SELECTION.name().equalsIgnoreCase(type)) {
            enumeration = SINGLE_SELECTION;
        }
        else if (MULTI_SELECTION.name().equalsIgnoreCase(type)) {
            enumeration = MULTI_SELECTION;
        }
        return enumeration;
    }

    public static QuestionType parseInt(int type) {
        QuestionType enumeration = null;
        if (OTHER.ordinal() == type) {
            enumeration = OTHER;
        }
        else if (SINGLE_SELECTION.ordinal() == type) {
            enumeration = SINGLE_SELECTION;
        }
        else if (MULTI_SELECTION.ordinal() == type) {
            enumeration = MULTI_SELECTION;
        }
        return enumeration;
    }

    public static boolean exists(int type) {
        return null!=parseInt(type);
    }

    public static List<QuestionType> getAll() {
        List<QuestionType> enumerations = new ArrayList<>();
        enumerations.add(OTHER);
        enumerations.add(SINGLE_SELECTION);
        enumerations.add(MULTI_SELECTION);
        return enumerations;
    }

    public static List<QuestionType> getByTypes(List<Long> types) {
        List<QuestionType> retVal = new ArrayList<>();
        if (null==types || types.isEmpty()) {
            return  retVal;
        }
        for (Long type : types) {
            QuestionType de = parseInt(type.intValue());
            if (null==de) {
                continue;
            }
            retVal.add(de);
        }
        return retVal;
    }
}
