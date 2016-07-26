package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/7/26.
 */
public enum ProcessStatus {
    GOING,     // 进行中
    CANCELED,  // 取消
    COMPLETED; // 完成

    public static ProcessStatus parseString(String type) {
        ProcessStatus enumeration = null;
        if (GOING.name().equalsIgnoreCase(type)) {
            enumeration = GOING;
        }
        else if (CANCELED.name().equalsIgnoreCase(type)) {
            enumeration = CANCELED;
        }
        else if (COMPLETED.name().equalsIgnoreCase(type)) {
            enumeration = COMPLETED;
        }
        return enumeration;
    }

    public static ProcessStatus parseInt(int type) {
        ProcessStatus enumeration = null;
        if (GOING.ordinal() == type) {
            enumeration = GOING;
        }
        else if (CANCELED.ordinal() == type) {
            enumeration = CANCELED;
        }
        else if (COMPLETED.ordinal() == type) {
            enumeration = COMPLETED;
        }
        return enumeration;
    }
}
