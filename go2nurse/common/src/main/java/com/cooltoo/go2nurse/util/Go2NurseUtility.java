package com.cooltoo.go2nurse.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by hp on 2016/6/12.
 */
@Component
public class Go2NurseUtility {

    private static final Logger logger = LoggerFactory.getLogger(Go2NurseUtility.class);

    @Value("${go2nurse.nginx.prefix}")
    private String httpPrefix;

    @Value("${go2nurse.nginx.prefix.for.nursego}")
    private String httpPrefixForNurseGo;
    @Value("${storage.user.path}")
    private String nursegoUserPath;
    @Value("${storage.official.path}")
    private String nursegoOfficialPath;

    public String getHttpPrefix() {
        return httpPrefix;
    }

    public String getHttpPrefixForNurseGo() {
        return httpPrefixForNurseGo;
    }

    public String getHttpPrefixUserPathForNurseGo() {
        return httpPrefixForNurseGo+nursegoUserPath;
    }

    public String getHttpPrefixOfficialPathForNurseGo() {
        return httpPrefixForNurseGo+nursegoOfficialPath;
    }



    @Value("${wechat_notify_url}")
    private String wechatNotifyUrl;
    public String getWechatNotifyUrl() {
        return wechatNotifyUrl;
    }

    @Value("${wechat_api_key}")
    private String wechatApiKey;
    public String getWechatApiKey() {
        return wechatApiKey;
    }



    @Value("${pingpp_go2nurse_api_key}")
    private String pingPPAPIKey;
    public String getPingPPAPIKey() {
        return pingPPAPIKey;
    }

    @Value("${pingpp_go2nurse_app_id}")
    private String pingPPAPPId;
    public String getPingPPAPPId() {
        return pingPPAPPId;
    }

    @Value("${pingpp_go2nurse_rsa_private_key}")
    private String pingPPRSAPrivateKey;
    public String getPingPPRSAPrivateKey() {
        return pingPPRSAPrivateKey;
    }
}
