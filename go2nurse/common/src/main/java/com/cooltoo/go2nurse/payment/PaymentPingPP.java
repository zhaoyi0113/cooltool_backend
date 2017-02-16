package com.cooltoo.go2nurse.payment;

import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.VerifyUtil;

import com.google.gson.JsonSyntaxException;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.*;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Refund;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 26/01/2017.
 */
public class PaymentPingPP implements IPayment {



    @Override
    public String createSign(Map paramenters) {
        return null;
    }

    @Override
    public boolean checkSign(Map paramenters) {
        return false;
    }

    @Override
    public Map pay(Map parameters) {
        Object apiKey     = parameters.remove("api_key");
        Object privateKey = parameters.remove("private_key");
        if (apiKey instanceof String) {
            Pingpp.apiKey = apiKey.toString();
        }
        if (privateKey instanceof String) {
            Pingpp.privateKey = privateKey.toString();
        }


        HashMap<String, Object> returnVal = new HashMap<>();
        try {
            //发起交易请求
            Charge charge = Charge.create(parameters);
            // 传到客户端请先转成字符串 .toString(), 调该方法，会自动转成正确的 JSON 字符串
            //logger.info("create charge is {}", charge);
            returnVal.put(RETURN_CODE, CODE_SUCCESS);
            returnVal.put(RETURN_VALUE, charge);
        } catch (PingppException e) {
            returnVal.put(RETURN_CODE, CODE_FAIL);
            returnVal.put(RETURN_VALUE, null);
            returnVal.put(RETURN_MESSAGE, e.getMessage());
        }
        return returnVal;
    }

    @Override
    public Map checkPayment(Map parameters) {
        Object apiKey   = parameters.remove("api_key");
        Object chargeId = parameters.remove("charge_id");
        if (apiKey instanceof String) {
            Pingpp.apiKey = apiKey.toString();
        }

        HashMap<String, Object> returnVal = new HashMap<>();
        try {
            Charge charge = Charge.retrieve(chargeId.toString());
            returnVal.put(RETURN_CODE, CODE_SUCCESS);
            returnVal.put(RETURN_VALUE, charge);
        } catch (Exception e) {
            returnVal.put(RETURN_CODE, CODE_FAIL);
            returnVal.put(RETURN_VALUE, null);
            returnVal.put(RETURN_MESSAGE, e.getMessage());
        }

        return returnVal;
    }

    @Override
    public Map refund(Map parameters) {
        Object apiKey   = parameters.remove("api_key");
        if (apiKey instanceof String) {
            Pingpp.apiKey = apiKey.toString();
        }

        Refund instance = new Refund();
        HashMap<String, Object> returnVal = new HashMap<>();
        try {
            Refund refund = instance.update(parameters);
            returnVal.put(RETURN_CODE, CODE_SUCCESS);
            returnVal.put(RETURN_VALUE, refund);
        } catch (Exception e) {
            returnVal.put(RETURN_CODE, CODE_FAIL);
            returnVal.put(RETURN_VALUE, null);
            returnVal.put(RETURN_MESSAGE, e.getMessage());
        }
        return returnVal;
    }

    @Override
    public Map checkRefund(Map parameters) {
        Object apiKey   = parameters.remove("api_key");
        if (apiKey instanceof String) {
            Pingpp.apiKey = apiKey.toString();
        }

        Object chargeId = parameters.get("charge_id");
        Object refundId = parameters.get("refund_id");

        HashMap<String, Object> returnVal = new HashMap<>();
        try {
            returnVal.put(RETURN_CODE, CODE_FAIL);
            returnVal.put(RETURN_VALUE, null);
            if (chargeId instanceof String) {
                Charge charge = Charge.retrieve(chargeId.toString());
                if (null!=charge && refundId instanceof String) {
                    Refund refund = charge.getRefunds().retrieve(refundId.toString());
                    returnVal.put(RETURN_CODE, CODE_SUCCESS);
                    returnVal.put(RETURN_VALUE, refund);
                }
            }
        } catch (Exception e) {
            returnVal.put(RETURN_CODE, CODE_FAIL);
            returnVal.put(RETURN_VALUE, null);
            returnVal.put(RETURN_MESSAGE, e.getMessage());
        }
        return returnVal;
    }

    @Override
    public Map close(Map parameters) {
        return null;
    }

    @Override
    public Map processNotify(Map parameters) {
        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put(RETURN_CODE, CODE_FAIL);
        returnValue.put(RETURN_VALUE, null);

        Object objNotifyBody = parameters.get(WEB_HOOK_BODY);
        if (!(objNotifyBody instanceof String)) {
            returnValue.put(RETURN_MESSAGE, "parameter not contains notify body");
            return returnValue;
        }

        // ping++ charge
        String notifyBody = objNotifyBody.toString().trim();
        Event event = null;
        try {
            event = Event.GSON.fromJson(notifyBody, Event.class);
        } catch (JsonSyntaxException josnEx) {
            returnValue.put(RETURN_MESSAGE, josnEx.getMessage());
            return returnValue;
        }
        if (null!=event) {
            //Charge charge = (Charge)event.getData().getObject();
            //Refund refund = (Refund)event.getDate().getObject();
            returnValue.put(RETURN_CODE, CODE_SUCCESS);
            returnValue.put(RETURN_VALUE, event);
            returnValue.put(RETURN_MESSAGE, "OK");
            return returnValue;
        }
        else {
            returnValue.put(RETURN_MESSAGE, "notification message is 'FAIL'");
            return returnValue;
        }
    }

    @Override
    public Map processReturnValue(Map parameters) {
        return parameters;
    }


