package com.cooltoo.constants;

/**
 * Created by zhaolisong on 16/3/22.
 */
public enum AdminUserType {
    ADMINISTRATOR, NORMAL, MANAGER;


    public static AdminUserType parseString(String userType) {
        AdminUserType retVal = null;
        if (ADMINISTRATOR.name().equalsIgnoreCase(userType)) {
            retVal = ADMINISTRATOR;
        }
        else if (MANAGER.name().equalsIgnoreCase(userType)) {
            retVal = MANAGER;
        }
        else if (NORMAL.name().equalsIgnoreCase(userType)) {
            retVal = NORMAL;
        }
        return retVal;
    }

    public static AdminUserType parseInt(int userType) {
        AdminUserType retVal = null;
        if (ADMINISTRATOR.ordinal() == userType) {
            retVal = ADMINISTRATOR;
        }
        else if (MANAGER.ordinal() == userType) {
            retVal = MANAGER;
        }
        else if (NORMAL.ordinal() == userType) {
            retVal = NORMAL;
        }
        return retVal;
    }
}
