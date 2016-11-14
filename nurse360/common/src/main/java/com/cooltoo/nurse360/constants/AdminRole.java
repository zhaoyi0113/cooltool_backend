package com.cooltoo.nurse360.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/14.
 */
public enum AdminRole {
    ADMIN,
    USER
    ;

    public static AdminRole parseString(String type) {
        AdminRole ret = null;
        if (ADMIN.name().equalsIgnoreCase(type)) {
            ret = ADMIN;
        }
        else if (USER.name().equalsIgnoreCase(type)) {
            ret = USER;
        }
        return ret;
    }

    public static AdminRole parseInt(int type) {
        AdminRole ret = null;
        if (ADMIN.ordinal() == type) {
            ret = ADMIN;
        }
        else if (USER.ordinal() == type) {
            ret = USER;
        }
        return ret;
    }

    public static List<AdminRole> getAll() {
        List<AdminRole> roles = new ArrayList<>();
        roles.add(ADMIN);
        roles.add(USER);
        return roles;
    }
}