    /**
     * <A href="https://www.pingxx.com/api#创建-charge-对象">Ping++ 支付API详细介绍</A>
     * @param apiKey Ping++ API-Key.
     * @param privateKey RSA 加密验签私钥.
     * @param appId  Ping++ 应用 ID.
     * @param clientIp 发起支付请求客户端的 IPv4 地址.
     * @param channel 支付使用的第三方支付渠道
     * @param orderNo 商户订单号, 适配每个渠道对此参数的要求, 必须在商户系统内唯一.
     * @param amount 订单总金额(必须大于0), 单位为对应币种的最小货币单位, 人民币为分.
     * @param subject 商品的标题, 该参数最长为32个Unicode字符, 银联全渠道(upacp/upacp_wap)限制在32个字节.
     * @param body 商品的描述信息, 该参数最长为128个Unicode字符, yeepay_wap对于该参数长度限制为100个Unicode字符.
     * @param description 订单附加说明，最多 255 个 Unicode 字符.
     * @param extra 特定渠道发起交易时需要的额外参数，以及部分渠道支付成功返回的额外参数
     * @return Charge 参数
     */
    public Map<String, Object> preparePayment(String apiKey, String privateKey, String appId,
                                              String clientIp, String channel,
                                              String orderNo, int amount,
                                              String subject, String body, String description,
                                              Map extra
    ) {
        Map<String, Object> chargeMap = new HashMap<>();


        if (VerifyUtil.isStringEmpty(body)) {
            body = "no_body";
        }
        if (VerifyUtil.isStringEmpty(description)) {
            description="无";
        }


        // appId
        Map<String, String> app = new HashMap<>();
        app.put("id", appId);
        chargeMap.put("app", app);

        // 发起支付请求客户端的 IP 地址，格式为 IPV4，如: 127.0.0.1
        chargeMap.put("client_ip", clientIp);
        // 支付使用的第三方支付渠道取值
        // 请参考：https://www.pingxx.com/api#api-c-new
        chargeMap.put("channel", channel);
        // 推荐使用 8-20 位，要求数字或字母，不允许其他字符
        chargeMap.put("order_no", orderNo);
        //订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
        chargeMap.put("amount", amount);
        chargeMap.put("currency", "cny");
        // 商品的标题，该参数最长为 32 个 Unicode 字符，
        // 银联全渠道（upacp/upacp_wap）限制在 32 个字节
        chargeMap.put("subject", VerifyUtil.stringLimit(subject, 16));
        // 商品的描述信息，该参数最长为 128 个 Unicode 字符，
        // yeepay_wap 对于该参数长度限制为 100 个 Unicode 字符。
        chargeMap.put("body", VerifyUtil.stringLimit(body, 100));
        // 订单附加说明，最多 255 个 Unicode 字符。
        chargeMap.put("description", VerifyUtil.stringLimit(description, 254));
        chargeMap.put("extra", extra);

        // api key, RAS private key
        chargeMap.put("api_key", apiKey);
        chargeMap.put("private_key", privateKey);

        return chargeMap;
    }


    /**
     * <A href="https://www.pingxx.com/api?language=Java#查询-charge-对象">Ping++ 查询 Charge API详细介绍</A>
     * @param apiKey Ping++ API-Key.
     * @param chargeId 查询的 charge 对象 id.
     * @return Charge 参数
     */
    public Map<String, Object> prepareCheckOfPayment(String apiKey, String chargeId) {
        Map<String, Object> chargeMap = new HashMap<>();
        chargeMap.put("api_key", apiKey);
        chargeMap.put("charge_id", chargeId);
        return chargeMap;
    }


    /**
     * <A href="https://www.pingxx.com/api?language=Java#创建-refund-对象">Ping++ 退款API详细介绍</A>
     * @param apiKey Ping++ API-Key.
     * @param chargeId 退款的 charge 对象 id.
     * @param amount 退款金额大于0, 单位为对应币种的最小货币单位. 必须小于等于可退款金额, 默认为全额退款。
     * @param description 退款详情, 最多 255 个 Unicode 字符.
     * @param fundingSource 退款资金来源.
     *                      取值范围:
     *                      "unsettled_funds":使用未结算资金退款;
     *                      "recharge_funds":使用可用余额退款.
     * @return Refund 参数
     */
    public Map<String, Object> prepareRefund(String apiKey,
                                             String chargeId, Integer amount, String description,
                                             String fundingSource
    ) {
        Map<String, Object> chargeMap = new HashMap<>();

        chargeMap.put("id", chargeId);
        if (amount instanceof Integer) {
            chargeMap.put("amount", amount);
        }
        if (VerifyUtil.isStringEmpty(description)) {
            description="无";
        }
        chargeMap.put("description", VerifyUtil.stringLimit(description, 254));
        if ((fundingSource instanceof String) && !fundingSource.trim().isEmpty()) {
            chargeMap.put("funding_source", fundingSource);
        }

        // api key, RAS private key
        chargeMap.put("api_key", apiKey);

        return chargeMap;
    }


    /**
     * <A href="https://www.pingxx.com/api?language=Java#创建-refund-对象">Ping++ 退款API详细介绍</A>
     * @param apiKey Ping++ API-Key.
     * @param refundId 查询的 refund 对象 id.
     * @param chargeId 退款的 charge 对象 id.
     * @return Refund 参数
     */
    public Map<String, Object> prepareCheckOfRefund(String apiKey,
                                                    String refundId, String chargeId
    ) {
        Map<String, Object> chargeMap = new HashMap<>();

        chargeMap.put("refund_id", refundId);
        chargeMap.put("charge_id", chargeId);
        chargeMap.put("api_key", apiKey);

        return chargeMap;
    }

}
