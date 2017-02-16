package com.cooltoo.go2nurse.payment;

/**
 * Created by zhaolisong on 14/02/2017.
 */
public class PaymentFactory {

    public static final String TYPE_PingPP = "ping++";
    public static final String TYPE_WeChat = "wechat";

    public static IPayment newPayment(String type) {
        if (TYPE_PingPP.equals(type)) {
            return new PaymentPingPP();
        }
        if (TYPE_WeChat.equals(type)) {
            return new PaymentWeChat();
        }
        return null;
    }
}
