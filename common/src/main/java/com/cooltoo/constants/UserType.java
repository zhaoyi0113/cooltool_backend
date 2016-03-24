package com.cooltoo.constants;

/**
 * Created by yzzhao on 3/2/16.
 */
public enum UserType {
    NURSE;

    public static UserType parseString(String user) {
        if (NURSE.name().equalsIgnoreCase(user)) {
            return NURSE;
        }
        return null;
    }
    public static UserType parseInt(int user) {
        if (NURSE.ordinal()==(user)) {
            return NURSE;
        }
        return null;
    }
}
