package com.cooltoo.go2nurse.openapp;

import com.cooltoo.go2nurse.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by yzzhao on 1/10/16.
 */
@Component
public class AccessTokenScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AccessTokenScheduler.class);
    @Value("${wechat_go2nurse_appid}")
    private String srvAppId;

    @Value("${wechat_go2nurse_appsecret}")
    private String srvAppSecret;

    private String accessToken;

    private String jsApiTicket;

    @Autowired
    private WeChatService weChatService;

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
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + srvAppId + "&secret=" + srvAppSecret;
        Map map = HttpUtils.getHttpRequest(url, Map.class);
        if (map != null && map.containsKey("access_token")) {
            accessToken = (String) map.get("access_token");
            logger.info("refresh access token " + accessToken);
            requestJSAPITicket();
        } else {
            logger.error("can't get token access");
        }
    }

    private void requestJSAPITicket() {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
        Map map = HttpUtils.getHttpRequest(url, Map.class);
        if (map != null && map.containsKey("ticket")) {
            jsApiTicket = (String) map.get("ticket");
            logger.info("refresh js api ticket " + jsApiTicket);
        } else {
            logger.error("can't get js api ticket");
        }
    }


}
