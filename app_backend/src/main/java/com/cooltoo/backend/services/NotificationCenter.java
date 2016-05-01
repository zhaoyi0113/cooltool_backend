package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseDeviceTokensBean;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by yzzhao on 4/29/16.
 */
@Component
public class NotificationCenter {

    private static final String P12_CER_FILE = "/CertificatesPushNotification.p12";

    private static final String P12_PASSWORD = "!Yqt0529*";

    @Autowired
    private NurseDeviceTokensService deviceTokensService;

    public void publishToAllDevices(String bodyText, Map<String, String> customFields, String action){
        ApnsService apnsService = createAPNSService(bodyText);
        PayloadBuilder badge = APNS.newPayload().alertBody(bodyText).sound("default");
        for(Map.Entry<String, String> entry : customFields.entrySet()){
            badge = badge.customField(entry.getKey(), entry.getValue());
        }
        String payload = createPayload(bodyText, customFields, action);
        List<NurseDeviceTokensBean> deviceTokens = deviceTokensService.getAllActiveDeviceTokens();
        for(NurseDeviceTokensBean token : deviceTokens){
            apnsService.push(token.getDeviceToken(), payload);
        }
    }

    private ApnsService createAPNSService(String bodyText){
        return APNS.newService().withCert(getClass().
                getResourceAsStream(P12_CER_FILE),P12_PASSWORD).
                withSandboxDestination().build();
    }

    private String createPayload(String bodyText, Map<String, String> customFields, String action) {
        ApnsService apnsService = APNS.newService().withCert(getClass().
                getResourceAsStream(P12_CER_FILE), P12_PASSWORD).
                withSandboxDestination().build();
        PayloadBuilder badge = APNS.newPayload().alertBody(bodyText).sound("default");
        for (Map.Entry<String, String> entry : customFields.entrySet()) {
            badge = badge.customField(entry.getKey(), entry.getValue());
        }
        return badge.actionKey(action).build();
    }

}
