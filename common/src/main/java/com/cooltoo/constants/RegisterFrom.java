package com.cooltoo.constants;

/**
 * Created by zhaolisong on 16/9/26.
 */
public enum RegisterFrom {
    COOLTOO("护士说"),
    GO2NURSE("全时护理-护士端");

    private String application;
    public String getApplication() {
        return application;
    }

    RegisterFrom(String application) {
        this.application = application;
    }


    public static RegisterFrom parseString(String from) {
        RegisterFrom type = null;
        if (COOLTOO.name().equalsIgnoreCase(from)) {
            type = COOLTOO;
        }
        else if (GO2NURSE.name().equalsIgnoreCase(from)) {
            type = GO2NURSE;
        }
        return type;
    }

    public static RegisterFrom parseInt(int from) {
        RegisterFrom type = null;
        if (COOLTOO.ordinal()==from) {
            type = COOLTOO;
        }
        else if (GO2NURSE.ordinal()==from) {
            type = GO2NURSE;
        }
        return type;
    }
}
