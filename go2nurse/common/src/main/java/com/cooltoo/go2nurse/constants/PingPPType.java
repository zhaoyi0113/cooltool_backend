package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/7/15.
 */
public enum PingPPType {
    REFUND, // 退款
    CHARGE, // 支付
    TRANSFER, // 企业转账
    ;

    public static PingPPType parseString(String type) {
        PingPPType ret = null;
        if (REFUND.name().equalsIgnoreCase(type)) {
            ret = REFUND;
        }
        else if (CHARGE.name().equalsIgnoreCase(type)) {
            ret = CHARGE;
        }
        else if (TRANSFER.name().equalsIgnoreCase(type)) {
            ret = TRANSFER;
        }
        return ret;
    }

    public static PingPPType parseInt(int type) {
        PingPPType ret = null;
        if (REFUND.ordinal() == type) {
            ret = REFUND;
        }
        else if (CHARGE.ordinal() == type) {
            ret = CHARGE;
        }
        else if (TRANSFER.ordinal() == type) {
            ret = TRANSFER;
        }
        return ret;
    }
}
