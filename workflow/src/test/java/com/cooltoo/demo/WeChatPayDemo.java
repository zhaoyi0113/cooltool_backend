package com.cooltoo.demo;

import com.cooltoo.util.NetworkUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhaoyi0113 on 22/10/2016.
 */
public class WeChatPayDemo {

    public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static String getMD5String(String descript){
        return signString(descript, "MD5");
    }

    private static String signString(String decript, String algorithm) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(algorithm);
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            String sign = hexString.toString();
            return sign;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String createSign(String weChatApiKey, String characterEncoding, SortedMap<String,Object> parameters){
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
        System.out.println("data:"+sb);
        return NumberUtil.md5Encode(sb.toString(), characterEncoding, "MD5").toUpperCase();
//        return getMD5String(sb.toString()).toUpperCase();
    }

    public static void main(String[] args) {
        Map<String, Object> keyParam = new HashMap();

        String key = "openid";
        keyParam.put(key, "oFmjasjc8rv0E8Gq63IHGD5fP49A");

        key = "device_info";
        keyParam.put(key, "WEB");

        key = "spbill_create_ip";
        keyParam.put(key, "220.240.189.182");

        key = "appid";
        keyParam.put(key, "wxbeec5531b4ae001a");

        key = "mch_id";
        keyParam.put(key, "1252899001");

        key = "out_trade_no";
        keyParam.put(key, "MRxPBFbYyjemZdYSjSxpTODysuylXgb");

        key = "body";
        keyParam.put(key, "fdsafs");

        // 随机字符串,不长于32位.推荐随机数生成算法
        key = "nonce_str";
        keyParam.put(key, "m2BWnsQmiYFH1LBP5FO7Fr5GkwBWyYJ");

        key = "trade_type";
        keyParam.put(key, "JSAPI");

        key = "fee_type";
        keyParam.put(key, "CNY");

        key = "total_fee";
        keyParam.put(key, "100");

        key = "notify_url";
        keyParam.put(key, "http://www.go2nurse.cn:8090/go2nurse/user/order/pingpp/webhooks");


        // 订单失效时间,格式为yyyyMMddHHmmss
        key = "time_expire";
        keyParam.put(key, "20161022215821");

        // 订单生成时间,格式为yyyyMMddHHmmss
        key = "time_start";
        keyParam.put(key, "20161022215221");

        key = "sign";
        keyParam.put(key, createSign("688d9c1d420b3f54be09ccf47071000d", "UTF-8", new TreeMap<>(keyParam)));

        System.out.println("request payment parameter map===="+keyParam);

        String xmlWeChatOrder = VerifyUtil.getRequestXml(keyParam);
        System.out.println("create sign xml "+xmlWeChatOrder);
        String response = NetworkUtil.httpsRequest(UNIFIED_ORDER_URL, "POST", xmlWeChatOrder, null);
        keyParam = VerifyUtil.parseResponseXml(response);
        System.out.println(keyParam);
    }
}
