package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/8/28.
 */
public enum ConsultationTalkStatus {
    NONE,
    USER_SPEAK,
    NURSE_SPEAK,
    ADMIN_SPEAK;

    public static ConsultationTalkStatus parseString(String type) {
        ConsultationTalkStatus ret = null;
        if (NONE.name().equalsIgnoreCase(type)) {
            ret = NONE;
        }
        else if (USER_SPEAK.name().equalsIgnoreCase(type)) {
            ret = USER_SPEAK;
        }
        else if (NURSE_SPEAK.name().equalsIgnoreCase(type)) {
            ret = NURSE_SPEAK;
        }
        else if (ADMIN_SPEAK.name().equalsIgnoreCase(type)) {
            ret = ADMIN_SPEAK;
        }
        return ret;
    }

    public static ConsultationTalkStatus parseInt(int type) {
        ConsultationTalkStatus ret = null;
        if (NONE.ordinal() == type) {
            ret = NONE;
        }
        else if (USER_SPEAK.ordinal() == type) {
            ret = USER_SPEAK;
        }
        else if (NURSE_SPEAK.ordinal() == type) {
            ret = NURSE_SPEAK;
        }
        else if (ADMIN_SPEAK.ordinal() == type) {
            ret = ADMIN_SPEAK;
        }
        return ret;
    }
}
