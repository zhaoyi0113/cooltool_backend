package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/9/7.
 */
public enum AdvertisementType {
    CONSULTATION,  // 咨询
    APPOINTMENT    // 预约
    ;


    public static AdvertisementType parseString(String type) {
        AdvertisementType ret = null;
        if (CONSULTATION.name().equalsIgnoreCase(type)) {
            ret = CONSULTATION;
        }
        else if (APPOINTMENT.name().equalsIgnoreCase(type)) {
            ret = APPOINTMENT;
        }
        return ret;
    }

    public static AdvertisementType parseInt(int type) {
        AdvertisementType ret = null;
        if (CONSULTATION.ordinal() == type) {
            ret = CONSULTATION;
        }
        else if (APPOINTMENT.ordinal() == type) {
            ret = APPOINTMENT;
        }
        return ret;
    }
}
