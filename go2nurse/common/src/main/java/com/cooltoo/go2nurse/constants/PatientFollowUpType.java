package com.cooltoo.go2nurse.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/8.
 */
public enum PatientFollowUpType {
    CONSULTATION, /* 护士随访: 提问 */
    QUESTIONNAIRE /* 护士随访: 发问卷 */
    ;

    public static PatientFollowUpType parseString(String type) {
        PatientFollowUpType enumeration = null;
        if (CONSULTATION.name().equalsIgnoreCase(type)) {
            enumeration = CONSULTATION;
        }
        else if (QUESTIONNAIRE.name().equalsIgnoreCase(type)) {
            enumeration = QUESTIONNAIRE;
        }
        return enumeration;
    }

    public static PatientFollowUpType parseInt(int type) {
        PatientFollowUpType enumeration = null;
        if (CONSULTATION.ordinal() == type) {
            enumeration = CONSULTATION;
        }
        else if (QUESTIONNAIRE.ordinal() == type) {
            enumeration = QUESTIONNAIRE;
        }
        return enumeration;
    }

    public static boolean exists(int type) {
        return null!=parseInt(type);
    }

    public static List<PatientFollowUpType> getAll() {
        List<PatientFollowUpType> enumerations = new ArrayList<>();
        enumerations.add(CONSULTATION);
        enumerations.add(QUESTIONNAIRE);
        return enumerations;
    }
}
