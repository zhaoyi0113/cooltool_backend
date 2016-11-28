package com.cooltoo.nurse360.hospital.util;

import com.cooltoo.constants.AdminUserType;

/**
 * Created by zhaolisong on 2016/11/28.
 */
public class TokenUtil {

    public static TokenUtil newInstance() {
        return new TokenUtil();
    }

    public String constructToken(String token, AdminUserType adminUserType) {
        if (null!=token && null!=adminUserType && token.trim().length()>0) {
            return token+"_"+adminUserType;
        }
        return "";
    }

    public String getToken(String tokenInHttpHeader) {
        int indexOfSplitter = null==tokenInHttpHeader ? -1 : tokenInHttpHeader.lastIndexOf('_');
        if (0>=indexOfSplitter) {
            return null;
        }
        String token = tokenInHttpHeader.substring(0, indexOfSplitter);
        return token;

    }

    public AdminUserType getAdminType(String tokenInHttpHeader) {
        int indexOfSplitter = null==tokenInHttpHeader ? -1 : tokenInHttpHeader.lastIndexOf('_');
        if (0>=indexOfSplitter || indexOfSplitter+1>=tokenInHttpHeader.length()) {
            return null;
        }
        String userType = tokenInHttpHeader.substring(indexOfSplitter+1);
        return AdminUserType.parseString(userType);
    }
}
