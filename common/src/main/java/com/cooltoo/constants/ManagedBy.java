package com.cooltoo.constants;

/**
 * Created by zhaolisong on 28/12/2016.
 */
public enum ManagedBy {
    COOLTOO,
    SELF;

    public static ManagedBy parseString(String type) {
        if (COOLTOO.name().equalsIgnoreCase(type)) {
            return COOLTOO;
        }
        else if (SELF.name().equalsIgnoreCase(type)) {
            return SELF;
        }
        return null;
    }

    public static ManagedBy parseInt(int type) {
        if (COOLTOO.ordinal()==(type)) {
            return COOLTOO;
        }
        else if (SELF.ordinal()==type) {
            return SELF;
        }
        return null;
    }

}
