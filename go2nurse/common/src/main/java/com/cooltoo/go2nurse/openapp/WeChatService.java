package com.cooltoo.go2nurse.openapp;

import com.cooltoo.constants.AppChannel;
import com.cooltoo.go2nurse.beans.WeChatUserInfo;
import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import com.cooltoo.go2nurse.repository.UserOpenAppRepository;
import com.cooltoo.go2nurse.repository.UserTokenAccessRepository;
import com.cooltoo.go2nurse.util.HttpUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @Value("${go2nurse.web.port}")
    private String serverPort;

    @Value("${server.host}")
    private String serverHost;

    @Autowired
    private UserOpenAppRepository openAppRepository;

    @Autowired
    private UserTokenAccessRepository tokenAccessRepository;

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

    public URI login(String code, String state) {
        Map accessToken = getWebLoginAccessToken(code);
        WeChatUserInfo userInfo = getUserInfo(accessToken);
        URI userTokens = loginWithWeChatUser(userInfo);
        if (userTokens != null) return userTokens;
        return null;
    }

    public URI loginWithWeChatUser(WeChatUserInfo userInfo) {
        String unionid = null;
        if (userInfo != null) {
            unionid = userInfo.getUnionid();
            logger.info("login user openid=" + userInfo.getOpenid() + " unionid=" + unionid);
            List<UserOpenAppEntity> users = openAppRepository.findByUnionid(unionid);
            if (!users.isEmpty() && users.get(0).getUserId() != 0) {
                //user unionid already exists, check whether it has login token
                List<UserTokenAccessEntity> userTokens = tokenAccessRepository.findByUserId(users.get(0).getUserId());
                if (!userTokens.isEmpty()) {
                    try {
                        //if found login token, redirect to token url
                        return new URI("http://" + serverHost + ":" + serverPort + "/?token=" + userTokens.get(0).getToken());
                    } catch (URISyntaxException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } else {
                //user union id doesn't exist, add the union id to database
                UserOpenAppEntity entity = new UserOpenAppEntity();
                entity.setChannel(AppChannel.WECHAT);
                Gson gson = new Gson();
                String jsonData = gson.toJson(userInfo);
                entity.setData(jsonData);
                entity.setOpenid(userInfo.getOpenid());
                entity.setUnionid(userInfo.getUnionid());
                entity.setCreatedAt(System.currentTimeMillis());
                openAppRepository.save(entity);
            }
        }
        try {
            String urlStr = "http://" + serverHost + ":" + serverPort + "/#/register";
            if (unionid != null) {
                urlStr += "/" + AppChannel.WECHAT + "/" + unionid;
            }
            return new URI(urlStr);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Map getWebLoginAccessToken(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                srvAppId + "&secret=" + srvAppSecret + "&code=" + code + "&&grant_type=authorization_code";
        return HttpUtils.getRequest(url);
    }

    public WeChatUserInfo getUserInfo(Map<String, String> webToken) {
        if (webToken == null || !webToken.containsKey("openid")
                || !webToken.containsKey("access_token")) {
            return null;
        }
        try {
            String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
                    + webToken.get("access_token") +
                    "&openid=" + webToken.get("openid") + "&lang=zh_CN";
            WeChatUserInfo userInfo = HttpUtils.getHttpRequest(url, WeChatUserInfo.class);
            logger.info("get wechat user info " + userInfo.getUnionid());
            return userInfo;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
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
