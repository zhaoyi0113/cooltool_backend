package com.cooltoo.go2nurse.constants;

/**
 * Created by zhaolisong on 12/12/2016.
 */
public enum WalletInOutType {
    NONE,
    ORDER_IN,
    WITHDRAW;


    public static WalletInOutType parseString(String type) {
        WalletInOutType ret = null;
        if (NONE.name().equalsIgnoreCase(type)) {
            ret = NONE;
        }
        else if (ORDER_IN.name().equalsIgnoreCase(type)) {
            ret = ORDER_IN;
        }
        else if (WITHDRAW.name().equalsIgnoreCase(type)) {
            ret = WITHDRAW;
        }
        return ret;
    }

    public static WalletInOutType parseInt(int type) {
        WalletInOutType ret = null;
        if (NONE.ordinal()==type) {
            ret = NONE;
        }
        else if (ORDER_IN.ordinal()==type) {
            ret = ORDER_IN;
        }
        else if (WITHDRAW.ordinal()==type) {
            ret = WITHDRAW;
        }
        return ret;
    }
}
