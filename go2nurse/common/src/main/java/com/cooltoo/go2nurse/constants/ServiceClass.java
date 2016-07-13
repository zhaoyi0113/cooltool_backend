package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/7/13.
 */
public enum  ServiceClass {
    STANDARD, // 小时
    ADVANCED   // 天
    ;

    public static ServiceClass parseString(String type) {
        ServiceClass ret = null;
        if (STANDARD.name().equalsIgnoreCase(type)) {
            ret = STANDARD;
        } else if (ADVANCED.name().equalsIgnoreCase(type)) {
            ret = ADVANCED;
        }
        return ret;
    }

    public static ServiceClass parseInt(int type) {
        ServiceClass ret = null;
        if (STANDARD.ordinal()==type) {
            ret = STANDARD;
        } else if (ADVANCED.ordinal()==type) {
            ret = ADVANCED;
        }
        return ret;
    }
}
