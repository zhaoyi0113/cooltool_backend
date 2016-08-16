package com.cooltoo.constants;

/**
 * Created by hp on 2016/8/16.
 */
public enum VideoPlatform {
    CC,    // 糖豆视频
    QiNiu, // 七牛视屏
    ;

    public static VideoPlatform parseString(String type) {
        VideoPlatform ret = null;
        if (CC.name().equalsIgnoreCase(type)) {
            ret = CC;
        }
        else if (QiNiu.name().equalsIgnoreCase(type)) {
            ret = QiNiu;
        }
        return ret;
    }

    public static VideoPlatform parseInt(int type) {
        VideoPlatform ret = null;
        if (CC.ordinal() == type) {
            ret = CC;
        }
        else if (QiNiu.ordinal() == type) {
            ret = QiNiu;
        }
        return ret;
    }
}
