package com.cooltoo.go2nurse.constants;

/**
 * Created by zhaolisong on 13/12/2016.
 */
public enum WalletProcess {
    COMPLETED,
    PROCESSING;


    public static WalletProcess parseString(String type) {
        WalletProcess ret = null;
        if (COMPLETED.name().equalsIgnoreCase(type)) {
            ret = COMPLETED;
        }
        else if (PROCESSING.name().equalsIgnoreCase(type)) {
            ret = PROCESSING;
        }
        return ret;
    }

    public static WalletProcess parseInt(int type) {
        WalletProcess ret = null;
        if (COMPLETED.ordinal()==type) {
            ret = COMPLETED;
        }
        else if (PROCESSING.ordinal()==type) {
            ret = PROCESSING;
        }
        return ret;
    }
}
