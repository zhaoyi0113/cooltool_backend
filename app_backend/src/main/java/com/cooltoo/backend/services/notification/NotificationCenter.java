package com.cooltoo.backend.services.notification;

import com.cooltoo.backend.beans.NurseDeviceTokensBean;
import com.cooltoo.backend.services.NurseDeviceTokensService;
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

    public void publishToAllDevices(String bodyText, Map<String, String> customFields, String actionCode){
        List<NurseDeviceTokensBean> deviceTokens = deviceTokensService.getAllActiveDeviceTokens();
        if(deviceTokens.isEmpty()){
            return;
        }
        ApnsService apnsService = createAPNSService(bodyText);
        String payload = createPayload(bodyText, customFields, actionCode);
        publishToDevice(apnsService, payload, deviceTokens);
    }


    public void publishToUser(long userId, String bodyText, Map<String, String> customFields, String actionCode){
        List<NurseDeviceTokensBean> tokens = deviceTokensService.getNurseDeviceTokens(userId);
        if (tokens.isEmpty()){
            return;
        }
        ApnsService apnsService = createAPNSService(bodyText);
        String payload = createPayload(bodyText, customFields, actionCode);
        publishToDevice(apnsService, payload, tokens);
    }

    private void publishToDevice(ApnsService apnsService, String payload, List<NurseDeviceTokensBean> deviceTokens) {
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
        if(customFields != null) {
            for (Map.Entry<String, String> entry : customFields.entrySet()) {
                badge = badge.customField(entry.getKey(), entry.getValue());
            }
        }
        return badge.actionKey(action).build();
    }

}
