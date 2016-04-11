package com.cooltoo.constants;

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
}
