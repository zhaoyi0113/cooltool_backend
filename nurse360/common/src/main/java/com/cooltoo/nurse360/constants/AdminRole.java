package com.cooltoo.nurse360.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/14.
 */
public enum AdminRole {
    ADMIN,
    MANAGER,
    NURSE
    ;

    public static AdminRole parseString(String type) {
        AdminRole ret = null;
        if (ADMIN.name().equalsIgnoreCase(type)) {
            ret = ADMIN;
        }
        else if (MANAGER.name().equalsIgnoreCase(type)) {
            ret = MANAGER;
        }
        else if (NURSE.name().equalsIgnoreCase(type)) {
            ret = NURSE;
        }
        return ret;
    }

    public static AdminRole parseInt(int type) {
        AdminRole ret = null;
        if (ADMIN.ordinal() == type) {
            ret = ADMIN;
        }
        else if (MANAGER.ordinal() == type) {
            ret = MANAGER;
        }
        else if (NURSE.ordinal() == type) {
            ret = NURSE;
        }
        return ret;
    }

    public static List<AdminRole> getAll() {
        List<AdminRole> roles = new ArrayList<>();
        roles.add(ADMIN);
        roles.add(MANAGER);
        roles.add(NURSE);
        return roles;
    }
}
