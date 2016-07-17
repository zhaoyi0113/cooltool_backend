package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/7/15.
 */
public enum AppType {
    NURSE_GO, // 护士说
    GO_2_NURSE, // 全时护理
    ;

    public static AppType parseString(String type) {
        AppType ret = null;
        if (NURSE_GO.name().equalsIgnoreCase(type)) {
            ret = NURSE_GO;
        }
        else if (GO_2_NURSE.name().equalsIgnoreCase(type)) {
            ret = GO_2_NURSE;
        }
        return ret;
    }

    public static AppType parseInt(int type) {
        AppType ret = null;
        if (NURSE_GO.ordinal() == type) {
            ret = NURSE_GO;
        }
        else if (GO_2_NURSE.ordinal() == type) {
            ret = GO_2_NURSE;
        }
        return ret;
    }
}
