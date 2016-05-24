package com.cooltoo.constants;

/**
 * Created by yzzhao on 12/23/15.
 */
public enum CommonStatus {
    DISABLED, ENABLED, DELETED;

    public static CommonStatus parseString(String status) {
        CommonStatus retVal = null;
        if (ENABLED.name().equalsIgnoreCase(status)) {
            retVal= ENABLED;
        }
        else if (DISABLED.name().equalsIgnoreCase(status)) {
            retVal = DISABLED;
        }
        else if (DELETED.name().equalsIgnoreCase(status)) {
            retVal = DELETED;
        }
        return retVal;
    }

    public static CommonStatus parseInt(int status) {
        CommonStatus retVal = null;
        if (ENABLED.ordinal() == status) {
            retVal = ENABLED;
        }
        else if (DISABLED.ordinal() == status) {
            retVal = DISABLED;
        }
        else if (DELETED.ordinal() == status) {
            retVal = DELETED;
        }
        return retVal;
    }
}
