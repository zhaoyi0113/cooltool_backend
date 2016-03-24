package com.cooltoo.constants;

/**
 * Created by zhaolisong on 16/3/23.
 */
public enum WorkFileType {
    IDENTIFICATION, WORK_FILE, UNKNOW;

    public static WorkFileType parseString(String fileType) {
        WorkFileType retVal = null;
        if (IDENTIFICATION.toString().equalsIgnoreCase(fileType)) {
            retVal = IDENTIFICATION;
        }
        else if (WORK_FILE.toString().equalsIgnoreCase(fileType)) {
            retVal = WORK_FILE;
        }
        else if (UNKNOW.toString().equalsIgnoreCase(fileType)) {
            retVal = UNKNOW;
        }
        return retVal;
    }
}
