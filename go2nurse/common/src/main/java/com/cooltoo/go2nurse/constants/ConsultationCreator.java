package com.cooltoo.go2nurse.constants;

/**
 * Created by zhaolisong on 2016/11/8.
 */
public enum  ConsultationCreator {
    USER,
    NURSE;

    public static ConsultationCreator parseString(String type) {
        ConsultationCreator ret = null;
        if (USER.name().equalsIgnoreCase(type)) {
            ret = USER;
        }
        else if (NURSE.name().equalsIgnoreCase(type)) {
            ret = NURSE;
        }
        return ret;
    }

    public static ConsultationCreator parseInt(int type) {
        ConsultationCreator ret = null;
        if (USER.ordinal() == type) {
            ret = USER;
        }
        else if (NURSE.ordinal() == type) {
            ret = NURSE;
        }
        return ret;
    }
}
