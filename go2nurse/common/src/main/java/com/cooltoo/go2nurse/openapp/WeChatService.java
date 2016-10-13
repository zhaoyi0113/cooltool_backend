package com.cooltoo.go2nurse.openapp;

import com.cooltoo.constants.AppChannel;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.WeChatUserInfo;
import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import com.cooltoo.go2nurse.entities.UserWeChatTokenAccessEntity;
import com.cooltoo.go2nurse.entities.WeChatAccountEntity;
import com.cooltoo.go2nurse.repository.UserOpenAppRepository;
import com.cooltoo.go2nurse.repository.UserTokenAccessRepository;
import com.cooltoo.go2nurse.repository.UserWeChatTokenAccessRepository;
import com.cooltoo.go2nurse.repository.WeChatAccountRepository;
import com.cooltoo.go2nurse.util.HttpUtils;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by yzzhao on 8/14/16.
 */
@Service("WeChatService")
@Transactional
public class WeChatService {

    private static final Logger logger = LoggerFactory.getLogger(WeChatService.class);

    @Value("${wechat_token}")
    private String token;

    @Value("${server.host}")
    private String serverHost;

    @Autowired
    private UserOpenAppRepository openAppRepository;

    @Autowired
    private UserTokenAccessRepository tokenAccessRepository;

    @Autowired
    private AccessTokenScheduler tokenScheduler;

    @Autowired
    private WeChatAccountRepository weChatAccountRepository;

