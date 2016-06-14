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
}
