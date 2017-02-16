package com.cooltoo.go2nurse.payment;

import java.util.Map;

/**
 * Created by zhaolisong on 26/01/2017.
 */
public interface IPayment {

    public static final String RETURN_CODE    = "return_code";  /* SUCCESS, FAIL*/
    public static final String CODE_SUCCESS   = "success";
    public static final String CODE_FAIL      = "fail";
    public static final String RETURN_VALUE   = "return_value"; /* Java Instance */
    public static final String RETURN_MESSAGE = "return_msg";   /* Message Of Return */
    public static final String WEB_HOOK_BODY  = "web_hooks_body";

    String createSign(Map paramenters);

    boolean checkSign(Map paramenters);

    Map pay(Map parameters);

    Map checkPayment(Map parameters);

    Map refund(Map parameters);

    Map checkRefund(Map parameters);

    Map close(Map parameters);

    Map processNotify(Map parameters);

    Map processReturnValue(Map parameters);
}
