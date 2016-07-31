package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/7/31.
 */
public enum ChargeStatus {
    CHARGE_CREATED, // charge 对象创建成功
    CHARGE_SUCCEED // 订单支付成功
    ;


    public static ChargeStatus parseString(String type) {
        ChargeStatus ret = null;
        if (CHARGE_CREATED.name().equalsIgnoreCase(type)) {
            ret = CHARGE_CREATED;
        }
        else if (CHARGE_SUCCEED.name().equalsIgnoreCase(type)) {
            ret = CHARGE_SUCCEED;
        }
        return ret;
    }

    public static ChargeStatus parseInt(int type) {
        ChargeStatus ret = null;
        if (CHARGE_CREATED.ordinal() == type) {
            ret = CHARGE_CREATED;
        }
        else if (CHARGE_SUCCEED.ordinal() == type) {
            ret = CHARGE_SUCCEED;
        }
        return ret;
    }
}
