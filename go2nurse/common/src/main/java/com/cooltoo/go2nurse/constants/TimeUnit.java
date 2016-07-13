package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/7/13.
 */
public enum TimeUnit {
    HOUR, // 小时
    DAY   // 天
    ;

    public static TimeUnit parseString(String type) {
        TimeUnit ret = null;
        if (HOUR.name().equalsIgnoreCase(type)) {
            ret = HOUR;
        } else if (DAY.name().equalsIgnoreCase(type)) {
            ret = DAY;
        }
        return ret;
    }

    public static TimeUnit parseInt(int type) {
        TimeUnit ret = null;
        if (HOUR.ordinal()==type) {
            ret = HOUR;
        } else if (DAY.ordinal()==type) {
            ret = DAY;
        }
        return ret;
    }

}
