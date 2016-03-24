package com.cooltoo.constants;

/**
 * Created by Test111 on 2016/3/17.
 */
public enum SpeakType {
    /* 臭美 */
    SMUG,
    /* 吐槽 */
    CATHART,
    /* 提问 */
    ASK_QUESTION;

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
        return retVal;
    }
}
