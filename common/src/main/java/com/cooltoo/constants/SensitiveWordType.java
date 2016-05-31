package com.cooltoo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/5/31.
 */
public enum SensitiveWordType {
    OTHER, // 其他
    ADD_CONTENT_FORBIDDEN // 禁止添加内容
    ;

    public static SensitiveWordType parseString(String sensitiveWordType) {
        SensitiveWordType type = null;
        if (OTHER.name().equalsIgnoreCase(sensitiveWordType)) {
            type = OTHER;
        }
        else if (ADD_CONTENT_FORBIDDEN.name().equalsIgnoreCase(sensitiveWordType)) {
            type = ADD_CONTENT_FORBIDDEN;
        }
        return type;
    }

    public static SensitiveWordType parseInt(int sensitiveWordType) {
        SensitiveWordType type = null;
        if (OTHER.ordinal()==sensitiveWordType) {
            type = OTHER;
        }
        else if (ADD_CONTENT_FORBIDDEN.ordinal()==sensitiveWordType) {
            type = ADD_CONTENT_FORBIDDEN;
        }
        return type;
    }

    public static List<String> getAllType() {
        List<String> allType = new ArrayList<>();
        allType.add(ADD_CONTENT_FORBIDDEN.name());
        allType.add(OTHER.name());
        return allType;
    }
}
