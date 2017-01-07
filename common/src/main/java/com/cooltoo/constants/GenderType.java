package com.cooltoo.constants;

/**
 * Created by hp on 2016/3/24.
 */
public enum GenderType {
    FEMALE, MALE, SECRET;

    public static GenderType parseString(String gender) {
        GenderType retVal = null;
        if (FEMALE.name().equalsIgnoreCase(gender)) {
            retVal = FEMALE;
        }
        else if (MALE.name().equalsIgnoreCase(gender)) {
            retVal = MALE;
        }
        else if (SECRET.name().equalsIgnoreCase(gender)) {
            retVal = SECRET;
        }
        return retVal;
    }

    public static GenderType parseInt(int gender) {
        GenderType retVal = null;
        if (FEMALE.ordinal() == gender) {
            retVal = FEMALE;
        }
        else if (MALE.ordinal() == gender) {
            retVal = MALE;
        }
        else if (SECRET.ordinal() == gender) {
            retVal = SECRET;
        }
        return retVal;
    }

    public static String genderInfo(GenderType genderType) {
        if (FEMALE.equals(genderType)) {
            return "女";
        }
        else if (MALE.equals(genderType)) {
            return "男";
        }
        else if (SECRET.equals(genderType)) {
            return "保密";
        }
        else {
            return "保密";
        }
    }
}
