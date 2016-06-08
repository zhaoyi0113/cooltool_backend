package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/6/8.
 */
public enum CourseStatus {
      DISABLE  // 不可用
    , ENABLE   // 可用
    , EDITING  // 编辑中
    , DELETE   // 删除
    ;


    public static CourseStatus parseString(String status) {
        if (DISABLE.name().equalsIgnoreCase(status)) {
            return DISABLE;
        }
        else if (ENABLE.name().equalsIgnoreCase(status)) {
            return ENABLE;
        }
        else if (EDITING.name().equalsIgnoreCase(status)) {
            return EDITING;
        }
        else if (DELETE.name().equalsIgnoreCase(status)) {
            return DELETE;
        }
        return null;
    }
}
