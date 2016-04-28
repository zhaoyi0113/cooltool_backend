package com.cooltoo.demo;

import com.cooltoo.AbstractCooltooTest;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 4/28/16.
 */
@Transactional
@Ignore
public class APNSDemo extends AbstractCooltooTest {

    private static final List<String> deviceTokens = new ArrayList();
    static{
        deviceTokens.add("bffcee41878a2919fd97aa4e536c05053cbcd84db18f9a1eeaf4f271dd10734a");
        deviceTokens.add("47c8d5781599aa480a27fc89a78680a4cec559e23974f92e63e41aebc7009af4");
    }

    @Test
    public void testPublishNotification() {
        ApnsService apnsService = APNS.newService().withCert(getClass().
                getResourceAsStream("/CertificatesPushNotification.p12"),"!Yqt0529*").
                withSandboxDestination().build();
        PayloadBuilder badge = APNS.newPayload().alertBody("this is a test").sound("default");
        String payload = badge.customField("custom", "111").actionKey("action1").build();
//        String payload = APNS.newPayload().alertBody("this is a test").build();
        for(String token : deviceTokens){
            apnsService.push(token, payload);
        }

    }
}
