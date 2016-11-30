package com.cooltoo.go2nurse.service.notification;


/**
 * Created by hp on 2016/9/14.
 */
public enum  MessageType {
    ORDER,
    CONSULTATION_TALK,
    APPOINTMENT,
    FOLLOW_UP_CONSULTATION,
    FOLLOW_UP_CONSULTATION_TALK,
    FOLLOW_UP_QUESTIONNAIRE,
    PUSH_COURSE,
    ;



    public static MessageType parseString(String type) {
        MessageType enumeration = null;
        if (ORDER.name().equalsIgnoreCase(type)) {
            enumeration = ORDER;
        }
        else if (CONSULTATION_TALK.name().equalsIgnoreCase(type)) {
            enumeration = CONSULTATION_TALK;
        }
        else if (APPOINTMENT.name().equalsIgnoreCase(type)) {
            enumeration = APPOINTMENT;
        }
        else if (FOLLOW_UP_CONSULTATION.name().equalsIgnoreCase(type)) {
            enumeration = FOLLOW_UP_CONSULTATION;
        }
        else if (FOLLOW_UP_CONSULTATION_TALK.name().equalsIgnoreCase(type)) {
            enumeration = FOLLOW_UP_CONSULTATION_TALK;
        }
        else if (FOLLOW_UP_QUESTIONNAIRE.name().equalsIgnoreCase(type)) {
            enumeration = FOLLOW_UP_QUESTIONNAIRE;
        }
        else if (PUSH_COURSE.name().equalsIgnoreCase(type)) {
            enumeration = PUSH_COURSE;
        }
        return enumeration;
    }

    public static MessageType parseInt(int type) {
        MessageType enumeration = null;
        if (ORDER.ordinal() == type) {
            enumeration = ORDER;
        }
        else if (CONSULTATION_TALK.ordinal() == type) {
            enumeration = CONSULTATION_TALK;
        }
        else if (APPOINTMENT.ordinal() == type) {
            enumeration = APPOINTMENT;
        }
        else if (FOLLOW_UP_CONSULTATION.ordinal() == type) {
            enumeration = FOLLOW_UP_CONSULTATION;
        }
        else if (FOLLOW_UP_CONSULTATION_TALK.ordinal() == type) {
            enumeration = FOLLOW_UP_CONSULTATION_TALK;
        }
        else if (FOLLOW_UP_QUESTIONNAIRE.ordinal() == type) {
            enumeration = FOLLOW_UP_QUESTIONNAIRE;
        }
        else if (PUSH_COURSE.ordinal() == type) {
            enumeration = PUSH_COURSE;
        }
        return enumeration;
    }
}
