package com.cooltoo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/12.
 */
public enum UserAuthority {
      DENY_ALL    //禁止所有访问
    , AGREE_ALL   //授权所有访问
    ;

    public static UserAuthority parseString(String authority) {
        if (DENY_ALL.name().equalsIgnoreCase(authority)) {
            return DENY_ALL;
        }
        else if (AGREE_ALL.name().equalsIgnoreCase(authority)) {
            return AGREE_ALL;
        }
        return null;
    }

    public static UserAuthority parseInt(int authority) {
        if (DENY_ALL.ordinal()==authority) {
            return DENY_ALL;
        }
        else if (AGREE_ALL.ordinal()==authority) {
            return AGREE_ALL;
        }
        return null;
    }

    public static List<String> getUserAuthority() {
        List<String> enumes = new ArrayList<>();
        enumes.add(DENY_ALL.name());
        enumes.add(AGREE_ALL.name());
        return enumes;
    }
}