    @Autowired
    private UserWeChatTokenAccessRepository weChatTokenAccessRepository;

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
        String appid = state.split("_",2)[0];
        Map accessToken = getWebLoginAccessToken(code, appid);
        WeChatUserInfo userInfo = getUserInfo(accessToken);
        URI userTokens = loginWithWeChatUser(userInfo, state.split("_",2)[1], appid);
        if (userTokens != null) return userTokens;
        return null;
    }

    public URI loginWithWeChatUser(WeChatUserInfo userInfo, String state, String appid) {
        String openid = null;
        if (userInfo != null) {
            openid = userInfo.getOpenid();
            logger.info("login user openid=" + userInfo.getOpenid());

            List<UserOpenAppEntity> users = new ArrayList<>();
                users = openAppRepository.findByOpenidAndStatusOrderByCreatedAtDesc(openid, CommonStatus.ENABLED);
            if (!users.isEmpty()) {
                //when user has open in wechat but has not registered, the user id will be 0
                if(users.get(0).getUserId() != 0) {
                    //user openid already exists, check whether it has login token
                    List<UserTokenAccessEntity> userTokens = tokenAccessRepository.findByUserId(users.get(0).getUserId());
                    if (!userTokens.isEmpty()) {
                        try {
                            String userToken = userTokens.get(0).getToken();
                            saveTokenToUserWeChat(appid, userToken);
                            //if found login token, redirect to token url
                            String url = "http://" + serverHost + "/go2nurse/?token=" + userToken;
                            if (state != null) {
                                url += "?redirect=" + state;
                            }

                            return new URI(url);
                        } catch (URISyntaxException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            } else {
                //user open id doesn't exist, add the open id to database
                logger.info("save openid to database: "+userInfo.getOpenid());
                UserOpenAppEntity entity = new UserOpenAppEntity();
                entity.setChannel(AppChannel.WECHAT);
                Gson gson = new Gson();
                String jsonData = gson.toJson(userInfo);
                entity.setData(jsonData);
                entity.setOpenid(userInfo.getOpenid());
                entity.setUnionid(userInfo.getUnionid());
                entity.setStatus(CommonStatus.ENABLED);
                entity.setAppId(appid);
                entity.setCreatedAt(System.currentTimeMillis());
                openAppRepository.save(entity);
            }
        }
        try {
            String urlStr = "http://" + serverHost + "/go2nurse/#/register";
            if (openid != null) {
                urlStr += "/" + AppChannel.WECHAT + "/" + openid;
            }
            if (state != null) {
                urlStr += "/" + state;
            }
            return new URI(urlStr);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public void saveTokenToUserWeChat(String appId, String token) {
        //save user token and openid
        logger.info("save app id "+appId+" token "+token);

        WeChatAccountEntity wechatAccount = weChatAccountRepository.findFirstByAppId(appId);
        if(wechatAccount!=null){
            if(weChatTokenAccessRepository.countByTokenAndStatus(token, CommonStatus.ENABLED)<=0) {
                UserWeChatTokenAccessEntity weChatTokenAccessEntity = new UserWeChatTokenAccessEntity();
                weChatTokenAccessEntity.setStatus(CommonStatus.ENABLED);
                weChatTokenAccessEntity.setTimeCreated(Calendar.getInstance().getTime());
                weChatTokenAccessEntity.setToken(token);
                weChatTokenAccessEntity.setWechatAccountId(wechatAccount.getId());
                weChatTokenAccessRepository.save(weChatTokenAccessEntity);
            }
        }else{
            logger.warn("can't find appid "+appId);
        }
    }

    public Map<String, String> getJSApiSignature(String userAccessToken, String url) {
        logger.info("request js api signature from " + url);
        String appid = weChatTokenAccessRepository.findAppIdFromToken(userAccessToken, CommonStatus.ENABLED);
        if(appid == null){
            logger.warn("Can't find appid from database for the user token "+userAccessToken);
            return new HashMap<>();
        }
        logger.info("get appid from user token "+userAccessToken+" is "+appid);
        String noncestr = System.currentTimeMillis() + "";
        String jsApiTicket = tokenScheduler.getJsApiTicket(appid);
        String timestamp = System.currentTimeMillis() + "";
        StringBuffer str = new StringBuffer();
        str.append("jsapi_ticket=").append(jsApiTicket).append("&noncestr=").append(noncestr).append("&timestamp=").append(timestamp).append("&url=").append(url);
        String signature = getSha1String(str.toString());
        Map<String, String> signaturemap = new Hashtable<>();
        signaturemap.put("noncestr", noncestr);
        signaturemap.put("jsapiticket", jsApiTicket);
        signaturemap.put("timestamp", timestamp);
        signaturemap.put("signature", signature);
        signaturemap.put("appid", appid);
        logger.info("generate js api ticket");
        return signaturemap;
    }

    public InputStream downloadImageFromWX(String userAccessToken, String mediaId){
        String appid = weChatTokenAccessRepository.findAppIdFromToken(userAccessToken, CommonStatus.ENABLED);
        String accessToken = tokenScheduler.getAccessToken(appid);
        String url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+accessToken+"&media_id="+mediaId;
        try {
            HttpEntity entity = HttpUtils.getHttpResponseEntity(url);
            return entity.getContent();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String getOpenIdByUserId(long userId){
        UserOpenAppEntity userEntity = openAppRepository.findFirstByUserId(userId);
        if(userEntity != null){
            return userEntity.getOpenid();
        }
        throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
    }

    private Map getWebLoginAccessToken(String code, String appid) {
        WeChatAccountEntity weChatAccount = weChatAccountRepository.findFirstByAppId(appid);
        if(weChatAccount == null){
            logger.error("can't find appid "+appid);
            return null;
        }
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                weChatAccount.getAppId() + "&secret=" + weChatAccount.getAppSecret() + "&code=" + code + "&&grant_type=authorization_code";
        return HttpUtils.getRequest(url);
    }

    private WeChatUserInfo getUserInfo(Map<String, String> webToken) {
        if (webToken == null || !webToken.containsKey("openid")
                || !webToken.containsKey("access_token")) {
            return null;
        }
        try {
            String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
                    + webToken.get("access_token") +
                    "&openid=" + webToken.get("openid") + "&lang=zh_CN";
            WeChatUserInfo userInfo = HttpUtils.getHttpRequest(url, WeChatUserInfo.class);
            logger.info("get wechat user info " + userInfo.getOpenid());
            return userInfo;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private String getSha1String(String decript) {
        return signString(decript, "SHA-1");
    }

    private String getMD5String(String descript) {
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
