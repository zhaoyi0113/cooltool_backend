package com.cooltoo.go2nurse.payment;

import com.cooltoo.util.NetworkUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhaolisong on 26/01/2017.
 */
public class PaymentWeChat implements IPayment {

    public static final String API_KEY  = "api_key";
    public static final String CHAR_SET = "char_set";

    /***************************************
     *            微信支付接口地址           *
     ***************************************/
    /** 微信支付统一接口(POST) */
    public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    /** 订单查询接口(POST) */
    public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    /** 微信退款接口(POST) */
    public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    /** 退款查询接口(POST) */
    public final static String CHECK_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
    /** 关闭订单接口(POST) */
    public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
    /** 对账单接口(POST) */
    public final static String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
    /** 短链接转换接口(POST) */
    public final static String SHORT_URL = "https://api.mch.weixin.qq.com/tools/shorturl";
    /** 接口调用上报接口(POST) */
    public final static String REPORT_URL = "https://api.mch.weixin.qq.com/payitil/report";

    /**
     * sign 签名
     * @param parameters 请求参数，参数中必须包含 api_key 和 char_set；
     *                   计算签名时需要去掉 api_key 和 char_set
     * @return 签名
     */
    @Override
    public String createSign(Map parameters) {
        Object objWeChatApiKey = parameters.get(API_KEY);
        if (!(objWeChatApiKey instanceof String)) {
            return "API key is empty";
        }
        Object objCharSet = parameters.get(CHAR_SET);
        if (!(objCharSet instanceof String)) {
            return "char set is empty";
        }
        parameters.remove(API_KEY);
        parameters.remove(CHAR_SET);

        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null!=v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + objWeChatApiKey.toString());
        String sign = NumberUtil.md5Encode(sb.toString(), objCharSet.toString(), "MD5").toUpperCase();
        return sign;
    }

    /**
     * 验证签名, 去掉 sign 之后，用剩下的参数重新计算新的newSign，
     * 检查 sign 与 newSign 是否相等。
     * @param parameters 请求参数，参数中必须包含 sign, api_key 和 char_set；
     *                   验证签名时需要去掉 sign
     * @return 签名是否正确
     */
    @Override
    public boolean checkSign(Map parameters) {
        if (null==parameters && parameters.isEmpty()) {
            return false;
        }
        Object objSign = parameters.get("sign");
        if (!(objSign instanceof String)) {
            return false;
        }
        parameters.remove("sign");

        String newSign = createSign(parameters);
        parameters.put("sign", objSign);
        return objSign.toString().equals(newSign);
    }

    @Override
    public Map pay(Map paramForPayment) {
        String xmlWeChatOrder = VerifyUtil.getRequestXml(new TreeMap<>(paramForPayment));
        String response = NetworkUtil.newInstance().httpsRequest(UNIFIED_ORDER_URL, "POST", xmlWeChatOrder, null);
        Map<String, String> returnValue = VerifyUtil.parseResponseXml(response);
        return returnValue;
    }

    @Override
    public Map checkPayment(Map parameters) {
        String xmlWeChatOrder = VerifyUtil.getRequestXml(new TreeMap<>(parameters));
        String response = NetworkUtil.newInstance().httpsRequest(CHECK_ORDER_URL, "POST", xmlWeChatOrder, null);
        Map<String, String> keyParam = VerifyUtil.parseResponseXml(response);
        return keyParam;
    }

    @Override
    public Map refund(Map parameters) {
        Object objMchId = parameters.get("mch_id");
        String mchId = "";
        if (objMchId instanceof String) {
            mchId = objMchId.toString();
        }
        String xmlWeChatOrder = VerifyUtil.getRequestXml(new TreeMap<>(parameters));
        String response = NetworkUtil.newInstance().httpsRequest(REFUND_URL, "POST", parameters, xmlWeChatOrder, mchId, null/*certP12FileInputStream*/);
        Map<String, String> keyParam = VerifyUtil.parseResponseXml(response);
        return keyParam;
    }

    @Override
    public Map checkRefund(Map parameters) {
        String xmlWeChatOrder = VerifyUtil.getRequestXml(new TreeMap<>(parameters));
        String response = NetworkUtil.newInstance().httpsRequest(CHECK_REFUND_URL, "POST", xmlWeChatOrder);
        Map<String, String> keyParam = VerifyUtil.parseResponseXml(response);
        return keyParam;
    }

    @Override
    public Map close(Map parameters) {
        String xmlWeChatOrder = VerifyUtil.getRequestXml(new TreeMap<>(parameters));
        String response = NetworkUtil.newInstance().httpsRequest(CLOSE_ORDER_URL, "POST", xmlWeChatOrder);
        Map<String, String> keyParam = VerifyUtil.parseResponseXml(response);
        return keyParam;
    }

    @Override
    public Map processNotify(Map parameters) {
        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put(RETURN_CODE, CODE_FAIL);
        returnValue.put(RETURN_VALUE, null);

        Object objWeChatApiKey = parameters.get(API_KEY);
        if (!(objWeChatApiKey instanceof String)) {
            returnValue.put(RETURN_MESSAGE, "parameter not contains ApiKey");
            return returnValue;
        }
        Object objNotifyBody = parameters.get("notify_body");
        if (!(objNotifyBody instanceof String)) {
            returnValue.put(RETURN_MESSAGE, "parameter not contains notify body");
            return returnValue;
        }

        Document document;
        SAXReader reader = new SAXReader();
        try { document = reader.read(new ByteArrayInputStream(((String)objNotifyBody).getBytes())); }
        catch (DocumentException doc) {
            returnValue.put(RETURN_MESSAGE, doc.getMessage());
            return returnValue;
        }

        Map<String,String> keyValue = new HashMap<>();
        try {
            Element root = document.getRootElement();
            List<Element> elem = root.elements();
            for (Element e : elem) {
                keyValue.put(e.getName(), e.getText());
            }
        }
        catch (Exception ex) {
            returnValue.put(RETURN_MESSAGE, "notification message format error");
            return returnValue;
        }

        Object returnCode = keyValue.get("return_code");
        if (null!=returnCode && "success".equalsIgnoreCase(returnCode.toString())) {
            //check the sign of WeiXin callback
            String originSign = null!=keyValue.get("sign") ? keyValue.get("sign").toString() : "";
            keyValue.remove("sign");

            keyValue.put(API_KEY, objWeChatApiKey.toString());
            keyValue.put(CHAR_SET, "UTF-8");
            String checkSign = createSign(keyValue);
            keyValue.remove(API_KEY);
            keyValue.remove(CHAR_SET);

            keyValue.put("sign", originSign);
            if (!checkSign.equalsIgnoreCase(originSign)) {
                returnValue.put(RETURN_MESSAGE, "checksum sign is wrong");
                return returnValue;
            }

            returnValue.put(RETURN_CODE, CODE_SUCCESS);
            returnValue.put(RETURN_MESSAGE, "OK");
            returnValue.put(RETURN_VALUE, keyValue);
            return returnValue;
        }
        else {
            returnValue.put(RETURN_MESSAGE, "notification message is 'FAIL'");
            return returnValue;
        }
    }

    @Override
    public Map processReturnValue(Map parameters) {
        Object returnCode = parameters.get(RETURN_CODE);
        Object returnMessage = parameters.get(RETURN_MESSAGE);

        StringBuilder responseMessage = new StringBuilder();
        responseMessage.append("<xml>");
        responseMessage.append("<return_code><![CDATA[").append(returnCode).append("]]></return_code>");
        responseMessage.append("<return_msg><![CDATA[").append(returnMessage).append("]]></return_msg>");
        responseMessage.append("</xml>");

        parameters.put("response_message", responseMessage.toString());
        return parameters;
    }

    /**
     * <A href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1">微信公众号支付API详细介绍</A>
     * @param apiKey 微信商户平台ApiKey
     * @param appId 微信分配的公众账号ID(企业号corpId即为此appId).
     * @param mchId 微信支付分配的商户号.
     * @param devInfo 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
     * @param tradeType 取值如下:JSAPI(公众号支付),NATIVE(原生扫码支付),APP(app支付)
     * @param openId 用户在商户appId下的唯一标识;trade_type=JSAPI,此参数必传.
     * @param spbillCreateIp APP和网页支付提交用户端ip,Native支付填调用微信支付API的机器IP.
     * @param outTradeNo 商户系统内部的订单号(32个字符内,可包含字母),其他说明见商户订单号
     * @param body 商品简单描述,该字段须严格按照规范传递,具体请见参数规定
     * @param feeType 符合ISO_4217标准的三位字母代码,默认人民币:CNY
     * @param totalFee 订单总金额,单位为分,详见支付金额
     * @param notifyUrl 接收微信支付异步通知回调地址,通知url必须为直接可访问的url,不能携带参数.
     * @return 支付订单参数
     */
    public Map<String, String> preparePayment(String apiKey,
                                              String appId, String mchId, String devInfo, String tradeType,
                                              String openId, String spbillCreateIp,
                                              String outTradeNo, String body, String feeType, int totalFee,
                                              String notifyUrl
    ) {
        Map<String, String> keyParam = new HashMap();
        String key;

        key = "appid";
        keyParam.put(key, appId);
        key = "mch_id";
        keyParam.put(key, mchId);
        key = "device_info";
        keyParam.put(key, devInfo);
        key = "trade_type";
        keyParam.put(key, tradeType);

        key = "openid";
        keyParam.put(key, openId);
        key = "spbill_create_ip";
        keyParam.put(key, spbillCreateIp);


        key = "out_trade_no";
        keyParam.put(key, outTradeNo);
        key = "body";
        keyParam.put(key, VerifyUtil.stringLimit(body, 127));
        key = "fee_type";
        keyParam.put(key, feeType);
        key = "total_fee";
        keyParam.put(key, String.valueOf(totalFee));

        key = "notify_url";
        keyParam.put(key, notifyUrl);

        // 随机字符串,不长于32位.推荐随机数生成算法
        key = "nonce_str";
        keyParam.put(key, NumberUtil.createNoncestr(31));

        // 订单生成时间/订单失效时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String timeStart = format.format(calendar.getTime());
        calendar.add(Calendar.MINUTE, 5);
        String timeExpire = format.format(calendar.getTime());
        // 订单失效时间,格式为yyyyMMddHHmmss
        key = "time_expire";
        keyParam.put(key, timeExpire);
        // 订单生成时间,格式为yyyyMMddHHmmss
        key = "time_start";
        keyParam.put(key, timeStart);


        keyParam.put(API_KEY, apiKey);
        keyParam.put(CHAR_SET, "UTF-8");
        String sign = createSign(keyParam);
        keyParam.remove(API_KEY);
        keyParam.remove(CHAR_SET);
        key = "sign";
        keyParam.put(key, sign);

        return keyParam;
    }

    /**
     * <A href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2">微信公众号查询订单API详细介绍</A>
     * @param apiKey 微信商户平台ApiKey
     * @param appId 微信分配的公众账号ID(企业号corpId即为此appId).
     * @param mchId 微信支付分配的商户号.
     *
     *（二选一）
     * @param transactionId 微信生成的订单号，在支付通知中有返回，建议优先使用
     * @param outTradeNo 商户侧传给微信的订单号
     */
    public Map<String, String> prepareCheckOfPayment(String apiKey,
                                                     String appId, String mchId,
                                                     String transactionId, String outTradeNo
    ) {
        Map<String, String> keyParam = new HashMap();
        String key;

        key = "appid";
        keyParam.put(key, appId);

        key = "mch_id";
        keyParam.put(key, mchId);

        if (null!=transactionId && transactionId.length()!=0) {
            key = "transaction_id";
            keyParam.put(key, transactionId);
        }
        if (null!=outTradeNo && outTradeNo.length()!=0) {
            key = "out_trade_no";
            keyParam.put(key, outTradeNo);
        }

        // 随机字符串,不长于32位.推荐随机数生成算法
        key = "nonce_str";
        keyParam.put(key, NumberUtil.createNoncestr(31));

        keyParam.put(API_KEY, apiKey);
        keyParam.put(CHAR_SET, "UTF-8");
        String sign = createSign(keyParam);
        keyParam.remove(API_KEY);
        keyParam.remove(CHAR_SET);

        key = "sign";
        keyParam.put(key, sign);

        return keyParam;
    }

    /**
     * <A href="https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_4&index=6">微信公众号申请退款API详细介绍</A>
     * @param apiKey 微信商户平台ApiKey
     * @param appId 微信分配的公众账号ID(企业号corpId即为此appId).
     * @param mchId 微信支付分配的商户号.
     * @param opUserId 操作员帐号, 默认为商户号
     * @param devInfo 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
     *
     *（二选一）
     * @param transactionId 微信生成的订单号，在支付通知中有返回
     * @param outTradeNo 商户侧传给微信的订单号
     *
     * @param outRefundNo 商户退款单号, 商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
     * @param totalFee 总金额，订单总金额，单位为分，只能为整数，详见支付金额
     * @param refundFee 退款金额，退款总金额，订单总金额，单位为分，只能为整数，详见支付金额
     * @param refundFeeType 货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
     */
    public Map<String, String> prepareRefund(String apiKey,
                                             String appId, String mchId, String opUserId, String devInfo,
                                             String transactionId, String outTradeNo,
                                             String outRefundNo, int totalFee, int refundFee, String refundFeeType
    ) {
        Map<String, String> keyParam = new HashMap();
        String key;

        key = "appid";
        keyParam.put(key, appId);
        key = "mch_id";
        keyParam.put(key, mchId);
        key = "device_info";
        keyParam.put(key, devInfo);
        if (null!=opUserId && opUserId.trim().length()>0) {
            key = "op_user_id";
            keyParam.put(key, opUserId);
        }
        else {
            key = "op_user_id";
            keyParam.put(key, mchId);
        }

        if (null!=outTradeNo && outTradeNo.trim().length()>0) {
            key = "out_trade_no";
            keyParam.put(key, outTradeNo);
        }
        if (null!=transactionId && transactionId.trim().length()>0) {
            key = "transaction_id";
            keyParam.put(key, transactionId);
        }

        key = "out_refund_no";
        keyParam.put(key, outRefundNo);
        key = "total_fee";
        keyParam.put(key, String.valueOf(totalFee));
        key = "refund_fee";
        keyParam.put(key, String.valueOf(refundFee));
        key = "refund_fee_type";
        keyParam.put(key, refundFeeType);


        key = "refund_account";
        keyParam.put(key, "REFUND_SOURCE_UNSETTLED_FUNDS");

        key = "sign_type";
        keyParam.put(key, "MD5");

        // 随机字符串,不长于32位.推荐随机数生成算法
        key = "nonce_str";
        keyParam.put(key, NumberUtil.createNoncestr(31));

        keyParam.put(API_KEY, apiKey);
        keyParam.put(CHAR_SET, "UTF-8");
        String sign = createSign(keyParam);
        keyParam.remove(API_KEY);
        keyParam.remove(CHAR_SET);

        key = "sign";
        keyParam.put(key, sign);

        return keyParam;
    }

    /**
     * <A href="https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_5&index=7">微信公众号查询退款API详细介绍</A>
     * @param apiKey 微信商户平台ApiKey
     * @param appId   微信分配的公众账号ID(企业号corpId即为此appId).
     * @param mchId   微信支付分配的商户号.
     * @param devInfo 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"

     * 四选一
     * @param transactionId 微信订单号 String(32)  微信订单号
     * @param outTradeNo    商户订单号 String(32) 商户系统内部的订单号
     * @param outRefundNo   商户退款单号 String(32) 商户侧传给微信的退款单号
     * @param refundId      微信退款单号 String(28) 微信生成的退款单号，在申请退款接口有返回
     */
    public Map<String, String> prepareCheckOfRefund(String apiKey,
                                                    String appId, String mchId, String devInfo,
                                                    String outRefundNo, String refundId,
                                                    String transactionId, String outTradeNo
    ) {
        Map<String, String> keyParam = new HashMap();
        String key;

        key = "appid";
        keyParam.put(key, appId);
        key = "mch_id";
        keyParam.put(key, mchId);
        key = "device_info";
        keyParam.put(key, devInfo);

        if (null!=transactionId && transactionId.length()!=0) {
            key = "transaction_id";
            keyParam.put(key, transactionId);
        }
        if (null!=outTradeNo && outTradeNo.length()!=0) {
            key = "out_trade_no";
            keyParam.put(key, outTradeNo);
        }
        if (null!=outRefundNo && outRefundNo.length()!=0) {
            key = "out_refund_no";
            keyParam.put(key, outRefundNo);
        }
        if (null!=refundId && refundId.length()!=0) {
            key = "refund_id";
            keyParam.put(key, refundId);
        }

        key = "sign_type";
        keyParam.put(key, "MD5");

        // 随机字符串,不长于32位.推荐随机数生成算法
        key = "nonce_str";
        keyParam.put(key, NumberUtil.createNoncestr(31));

        keyParam.put(API_KEY, apiKey);
        keyParam.put(CHAR_SET, "UTF-8");
        String sign = createSign(keyParam);
        keyParam.remove(API_KEY);
        keyParam.remove(CHAR_SET);

        key = "sign";
        keyParam.put(key, sign);

        return keyParam;
    }

    /**
     * <A href="https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_3&index=5">微信公众号关闭退款API详细介绍</A>
     * @param apiKey     微信商户平台ApiKey
     * @param appId      微信分配的公众账号ID(企业号corpId即为此appId).
     * @param mchId      微信支付分配的商户号.
     * @param outTradeNo 商户订单号，商户系统内部的订单号
     */
    public Map<String, String> prepareClose(String apiKey, String appId, String mchId, String outTradeNo) {
        Map<String, String> keyParam = new HashMap();
        String key = "appid";
        keyParam.put(key, appId);

        key = "mch_id";
        keyParam.put(key, mchId);

        key = "out_trade_no";
        keyParam.put(key, outTradeNo);

        // 随机字符串,不长于32位.推荐随机数生成算法
        key = "nonce_str";
        keyParam.put(key, NumberUtil.createNoncestr(31));

        key = "sign_type";
        keyParam.put(key, "MD5");

        keyParam.put(API_KEY, apiKey);
        keyParam.put(CHAR_SET, "UTF-8");
        String sign = createSign(keyParam);
        keyParam.remove(API_KEY);
        keyParam.remove(CHAR_SET);

        key = "sign";
        keyParam.put(key, sign);

        return keyParam;
    }


    /***************************************
     *           微信基础接口地址            *
     ***************************************/
    /** 获取token接口(GET) */
    public final static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    /** oauth2授权接口(GET) */
    public final static String OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    /** 刷新access_token接口(GET) */
    public final static String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    /**  菜单创建接口(POST) */
    public final static String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    /**  菜单查询(GET) */
    public final static String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
    /**  菜单删除(GET) */
    public final static String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

}
