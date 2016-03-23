package com.cooltoo.util;

/**
 * Created by zhaolisong on 16/3/22.
 */
public class VerifyUtil {
    public static final String ADMIN_USER_NAME_REGEXP = "[a-zA-Z_][a-zA-Z0-9_]{5,64}";
    public static final String ADMIN_USER_PASSWORD_REGEXP = "[a-z|A-Z|0-9]{32,128}";
    public static final String EMAIL_REGEXP = "^[a-zA-Z][a-zA-Z0-9_\\-]+[@][a-zA-Z_\\-\\.]+[\\.][a-zA-Z]+$";

    public static boolean isStringEmpty(String string) {
        if (string instanceof String) {
            return string.trim().isEmpty();
        }
        return true;
    }

    public static boolean isAdminUserNameValid(String adminUserName) {
        if (adminUserName instanceof String) {
            return adminUserName.matches(ADMIN_USER_NAME_REGEXP);
        }
        return false;
    }

    public static boolean isAdminUserPasswordValid(String password) {
        if (password instanceof String) {
            return password.matches(ADMIN_USER_PASSWORD_REGEXP);
        }
        return false;
    }
}
