package com.cooltoo.go2nurse.constants;

import com.cooltoo.util.VerifyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/7/13.
 */
public enum  OrderStatus {
    CANCELLED(-1, "CANCELLED"),  // 取消订单
    TO_PAY(0, "TO_PAY"), // 下单成功，等待支付
    TO_DISPATCH(1, "TO_DISPATCH"), // 支付成功，等待管理员提醒抢单或派单
    TO_SERVICE(2, "TO_SERVICE"), // 提醒抢单，等待护士抢单
    IN_PROCESS(3, "IN_PROCESS"), // 抢单成功(或派单成功), 上门服务
    COMPLETED(4, "COMPLETED"),  // 服务完成
    CREATE_CHARGE_FAILED(-2, "CREATE_CHARGE_FAILED") //创建订单失败
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
        else if (CREATE_CHARGE_FAILED.name.equalsIgnoreCase(type)) {
            ret = CREATE_CHARGE_FAILED;
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
        else if (CREATE_CHARGE_FAILED.id == type) {
            ret = CREATE_CHARGE_FAILED;
        }
        return ret;
    }

    public static List<OrderStatus> getAll() {
        List<OrderStatus> all = new ArrayList<>();
        all.add(OrderStatus.TO_PAY);
        all.add(OrderStatus.TO_DISPATCH);
        all.add(OrderStatus.TO_SERVICE);
        all.add(OrderStatus.IN_PROCESS);
        all.add(OrderStatus.COMPLETED);
        all.add(OrderStatus.CANCELLED);
        all.add(OrderStatus.CREATE_CHARGE_FAILED);
        return all;
    }

    public static List<OrderStatus> parseStrings(String statuses) {
        List<OrderStatus> ret = new ArrayList<>();
        if (VerifyUtil.isStringEmpty(statuses)) {
            return ret;
        }
        String[] arrStatus = statuses.split(",");
        for (String tmp : arrStatus) {
            OrderStatus one = parseString(tmp);
            if (null!=one) {
                ret.add(one);
            }
        }
        return ret;
    }
}
