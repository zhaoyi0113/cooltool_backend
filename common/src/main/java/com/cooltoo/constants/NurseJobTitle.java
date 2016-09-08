package com.cooltoo.constants;

public enum NurseJobTitle {
    NOT_SETTING("未设置"),
    NURSING_EXPERT("护理专家")
    ;

    private String description;
    private NurseJobTitle(String description) {
        this.description = description;
    }


    public static NurseJobTitle parseString(String type) {
        NurseJobTitle retVal = null;
        if (NURSING_EXPERT.name().equalsIgnoreCase(type)) {
            retVal = NURSING_EXPERT;
        }
        else if (NOT_SETTING.name().equalsIgnoreCase(type)) {
            retVal = NOT_SETTING;
        }
        return retVal;
    }

    public static NurseJobTitle parseInt(int type) {
        NurseJobTitle retVal = null;
        if (NURSING_EXPERT.ordinal() == type) {
            retVal = NURSING_EXPERT;
        }
        else if (NOT_SETTING.ordinal() == type) {
            retVal = NOT_SETTING;
        }
        return retVal;
    }
}