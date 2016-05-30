package com.cooltoo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/5/30.
 */
public enum RelationshipType {
    OTHER,// 其他
    BLOCK_ALL_SPEAK // 屏蔽
    ;

    public static RelationshipType parseString(String relationship) {
        RelationshipType type = null;
        if (BLOCK_ALL_SPEAK.name().equalsIgnoreCase(relationship)) {
            type = BLOCK_ALL_SPEAK;
        }
        return type;
    }

    public static RelationshipType parseInt(int relationship) {
        RelationshipType type = null;
        if (BLOCK_ALL_SPEAK.ordinal()==relationship) {
            type = BLOCK_ALL_SPEAK;
        }
        return type;
    }

    public static List<String> getAllType() {
        List<String> allType = new ArrayList<>();
        allType.add(BLOCK_ALL_SPEAK.name());
        return allType;
    }
}
