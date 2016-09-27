package com.cooltoo.constants;

/**
 * Created by yzzhao on 3/2/16.
 */
public enum UserType {
    NURSE,
    NORMAL_USER,
    DOCTOR;

    public static UserType parseString(String user) {
        if (NURSE.name().equalsIgnoreCase(user)) {
            return NURSE;
        }
        else if (NORMAL_USER.name().equalsIgnoreCase(user)) {
            return NORMAL_USER;
        }
        else if (DOCTOR.name().equalsIgnoreCase(user)) {
            return DOCTOR;
        }
        return null;
    }
    public static UserType parseInt(int user) {
        if (NURSE.ordinal()==(user)) {
            return NURSE;
        }
        else if (NORMAL_USER.ordinal()==user) {
            return NORMAL_USER;
        }
        else if (DOCTOR.ordinal()==user) {
            return DOCTOR;
        }
        return null;
    }
}
