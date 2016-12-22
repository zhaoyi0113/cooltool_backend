package com.cooltoo.go2nurse.service;

import com.cooltoo.util.VerifyUtil;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.PingppException;
import com.pingplusplus.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 2016/7/15.
 */
@Service("PingPPService")
public class PingPPService {

    private static final Logger logger = LoggerFactory.getLogger(PingPPService.class);

    @Value("${pingpp_go2nurse_api_key}")
    private String pingPPAPIKey;
    @Value("${pingpp_go2nurse_app_id}")
    private String pingPPAPPId;
    @Value("${pingpp_go2nurse_rsa_private_key}")
    private String pingPPRSAPrivateKey;

    public PingPPService() {
    }

    public Charge createCharge(String orderNo, String channel, int amount, String ip, String subject, String body, String description, Map extra) {
        logger.info("create charge orderNo={} channel={} amount={} ip={} subject={} body={} desc={}",
                orderNo, channel, amount, ip, subject, body, description);
        if (VerifyUtil.isStringEmpty(body)) {
            body = "no_body";
        }
        if (VerifyUtil.isStringEmpty(description)) {
            description="无";
        }
        Charge charge = null;
        Pingpp.apiKey = pingPPAPIKey;
        Pingpp.privateKey = pingPPRSAPrivateKey;
        Map<String, Object> chargeMap = new HashMap<>();
        // 推荐使用 8-20 位，要求数字或字母，不允许其他字符
        chargeMap.put("order_no", orderNo);
        // appId
        Map<String, String> app = new HashMap<>();
        app.put("id", pingPPAPPId);
        chargeMap.put("app", app);
        // 支付使用的第三方支付渠道取值
        // 请参考：https://www.pingxx.com/api#api-c-new
        chargeMap.put("channel", channel);
        //订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
        chargeMap.put("amount", amount);
        // 发起支付请求客户端的 IP 地址，格式为 IPV4，如: 127.0.0.1
        chargeMap.put("client_ip", ip);
        chargeMap.put("currency", "cny");
        // 商品的标题，该参数最长为 32 个 Unicode 字符，
        // 银联全渠道（upacp/upacp_wap）限制在 32 个字节
        chargeMap.put("subject", stringLimit(subject, 16));
        // 商品的描述信息，该参数最长为 128 个 Unicode 字符，
        // yeepay_wap 对于该参数长度限制为 100 个 Unicode 字符。
        chargeMap.put("body", stringLimit(body, 100));
        // 订单附加说明，最多 255 个 Unicode 字符。
        chargeMap.put("description", stringLimit(description, 254));
        chargeMap.put("extra", extra);
        try {
            //发起交易请求
            charge = Charge.create(chargeMap);
            // 传到客户端请先转成字符串 .toString(), 调该方法，会自动转成正确的 JSON 字符串
            logger.info("create charge is {}", charge);
        } catch (PingppException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return charge;
    }

    private String stringLimit(String src, int lengthLimit) {
        if (VerifyUtil.isStringEmpty(src)) {
            return "";
        }
        if (lengthLimit>0 && src.length()>lengthLimit) {
            return src.substring(0, lengthLimit);
        }
        return src;
    }

}
