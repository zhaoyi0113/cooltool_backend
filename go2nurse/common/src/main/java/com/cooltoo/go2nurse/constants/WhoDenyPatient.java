package com.cooltoo.go2nurse.constants;

/**
 * Created by zhaolisong on 14/12/2016.
 */
public enum WhoDenyPatient {
    VENDOR,
    NURSE
    ;

    public static WhoDenyPatient parseString(String type) {
        WhoDenyPatient ret = null;
        if (VENDOR.name().equalsIgnoreCase(type)) {
            ret = VENDOR;
        }
        else if (NURSE.name().equalsIgnoreCase(type)) {
            ret = NURSE;
        }
        return ret;
    }

    public static WhoDenyPatient parseInt(int type) {
        WhoDenyPatient ret = null;
        if (VENDOR.ordinal()==type) {
            ret = VENDOR;
        }
        else if (NURSE.ordinal()==type) {
            ret = NURSE;
        }
        return ret;
    }
}
