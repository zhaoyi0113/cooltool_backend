package com.cooltoo.constants;

/**
 * Created by yzzhao on 12/23/15.
 */
public enum CommonStatus {
    DISABLED, ENABLED;

    public static CommonStatus parseString(String status) {
        CommonStatus retVal = DISABLED;
        if (ENABLED.name().equalsIgnoreCase(status)) {
            retVal= ENABLED;
        }
        else if (DISABLED.name().equalsIgnoreCase(status)) {
            retVal = DISABLED;
        }
        return retVal;
    }

    public static CommonStatus parseInt(int status) {
        CommonStatus retVal = DISABLED;
        if (ENABLED.ordinal() == status) {
            retVal = ENABLED;
        }
        else if (DISABLED.ordinal() == status) {
            retVal = DISABLED;
        }
        return retVal;
    }
}
