package com.cooltoo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/11.
 */
public enum SuggestionStatus {
      UNREAD //未读
    , READ   //已读
    ;

    public static SuggestionStatus parseString(String type) {
        if (UNREAD.name().equalsIgnoreCase(type)) {
            return UNREAD;
        }
        else if (READ.name().equalsIgnoreCase(type)) {
            return READ;
        }
        return null;
    }

    public static List<String> getAllStatus() {
        List<String> status = new ArrayList<String>();
        status.add(UNREAD.name());
        status.add(READ.name());
        return status;
    }
}
