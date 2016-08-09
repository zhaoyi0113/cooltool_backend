package com.cooltoo.constants;

/**
 * Created by yzzhao on 5/22/16.
 */
public enum PlatformType {
    IOS,ANDROID,BACKEND_SERVICE,BACKEN_ADMIN_WEB
    ,GO2NURSE_IOS
    ,GO2NURSE_ANDROID
    ,GO2NURSE_BACKEND_SERVICE
    ;

    public static PlatformType parseString(String type) {
        PlatformType retVal = null;
        if (IOS.name().equalsIgnoreCase(type)) {
            retVal = IOS;
        }
        else if (ANDROID.name().equalsIgnoreCase(type)) {
            retVal = ANDROID;
        }
        else if (BACKEND_SERVICE.name().equalsIgnoreCase(type)) {
            retVal = BACKEND_SERVICE;
        }
        else if (BACKEN_ADMIN_WEB.name().equalsIgnoreCase(type)) {
            retVal = BACKEN_ADMIN_WEB;
        }
        else if (GO2NURSE_IOS.name().equalsIgnoreCase(type)) {
            retVal = GO2NURSE_IOS;
        }
        else if (GO2NURSE_ANDROID.name().equalsIgnoreCase(type)) {
            retVal = GO2NURSE_ANDROID;
        }
        else if (GO2NURSE_BACKEND_SERVICE.name().equalsIgnoreCase(type)) {
            retVal = GO2NURSE_BACKEND_SERVICE;
        }
        return retVal;
    }

    public static PlatformType parseString(int type) {
        PlatformType retVal = null;
        if (IOS.ordinal()==(type)) {
            retVal = IOS;
        }
        else if (ANDROID.ordinal()==(type)) {
            retVal = ANDROID;
        }
        else if (BACKEND_SERVICE.ordinal()==(type)) {
            retVal = BACKEND_SERVICE;
        }
        else if (BACKEN_ADMIN_WEB.ordinal()==(type)) {
            retVal = BACKEN_ADMIN_WEB;
        }
        else if (GO2NURSE_IOS.ordinal()==(type)) {
            retVal = GO2NURSE_IOS;
        }
        else if (GO2NURSE_ANDROID.ordinal()==(type)) {
            retVal = GO2NURSE_ANDROID;
        }
        else if (GO2NURSE_BACKEND_SERVICE.ordinal()==(type)) {
            retVal = GO2NURSE_BACKEND_SERVICE;
        }
        return retVal;
    }
}
