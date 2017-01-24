package com.cooltoo.go2nurse.openapp;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.NetworkUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhaolisong on 2016/10/18.
 */
@Service("WeChatPayService")
public class WeChatPayService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatPayService.class);

    /***************************************
     *           微信基础接口地址            *
     ***************************************/
    //获取token接口(GET)
    public final static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    //oauth2授权接口(GET)
    public final static String OAUTH2_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    //刷新access_token接口（GET）
    public final static String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    // 菜单创建接口（POST）
    public final static String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    // 菜单查询（GET）
    public final static String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
    // 菜单删除（GET）
    public final static String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
    /***************************************
     *            微信支付接口地址           *
     ***************************************/
    //微信支付统一接口(POST)
    public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //微信退款接口(POST)
    public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    //订单查询接口(POST)
    public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    //关闭订单接口(POST)
    public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
    //退款查询接口(POST)
    public final static String CHECK_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
    //对账单接口(POST)
    public final static String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
    //短链接转换接口(POST)
    public final static String SHORT_URL = "https://api.mch.weixin.qq.com/tools/shorturl";
    //接口调用上报接口(POST)
    public final static String REPORT_URL = "https://api.mch.weixin.qq.com/payitil/report";

    @Value("${wechat_notify_url}")
    private String notifyUrl;
    @Value("${wechat_api_key}")
    private String apiKey;

    public String getNotifyUrl() {
        return notifyUrl;
    }
    public String getApiKey() {
        return apiKey;
    }

    /**
     * <A href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1">微信公众号支付API详细介绍</A>
     * @param openId 用户在商户appId下的唯一标识;trade_type=JSAPI,此参数必传.
     * @param devInfo 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
     * @param spbillCreateIp APP和网页支付提交用户端ip,Native支付填调用微信支付API的机器IP.
     * @param appId 微信分配的公众账号ID(企业号corpId即为此appId).
     * @param mchId 微信支付分配的商户号.
     * @param apiKey 微信商户平台ApiKey
     * @param outTradeNo 商户系统内部的订单号(32个字符内,可包含字母),其他说明见商户订单号
     * @param tradeType 取值如下:JSAPI(公众号支付),NATIVE(原生扫码支付),APP(app支付)
     * @param body 商品简单描述,该字段须严格按照规范传递,具体请见参数规定
     * @param feeType 符合ISO_4217标准的三位字母代码,默认人民币:CNY
     * @param totalFee 订单总金额,单位为分,详见支付金额
     * @param notifyUrl 接收微信支付异步通知回调地址,通知url必须为直接可访问的url,不能携带参数.
     */
    public Map<String, String> payByWeChat(String openId, String devInfo, String spbillCreateIp,
                                           String appId, String mchId, String apiKey,
                                           String outTradeNo, String tradeType, String body,
                                           String feeType, int totalFee,
                                           String notifyUrl
    ) {
        Map<String, String> keyParam = new HashMap();

        String key = "openid";
        keyParam.put(key, openId);

        key = "device_info";
        keyParam.put(key, devInfo);

        key = "spbill_create_ip";
        keyParam.put(key, spbillCreateIp);

        key = "appid";
        keyParam.put(key, appId);

        key = "mch_id";
        keyParam.put(key, mchId);

        key = "out_trade_no";
        keyParam.put(key, outTradeNo);

        key = "body";
        keyParam.put(key, body);

        // 随机字符串,不长于32位.推荐随机数生成算法
        key = "nonce_str";
        keyParam.put(key, NumberUtil.createNoncestr(31));

        key = "trade_type";
        keyParam.put(key, tradeType);

        key = "fee_type";
        keyParam.put(key, feeType);

        key = "total_fee";
        keyParam.put(key, String.valueOf(totalFee));

        key = "notify_url";
        keyParam.put(key, notifyUrl);


        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String timeStart = format.format(calendar.getTime());
        calendar.add(Calendar.MINUTE, 6);
        String timeExpire = format.format(calendar.getTime());

        // 订单失效时间,格式为yyyyMMddHHmmss
        key = "time_expire";
        keyParam.put(key, timeExpire);

        // 订单生成时间,格式为yyyyMMddHHmmss
        key = "time_start";
        keyParam.put(key, timeStart);

        key = "sign";
        keyParam.put(key, createSign(apiKey, "UTF-8", new TreeMap<>(keyParam)));

        logger.info("request payment parameter map===="+keyParam);

        String xmlWeChatOrder = VerifyUtil.getRequestXml(new TreeMap<>(keyParam));
        logger.info("create sign xml "+xmlWeChatOrder);
        String response = NetworkUtil.newInstance().httpsRequest(UNIFIED_ORDER_URL, "POST", xmlWeChatOrder, null);
        keyParam = VerifyUtil.parseResponseXml(response);
        return keyParam;
    }

    /**
     * @Description：sign签名
     * @param characterEncoding 编码格式
     * @param parameters 请求参数
     * @return
     */
    public String createSign(String weChatApiKey, String characterEncoding, SortedMap<String,Object> parameters){
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
        sb.append("key=" + weChatApiKey);
        String sign = NumberUtil.md5Encode(sb.toString(), characterEncoding, "MD5").toUpperCase();
        return sign;
    }

    /**
     * <A href="https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_4&index=6">微信公众号申请退款API详细介绍</A>
     * @param appId 微信分配的公众账号ID(企业号corpId即为此appId).
     * @param mchId 微信支付分配的商户号.
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
     * @param opUserId 操作员帐号, 默认为商户号
     * @param certP12FileInputStream 商户p12证书
     */
    public Map<String, String> refundByWeChat(String appId, String mchId, String devInfo,
                                              String transactionId, String outTradeNo,
                                              String outRefundNo,
                                              int totalFee, int refundFee, String refundFeeType,
                                              String opUserId,
                                              InputStream certP12FileInputStream
    ) {
        Map<String, String> keyParam = new HashMap();
        String key = "appid";
        keyParam.put(key, appId);

        key = "mch_id";
        keyParam.put(key, mchId);

        key = "device_info";
        keyParam.put(key, devInfo);

        // 随机字符串,不长于32位.推荐随机数生成算法
        key = "nonce_str";
        keyParam.put(key, NumberUtil.createNoncestr(31));

        if (null!=outTradeNo && outTradeNo.trim().length()>0) {
            key = "out_trade_no";
            keyParam.put(key, outTradeNo);
        }
        if (null!=transactionId && transactionId.trim().length()>0) {
            key = "transaction_id";
            keyParam.put(key, transactionId);
        }
        if ((null==transactionId || transactionId.trim().length()==0)
         && (null==outTradeNo    || outTradeNo.trim().length()==0)) {
            logger.error("wechat order number and vendor order number are both empty.");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        key = "out_refund_no";
        keyParam.put(key, outRefundNo);

        key = "total_fee";
        keyParam.put(key, String.valueOf(totalFee));

        key = "refund_fee";
        keyParam.put(key, String.valueOf(refundFee));

        key = "refund_fee_type";
        keyParam.put(key, refundFeeType);

        if (null!=opUserId && opUserId.trim().length()>0) {
            key = "op_user_id";
            keyParam.put(key, opUserId);
        }
        else {
            keyParam.put(key, mchId);
        }

        key = "refund_account";
        keyParam.put(key, "REFUND_SOURCE_UNSETTLED_FUNDS");

        key = "sign_type";
        keyParam.put(key, "MD5");

        key = "sign";
        keyParam.put(key, createSign(apiKey, "UTF-8", new TreeMap<>(keyParam)));

        logger.info("request refund parameter map===="+keyParam);

        String xmlWeChatOrder = VerifyUtil.getRequestXml(new TreeMap<>(keyParam));
        logger.info("create sign xml "+xmlWeChatOrder);
        String response = NetworkUtil.newInstance().httpsRequest(REFUND_URL, "POST", keyParam, xmlWeChatOrder, mchId, certP12FileInputStream);
        keyParam = VerifyUtil.parseResponseXml(response);
        return keyParam;
    }
}
