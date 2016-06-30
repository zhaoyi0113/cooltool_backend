package com.cooltoo.constants;

/**
 * Created by hp on 2016/6/30.
 */
public enum YesNoEnum {
    NO,
    YES,
    NONE
    ;

    public static YesNoEnum parseString(String type) {
        if (NO.name().equalsIgnoreCase(type)) {
            return NO;
        }
        else if (YES.name().equalsIgnoreCase(type)) {
            return YES;
        }
        else if (NONE.name().equalsIgnoreCase(type)) {
            return NONE;
        }
        return null;
    }

    public static YesNoEnum parseInt(int type) {
        if (NO.ordinal()==(type)) {
            return NO;
        }
        else if (YES.ordinal()==type) {
            return YES;
        }
        else if (NONE.ordinal()==type) {
            return NONE;
        }
        return null;
    }
}
