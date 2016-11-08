package com.cooltoo.go2nurse.constants;

/**
 * Created by zhaolisong on 2016/11/8.
 */
public enum ConsultationReason {
    CONSULTATION,
    PATIENT_FOLLOW_UP;

    public static ConsultationReason parseString(String type) {
        ConsultationReason ret = null;
        if (CONSULTATION.name().equalsIgnoreCase(type)) {
            ret = CONSULTATION;
        }
        else if (PATIENT_FOLLOW_UP.name().equalsIgnoreCase(type)) {
            ret = PATIENT_FOLLOW_UP;
        }
        return ret;
    }

    public static ConsultationReason parseInt(int type) {
        ConsultationReason ret = null;
        if (CONSULTATION.ordinal() == type) {
            ret = CONSULTATION;
        }
        else if (PATIENT_FOLLOW_UP.ordinal() == type) {
            ret = PATIENT_FOLLOW_UP;
        }
        return ret;
    }
}
