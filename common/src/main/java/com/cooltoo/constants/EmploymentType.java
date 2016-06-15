package com.cooltoo.constants;

/**
 * Created by hp on 2016/6/15.
 */
public enum EmploymentType {
    FULL_TIME,  //全职
    PART_TIME   //兼职
    ;

    public static EmploymentType parseString(String type) {
        EmploymentType retVal = null;
        if (FULL_TIME.name().equalsIgnoreCase(type)) {
            retVal = FULL_TIME;
        }
        else if (PART_TIME.name().equalsIgnoreCase(type)) {
            retVal = PART_TIME;
        }
        return retVal;
    }

    public static EmploymentType parseInt(int type) {
        EmploymentType retVal = null;
        if (FULL_TIME.ordinal() == type) {
            retVal = FULL_TIME;
        }
        else if (PART_TIME.ordinal() == type) {
            retVal = PART_TIME;
        }
        return retVal;
    }
}
