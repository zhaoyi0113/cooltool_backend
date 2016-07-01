package com.cooltoo.constants;

/**
 * 该枚举是 speak_type 数据表的映射.
 * 数据表中的 type 字段的值与该枚举的值一一对应.
 *
 * Created by Test111 on 2016/3/17.
 */
public enum SpeakType {
    /* 晒图 */
    SMUG("晒图"),
    /* 吐槽 */
    CATHART("吐槽"),
    /* 提问 */
    ASK_QUESTION("提问"),
    /* 官方发言 */
    OFFICIAL("官方发言"),
    /* 快拍 */
    SHORT_VIDEO("快拍"),
    ;

    private String value;

    SpeakType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static SpeakType parseString(String speak) {
        SpeakType retVal = null;
        if (SMUG.name().equalsIgnoreCase(speak)) {
            retVal = SMUG;
        }
        else if (CATHART.name().equalsIgnoreCase(speak)) {
            retVal = CATHART;
        }
        else if (ASK_QUESTION.name().equalsIgnoreCase(speak)) {
            retVal = ASK_QUESTION;
        }
        else if (OFFICIAL.name().equalsIgnoreCase(speak)) {
            retVal = OFFICIAL;
        }
        else if (SHORT_VIDEO.name().equalsIgnoreCase(speak)) {
            retVal = SHORT_VIDEO;
        }
        return retVal;
    }

    public static SpeakType parseInt(int speak) {
        SpeakType retVal = null;
        if (SMUG.ordinal()==(speak)) {
            retVal = SMUG;
        }
        else if (CATHART.ordinal()==(speak)) {
            retVal = CATHART;
        }
        else if (ASK_QUESTION.ordinal()==(speak)) {
            retVal = ASK_QUESTION;
        }
        else if (OFFICIAL.ordinal()==(speak)) {
            retVal = OFFICIAL;
        }
        else if (SHORT_VIDEO.ordinal()==(speak)) {
            retVal = SHORT_VIDEO;
        }
        return retVal;
    }

    public static String allValues() {
        StringBuilder values = new StringBuilder();
        values.append(SMUG.name()).append(",");
        values.append(CATHART.name()).append(",");
        values.append(ASK_QUESTION.name()).append(",");
        values.append(OFFICIAL.name()).append(",");
        values.append(SHORT_VIDEO.name());
        return values.toString();
    }
}
