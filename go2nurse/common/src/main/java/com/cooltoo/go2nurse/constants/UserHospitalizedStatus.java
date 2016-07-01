package com.cooltoo.go2nurse.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/7/1.
 */
public enum UserHospitalizedStatus {
    NONE, // 无选择
    IN_HOME, // 居家
    IN_HOSPITAL // 住院
    ;


    public static UserHospitalizedStatus parseString(String type) {
        UserHospitalizedStatus enumeration = null;
        if (NONE.name().equalsIgnoreCase(type)) {
            enumeration = NONE;
        }
        else if (IN_HOME.name().equalsIgnoreCase(type)) {
            enumeration = IN_HOME;
        }
        else if (IN_HOSPITAL.name().equalsIgnoreCase(type)) {
            enumeration = IN_HOSPITAL;
        }
        return enumeration;
    }

    public static UserHospitalizedStatus parseInt(int type) {
        UserHospitalizedStatus enumeration = null;
        if (NONE.ordinal() == type) {
            enumeration = NONE;
        }
        else if (IN_HOME.ordinal() == type) {
            enumeration = IN_HOME;
        }
        else if (IN_HOSPITAL.ordinal() == type) {
            enumeration = IN_HOSPITAL;
        }
        return enumeration;
    }

    public static boolean exists(int type) {
        return null!=parseInt(type);
    }

    public static List<UserHospitalizedStatus> getAll() {
        List<UserHospitalizedStatus> enumerations = new ArrayList<>();
        enumerations.add(NONE);
        enumerations.add(IN_HOME);
        enumerations.add(IN_HOSPITAL);
        return enumerations;
    }
}
