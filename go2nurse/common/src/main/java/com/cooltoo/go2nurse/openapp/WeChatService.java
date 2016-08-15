package com.cooltoo.go2nurse.openapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by yzzhao on 8/14/16.
 */
@Service("WeChatService")
public class WeChatService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatService.class);

    @Value("${wechat_token}")
    private String token;

    @Value("${wechat_go2nurse_appid}")
    private String srvAppId;

    @Value("${wechat_go2nurse_appsecret}")
    private String srvAppSecret;

    public boolean validateEntryConnection(String signature, String timeStamp, String nonce) {
        logger.info("validate connection " + signature + ", " + timeStamp + ", " + nonce + ", " + token);
        String[] tmpArr = {token, timeStamp, nonce};
        Arrays.sort(tmpArr);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tmpArr.length; i++) {
            builder.append(tmpArr[i]);
        }
        String tmpStr = builder.toString();
        String sha1 = getSha1String(tmpStr);

        logger.info("get tmp str " + tmpStr);
        logger.info("get sha1 " + sha1);
        if (sha1 != null && sha1.equals(signature)) {
            return true;
        }
        return false;
    }

    public String getSha1String(String decript) {
        return signString(decript, "SHA-1");
    }

    public String getMD5String(String descript) {
        return signString(descript, "MD5");
    }


    private static String signString(String decript, String algorithm) {
        try {
            logger.info(algorithm + " on string:" + decript);
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
            logger.info("generate:" + sign);
            return sign;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
