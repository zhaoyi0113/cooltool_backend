package com.cooltoo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 该枚举是 workfile_type 数据表的映射.
 * 数据表中的 type 字段的值与该枚举的值一一对应.
 *
 * Created by zhaolisong on 16/3/23.
 */
public enum WorkFileType {
    OTHER
    ,IDENTIFICATION //身份证
    ,EMPLOYEES_CARD //工作证
    ,QUALIFICATION  //资格证
    ;


    public static WorkFileType parseString(String fileType) {
        if (IDENTIFICATION.name().equalsIgnoreCase(fileType)) {
            return IDENTIFICATION;
        }
        else if (EMPLOYEES_CARD.name().equalsIgnoreCase(fileType)) {
            return EMPLOYEES_CARD;
        }
//        else if (QUALIFICATION.name().equalsIgnoreCase(fileType)) {
//            return QUALIFICATION;
//        }
        if (OTHER.name().equalsIgnoreCase(fileType)) {
            return OTHER;
        }

        return null;
    }

    public static WorkFileType parseInt(int fileType) {
        if (IDENTIFICATION.ordinal()==(fileType)) {
            return IDENTIFICATION;
        }
        else if (EMPLOYEES_CARD.ordinal()==(fileType)) {
            return EMPLOYEES_CARD;
        }
//        else if (QUALIFICATION.ordinal()==(fileType)) {
//            return QUALIFICATION;
//        }
        else if (OTHER.ordinal()==(fileType)) {
            return OTHER;
        }
        return null;
    }


    public static List<String> getAllValues() {
        List<String> allEnums = new ArrayList<String>();
        allEnums.add(IDENTIFICATION.name());
        allEnums.add(EMPLOYEES_CARD.name());
        //allEnums.add(QUALIFICATION.name());
        allEnums.add(OTHER.name());
        return allEnums;
    }
}
