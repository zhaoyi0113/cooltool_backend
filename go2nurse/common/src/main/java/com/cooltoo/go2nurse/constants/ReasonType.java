package com.cooltoo.go2nurse.constants;

/**
 * Created by zhaolisong on 16/9/27.
 */
public enum ReasonType {
    CONSULTATION, // 咨询
    ORDER,        // 订单
    APPOINTMENT  // 预约
    ;

    public static ReasonType parseString(String type) {
        ReasonType retVal = null;
        if (CONSULTATION.name().equalsIgnoreCase(type)) {
            retVal = CONSULTATION;
        }
        else if (ORDER.name().equalsIgnoreCase(type)) {
            retVal = ORDER;
        }
        else if (APPOINTMENT.name().equalsIgnoreCase(type)) {
            retVal = APPOINTMENT;
        }
        return retVal;
    }

    public static ReasonType parseInt(int type) {
        ReasonType retVal = null;
        if (CONSULTATION.ordinal() == type) {
            retVal = CONSULTATION;
        }
        else if (ORDER.ordinal() == type) {
            retVal = ORDER;
        }
        else if (APPOINTMENT.ordinal() == type) {
            retVal = APPOINTMENT;
        }
        return retVal;
    }
}
