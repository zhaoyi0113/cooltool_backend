package com.cooltoo.constants;

/**
 * Created by zhaolisong on 16/3/23.
 */
public enum WorkFileType {
    IDENTIFICATION, WORK_FILE, UNKNOW;

    public static WorkFileType parseString(String fileType) {
        if (IDENTIFICATION.name().equalsIgnoreCase(fileType)) {
            return IDENTIFICATION;
        }
        else if (WORK_FILE.name().equalsIgnoreCase(fileType)) {
            return WORK_FILE;
        }
        return UNKNOW;
    }

    public static WorkFileType parseInt(int fileType) {
        if (IDENTIFICATION.ordinal()==(fileType)) {
            return IDENTIFICATION;
        }
        else if (WORK_FILE.ordinal()==(fileType)) {
            return WORK_FILE;
        }
        return UNKNOW;
    }
}
