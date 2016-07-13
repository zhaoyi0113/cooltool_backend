package com.cooltoo.go2nurse.constants;

/**
 * Created by hp on 2016/7/13.
 */
public enum  OrderStatus {
    CANCELLED(-1, "CANCELLED"),  // 取消订单
    TO_PAY(0, "TO_PAY"), // 等待支付
    TO_DISPATCH(1, "TO_DISPATCH"), // 等待接单
    TO_SERVICE(2, "TO_SERVICE"), // 等待服务
    IN_PROCESS(3, "IN_PROCESS"), // 服务中
    COMPLETED(4, "COMPLETED")  // 服务完成
    ;

    private String name;
    private int id;

    OrderStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static OrderStatus parseString(String type) {
        OrderStatus ret = null;
        if (TO_PAY.name.equalsIgnoreCase(type)) {
            ret = TO_PAY;
        }
        else if (TO_DISPATCH.name.equalsIgnoreCase(type)) {
            ret = TO_DISPATCH;
        }
        else if (TO_SERVICE.name.equalsIgnoreCase(type)) {
            ret = TO_SERVICE;
        }
        else if (IN_PROCESS.name.equalsIgnoreCase(type)) {
            ret = IN_PROCESS;
        }
        else if (COMPLETED.name.equalsIgnoreCase(type)) {
            ret = COMPLETED;
        }
        else if (CANCELLED.name.equalsIgnoreCase(type)) {
            ret = CANCELLED;
        }
        return ret;
    }

    public static OrderStatus parseInt(int type) {
        OrderStatus ret = null;
        if (TO_PAY.id == type) {
            ret = TO_PAY;
        }
        else if (TO_DISPATCH.id == type) {
            ret = TO_DISPATCH;
        }
        else if (TO_SERVICE.id == type) {
            ret = TO_SERVICE;
        }
        else if (IN_PROCESS.id == type) {
            ret = IN_PROCESS;
        }
        else if (COMPLETED.id == type) {
            ret = COMPLETED;
        }
        else if (CANCELLED.id == type) {
            ret = CANCELLED;
        }
        return ret;
    }
}
