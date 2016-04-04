package com.cooltoo.constants;

/**
 * Created by hp on 2016/4/4.
 */
public enum AgreeType {
      WAITING          //等待验证
    , AGREED           //同意
    , BLACKLIST        //黑名单
    , ACCESS_ZONE_DENY //拒绝访问空间
    ;

    public static AgreeType parseString(String agreeType) {
        if (WAITING.name().equalsIgnoreCase(agreeType)) {
            return WAITING;
        }
        else if (AGREED.name().equalsIgnoreCase(agreeType)) {
            return AGREED;
        }
        else if (BLACKLIST.name().equalsIgnoreCase(agreeType)) {
            return BLACKLIST;
        }
        else if (ACCESS_ZONE_DENY.name().equalsIgnoreCase(agreeType)) {
            return ACCESS_ZONE_DENY;
        }
        return null;
    }

    public static AgreeType parseInt(int agreeType) {
        if (WAITING.ordinal() == agreeType) {
            return WAITING;
        }
        else if (AGREED.ordinal() == agreeType) {
            return AGREED;
        }
        else if (BLACKLIST.ordinal() == agreeType) {
            return BLACKLIST;
        }
        else if (ACCESS_ZONE_DENY.ordinal() == agreeType) {
            return ACCESS_ZONE_DENY;
        }
        return null;
    }
}
