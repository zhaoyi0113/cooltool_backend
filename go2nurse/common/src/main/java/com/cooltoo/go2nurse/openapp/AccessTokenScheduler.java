package com.cooltoo.go2nurse.openapp;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.WeChatAccountEntity;
import com.cooltoo.go2nurse.repository.WeChatAccountRepository;
import com.cooltoo.go2nurse.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yzzhao on 1/10/16.
 */
@Component
public class AccessTokenScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenScheduler.class);

    private Map<String, String> accessTokens = new HashMap<>();

    private Map<String, String> jsApiTickets = new HashMap<>();

    @Autowired private WeChatAccountRepository weChatAccountRepository;

    @PostConstruct
    public void postConstruct() {
        getAccessTokenScheduler();
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void getAccessTokenScheduler() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                requestAccessToken();
            }
        });
        thread.start();
    }

    private void requestAccessToken() {
        logger.info("access token scheduler");
        List<WeChatAccountEntity> accounts = weChatAccountRepository.findByStatus(CommonStatus.ENABLED);
        for(WeChatAccountEntity entity : accounts){
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + entity.getAppId() + "&secret=" + entity.getAppSecret();
            Map map = HttpUtils.getHttpRequest(url, Map.class);
            if (map != null && map.containsKey("access_token")) {
                String accessToken = (String) map.get("access_token");
                logger.info("refresh access token " + accessToken);
                accessTokens.put(entity.getAppId(), accessToken);
                requestJSAPITicket(entity.getAppId(), accessToken);
            } else {
                logger.error("can't get token access");
            }
        }

    }

    private void requestJSAPITicket(String appId, String accessToken) {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
        Map map = HttpUtils.getHttpRequest(url, Map.class);
        if (map != null && map.containsKey("ticket")) {
            String jsApiTicket = (String) map.get("ticket");
            logger.info("refresh js api ticket " + jsApiTicket);
            jsApiTickets.put(appId, jsApiTicket);
        } else {
            logger.error("can't get js api ticket");
        }
    }

    public String getJsApiTicket(String appid) {
        return jsApiTickets.get(appid);
    }

    public String getAccessToken(String appid) {
        return accessTokens.get(appid);
    }

}
