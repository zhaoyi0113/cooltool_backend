package com.cooltoo.constants;

/**
 * Created by zhaolisong on 16/3/22.
 */
public enum AdminUserType {
    ADMINISTRATOR, NORMAL;


    public static AdminUserType parseString(String userType) {
        AdminUserType retVal = NORMAL;
        if (ADMINISTRATOR.name().equalsIgnoreCase(userType)) {
            retVal = ADMINISTRATOR;
        }
        else if (NORMAL.name().equalsIgnoreCase(userType)) {
            retVal = NORMAL;
        }
        return retVal;
    }

    public static AdminUserType parseInt(int userType) {
        AdminUserType retVal = NORMAL;
        if (ADMINISTRATOR.ordinal() == userType) {
            retVal = ADMINISTRATOR;
        }
        else if (NORMAL.ordinal() == userType) {
            retVal = NORMAL;
        }
        return retVal;
    }
}
