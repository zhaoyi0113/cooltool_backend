package com.cooltoo.constants;

import com.cooltoo.util.VerifyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/11.
 */
public enum ReadingStatus {
      UNREAD //未读
    , READ   //已读
    , DELETED //已删除
    ;

    public static ReadingStatus parseString(String type) {
        if (UNREAD.name().equalsIgnoreCase(type)) {
            return UNREAD;
        }
        else if (READ.name().equalsIgnoreCase(type)) {
            return READ;
        }
        else if (DELETED.name().equalsIgnoreCase(type)) {
            return DELETED;
        }
        return null;
    }

    public static ReadingStatus parseInt(int type) {
        if (UNREAD.ordinal() == type) {
            return UNREAD;
        }
        else if (READ.ordinal() == type) {
            return READ;
        }
        else if (DELETED.ordinal() == type) {
            return DELETED;
        }
        return null;
    }

    public static List<String> getAllStatusString() {
        List<String> status = new ArrayList<String>();
        status.add(UNREAD.name());
        status.add(READ.name());
        status.add(DELETED.name());
        return status;
    }

    public static List<ReadingStatus> getAllStatus() {
        List<ReadingStatus> status = new ArrayList<>();
        status.add(UNREAD);
        status.add(READ);
        status.add(DELETED);
        return status;
    }

    public static List<ReadingStatus> parseStatuses(String statuses) {
        if (VerifyUtil.isStringEmpty(statuses)) {
            return new ArrayList<>();
        }
        statuses = statuses.toLowerCase().trim();
        String[] strArray = statuses.split(",");

        List<ReadingStatus> readingStatuses  = new ArrayList<>();
        for (String tmp : strArray) {
            ReadingStatus status = parseString(tmp);
            if (null!=status) {
                readingStatuses.add(status);
            }
        }

        return readingStatuses;
    }
}
