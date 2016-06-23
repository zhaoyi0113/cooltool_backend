package com.cooltoo.util;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.constants.ReadingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/3/22.
 */
public class VerifyUtil {
    public static final String CC_VIDEO_CALLBACK_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<result>OK</result>";
    public static final String ADMIN_USER_NAME_REGEXP = "[a-zA-Z_][a-zA-Z0-9_]{5,64}";
    public static final String ADMIN_USER_PASSWORD_REGEXP = "[a-z|A-Z|0-9]{32,128}";
    public static final String EMAIL_REGEXP = "^[a-zA-Z][a-zA-Z0-9_\\-]+[@][a-zA-Z_\\-\\.]+[\\.][a-zA-Z]+$";
    public static final String OCCUPATION_SKILL_ID = "[[0-9]+,]*[0-9]+";
    private static final Logger logger = LoggerFactory.getLogger(VerifyUtil.class);

    public static boolean isMapEmpty(Map map) {
        if (map instanceof Map) {
            return map.isEmpty();
        }
        return true;
    }

    public static boolean isListEmpty(List list) {
        if (list instanceof List) {
            if (list.isEmpty()) {
                return true;
            }
            else {
                int count = 0;
                for (Object obj : list) {
                    if (null==obj) {
                        count++;
                    }
                }
                return count==list.size();
            }
        }
        return true;
    }

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
        try {
            for (String tmp : strArray) {
                Integer id = Integer.parseInt(tmp);
                recordIds.add(id);
            }
        }
        catch (Exception ex) {
            recordIds.clear();
        }
        return recordIds;
    }

    public static List<Long> parseLongIds(String ids) {
        if (!isIds(ids)) {
            return new ArrayList();
        }

        String[]   strArray  = ids.split(",");
        List<Long> recordIds = new ArrayList<>();
        try {
            for (String tmp : strArray) {
                Long id = Long.parseLong(tmp);
                recordIds.add(id);
            }
        }
        catch (Exception ex) {
            recordIds.clear();
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


    private static final String JSON_REGEXP_EMPTY = "^\\{\\s*\\}$";
    private static final String JSON_REGEXP_ONE = "^\\{\\s*\"[^\\s\"]+\"\\s*:\\s*\"[^\\s\"]+\"\\s*\\}$";
    private static final String JSON_REGEXP_MORE= "^\\{((\\s*\"[^\\s]+\"\\s*:\\s*\"[^\\s]+\"\\s*)\\s*,\\s*)+\\s*\"[^\\s]+\"\\s*:\\s*\"[^\\s]+\"\\s*\\s*\\}$";
    public static boolean isJsonKeyVal(String json) {
        if (isStringEmpty(json)) {
            return false;
        }
        json = json.trim();
        boolean isEmpty = json.matches(JSON_REGEXP_EMPTY);
        boolean isOne   = json.matches(JSON_REGEXP_ONE);
        boolean isMore  = json.matches(JSON_REGEXP_MORE);
        return (isEmpty || isOne || isMore);
    }

    public static Map<String, String> parseJsonKeyVal(String json) {
        if (!isJsonKeyVal(json)) {
            return new HashMap<>();
        }

        Map<String, String> keyValMap = new HashMap<>();
        boolean isEmpty = json.matches(JSON_REGEXP_EMPTY);
        if (isEmpty) {
            return keyValMap;
        }
        boolean isOne   = json.matches(JSON_REGEXP_ONE);
        if (isOne) {
            json = json.trim();
            json = json.substring(1, json.length()-1);
            String[] keyVal = parseJsonKeyValMap(json);
            keyValMap.put(keyVal[0], keyVal[1]);
            return keyValMap;
        }
        boolean isMore  = json.matches(JSON_REGEXP_MORE);
        if (isMore) {
            json = json.trim();
            json = json.substring(1, json.length()-1);
            String[] groups = json.split(",");
            for (String tmp : groups) {
                String[] keyVal = parseJsonKeyValMap(tmp);
                keyValMap.put(keyVal[0], keyVal[1]);
            }
            return keyValMap;
        }

        return keyValMap;
    }

    private static String[] parseJsonKeyValMap(String keyValMap) {
        String[] keyVal = keyValMap.trim().split("\"\\s*:\\s*\"");
        keyVal[0] = keyVal[0].trim();
        if (keyVal[0].length()!=1) {
            keyVal[0] = keyVal[0].substring(1, keyVal[0].length());
        }
        else {
            keyVal[0] = "";
        }
        keyVal[1] = keyVal[1].trim();
        if (keyVal[1].length()!=1) {
            keyVal[1] = keyVal[1].substring(0, keyVal[1].length() - 1);
        }
        else {
            keyVal[1] = "";
        }
        return keyVal;
    }


    public static List<SpeakType> parseSpeakTypes(String speakTypes) {
        if (isStringEmpty(speakTypes)) {
            return new ArrayList<>();
        }
        speakTypes = speakTypes.toLowerCase().trim();
        String[] strArray = speakTypes.split(",");

        List<SpeakType> types = new ArrayList<>();
        for (String tmp : strArray) {
            SpeakType type = SpeakType.parseString(tmp);
            if (null!=type) {
                types.add(type);
            }
        }

        return types;
    }

    public static List<ReadingStatus> parseSuggestionStatuses(String statuses) {
        if (isStringEmpty(statuses)) {
            return new ArrayList<>();
        }
        statuses = statuses.toLowerCase().trim();
        String[] strArray = statuses.split(",");

        List<ReadingStatus> types = new ArrayList<>();
        for (String tmp : strArray) {
            ReadingStatus type = ReadingStatus.parseString(tmp);
            if (null!=type) {
                types.add(type);
            }
        }

        return types;
    }

    public static List<CommonStatus> parseCommonStatus(String statuses) {
        if (isStringEmpty(statuses)) {
            return new ArrayList<>();
        }
        statuses = statuses.toLowerCase().trim();
        String[] strArray = statuses.split(",");

        List<CommonStatus> commonStatuses  = new ArrayList<>();
        for (String tmp : strArray) {
            CommonStatus status = CommonStatus.parseString(tmp);
            if (null!=status) {
                commonStatuses.add(status);
            }
        }

        return commonStatuses;
    }

    public static String reconstructSQLContentLike(String contentLike) {
        if (VerifyUtil.isStringEmpty(contentLike)) {
            contentLike = "%";
        }
        else {
            contentLike = contentLike.trim();
            StringBuilder fuzzyContent = new StringBuilder("%");
            for (int i=0, count=contentLike.length(); i < count; i ++) {
                fuzzyContent.append(contentLike.charAt(i)).append("%");
            }
            contentLike = fuzzyContent.toString();
        }
        return contentLike;
    }

    private static final char SPEAK_TOPIC_MARK = '#';
    public static List<String> parseSpeakTopic(String speakContent) {
        List<String> topics = new ArrayList<>();
        if (!isStringEmpty(speakContent)) {
            int preMarkIndex = -1;
            char[] chars = speakContent.toCharArray();
            for (int i = 0, count = chars.length; i < count; i++) {
                char currentChar = chars[i];
                if (SPEAK_TOPIC_MARK==currentChar) {
                    // topic begin
                    if (preMarkIndex<0) {
                        preMarkIndex = i;
                    }
                    // topic end
                    else {
                        if (preMarkIndex+1==i) {
                            // empty topic
                        }
                        else {
                            String topic = speakContent.substring(preMarkIndex+1, i);
                            topic = topic.trim();
                            if (!topics.contains(topic)) {
                                topics.add(topic);
                            }
                        }
                        preMarkIndex = -1;
                    }
                }
            }
        }
        return topics;
    }
}
