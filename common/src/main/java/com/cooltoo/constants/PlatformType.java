package com.cooltoo.constants;

/**
 * Created by yzzhao on 5/22/16.
 */
public enum PlatformType {
    IOS,ANDROID,BACKEND_SERVICE,BACKEN_ADMIN_WEB
    ,GO2NURSE_IOS
    ,GO2NURSE_ANDROID
    ,GO2NURSE_BACKEND_SERVICE
    ,NURSE360_IOS
    ,NURSE360_ANDROID
    ,NURSE360_BACKEND_SERVICE
    ,PATIENT_DB_IOS
    ,PATIENT_DB_ANDROID
    ,PATIENT_DB_BACKEND_SERVICE
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
        else if (NURSE360_IOS.name().equalsIgnoreCase(type)) {
            retVal = NURSE360_IOS;
        }
        else if (NURSE360_ANDROID.name().equalsIgnoreCase(type)) {
            retVal = NURSE360_ANDROID;
        }
        else if (NURSE360_BACKEND_SERVICE.name().equalsIgnoreCase(type)) {
            retVal = NURSE360_BACKEND_SERVICE;
        }
        else if (PATIENT_DB_IOS.name().equalsIgnoreCase(type)) {
            retVal = PATIENT_DB_IOS;
        }
        else if (PATIENT_DB_ANDROID.name().equalsIgnoreCase(type)) {
            retVal = PATIENT_DB_ANDROID;
        }
        else if (PATIENT_DB_BACKEND_SERVICE.name().equalsIgnoreCase(type)) {
            retVal = PATIENT_DB_BACKEND_SERVICE;
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
        else if (NURSE360_IOS.ordinal()==(type)) {
            retVal = NURSE360_IOS;
        }
        else if (NURSE360_ANDROID.ordinal()==(type)) {
            retVal = NURSE360_ANDROID;
        }
        else if (NURSE360_BACKEND_SERVICE.ordinal()==(type)) {
            retVal = NURSE360_BACKEND_SERVICE;
        }
        else if (PATIENT_DB_IOS.ordinal()==(type)) {
            retVal = PATIENT_DB_IOS;
        }
        else if (PATIENT_DB_ANDROID.ordinal()==(type)) {
            retVal = PATIENT_DB_ANDROID;
        }
        else if (PATIENT_DB_BACKEND_SERVICE.ordinal()==(type)) {
            retVal = PATIENT_DB_BACKEND_SERVICE;
        }
        return retVal;
    }
}
