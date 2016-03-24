package com.cooltoo.constants;

/**
 * Created by zhaolisong on 16/3/23.
 */
public enum VetStatus {
    NEED_UPLOAD, WAITING, COMPLETED, FAILED;

    public static VetStatus parseString(String status) {
        VetStatus retVal = null;
        if (NEED_UPLOAD.toString().equalsIgnoreCase(status)) {
            retVal = NEED_UPLOAD;
        }
        else if (WAITING.toString().equalsIgnoreCase(status)) {
            retVal = WAITING;
        }
        else if (COMPLETED.toString().equalsIgnoreCase(status)) {
            retVal = COMPLETED;
        }
        else if (FAILED.toString().equalsIgnoreCase(status)) {
            retVal = FAILED;
        }
        return retVal;
    }
}
