package com.cooltoo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/3/22.
 */
public class VerifyUtil {
    public static final String ADMIN_USER_NAME_REGEXP = "[a-zA-Z_][a-zA-Z0-9_]{5,64}";
    public static final String ADMIN_USER_PASSWORD_REGEXP = "[a-z|A-Z|0-9]{32,128}";
    public static final String EMAIL_REGEXP = "^[a-zA-Z][a-zA-Z0-9_\\-]+[@][a-zA-Z_\\-\\.]+[\\.][a-zA-Z]+$";
    public static final String OCCUPATION_SKILL_ID = "[[0-9]+,]*[0-9]+";
    private static final Logger logger = LoggerFactory.getLogger(VerifyUtil.class);

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

    public static boolean isIds(String ids) {
        logger.info("get occupation skill ids "+ids);
        if (ids instanceof String) {
            return ids.matches(OCCUPATION_SKILL_ID);
        }
        return false;
    }

    public static List<Integer> parseIntIds(String ids) {
        if (!isIds(ids)) {
            return new ArrayList();
        }

        String[]      strArray  = ids.split(",");
        List<Integer> recordIds = new ArrayList<>();
        for (String tmp : strArray) {
            Integer id = Integer.parseInt(tmp);
            recordIds.add(id);
        }
        return recordIds;
    }

    public static List<Long> parseLongIds(String ids) {
        if (!isIds(ids)) {
            return new ArrayList();
        }

        String[]   strArray  = ids.split(",");
        List<Long> recordIds = new ArrayList<>();
        for (String tmp : strArray) {
            Long id = Long.parseLong(tmp);
            recordIds.add(id);
        }
        return recordIds;
    }

    public static String numList2String(List ids) {
        if (null==ids||ids.isEmpty()) {
            return "";
        }
        else if (ids.size()==1) {
            return ""+ids.get(0);
        }
        else {
            StringBuilder strIds = new StringBuilder("");
            strIds.append(ids.get(0));
            for (int i = 1; i < ids.size(); i++) {
                strIds.append(",").append(ids.get(i));
            }
            return strIds.toString();
        }
    }

    public static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1Digest = MessageDigest.getInstance("SHA1");
        byte[] result = sha1Digest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
