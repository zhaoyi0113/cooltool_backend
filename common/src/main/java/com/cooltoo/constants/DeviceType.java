package com.cooltoo.constants;

/**
 * Created by hp on 2016/9/13.
 */
public enum  DeviceType {
    iOS("ios"),
    Android("android")
    ;

    DeviceType(String type) {
        deviceType = type;
    }

    private String deviceType;
    public String getDeviceType() {
        return deviceType;
    }



    public static DeviceType parseString(String type) {
        DeviceType retVal = null;
        if (iOS.name().equalsIgnoreCase(type)) {
            retVal = iOS;
        }
        else if (Android.name().equalsIgnoreCase(type)) {
            retVal = Android;
        }
        return retVal;
    }

    public static DeviceType parseInt(int type) {
        DeviceType retVal = null;
        if (iOS.ordinal() == type) {
            retVal = iOS;
        }
        else if (Android.ordinal() == type) {
            retVal = Android;
        }
        return retVal;
    }
}
