package com.cooltoo.backend.services.notification;

import com.cooltoo.backend.beans.NurseDeviceTokensBean;
import com.cooltoo.backend.services.NurseDeviceTokensService;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yzzhao on 4/29/16.
 */
@Component
public class NotificationCenter {

    private static final String P12_CER_FILE = "/CertificatesPushNotification.p12";

    private static final String P12_PASSWORD = "!Yqt0529*";

    private static final Logger logger = LoggerFactory.getLogger(NotificationCenter.class);

    private static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private NurseDeviceTokensService deviceTokensService;

    public void publishToAllDevices(String bodyText, Map<String, String> customFields, String actionCode, NotificationType type) {
        List<NurseDeviceTokensBean> deviceTokens = deviceTokensService.getAllActiveDeviceTokens();
        if (deviceTokens.isEmpty()) {
            logger.warn("there is no device registered.");
            return;
        }
        ApnsService apnsService = createAPNSService(bodyText);
        String payload = createPayload(bodyText, customFields, actionCode, type);
        if (payload != null) {
            publishToDevice(apnsService, payload, deviceTokens);
        }
    }

    private String createPayload(String bodyText, Map<String, String> customFields, String actionCode, NotificationType type) {
        String payload = null;
        switch (type){
            case ALERT:
                payload = createAlertPayload(bodyText, customFields, actionCode);
                break;
            case SILENT:
                payload = createSilentPayload(bodyText, customFields, actionCode);
                break;
        }
        return payload;
    }


    public void publishToUser(long userId, String bodyText, Map<String, String> customFields, String actionCode, NotificationType type) {
        List<NurseDeviceTokensBean> tokens = deviceTokensService.getNurseDeviceTokens(userId);
        logger.info("user={} receive text={} customFields={} actionCode={} notificationType={} tokens={}",
                userId, bodyText, customFields, actionCode, type, tokens);
        if (tokens.isEmpty()) {
            logger.warn("the user " + userId + " doesn't have any registered device.");
            return;
        }
        ApnsService apnsService = createAPNSService(bodyText);
        String payload = createPayload(bodyText, customFields, actionCode, type);
        if(payload != null) {
            publishToDevice(apnsService, payload, tokens);
        }
    }

    private void publishToDevice(final ApnsService apnsService, final String payload, List<NurseDeviceTokensBean> deviceTokens) {
        for (final NurseDeviceTokensBean token : deviceTokens) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    logger.debug("push to device " + payload + "," + token.getDeviceToken());
                    apnsService.push(token.getDeviceToken(), payload);
                }
            });

        }
    }

    private ApnsService createAPNSService(String bodyText) {
        return APNS.newService().withCert(getClass().
                getResourceAsStream(P12_CER_FILE), P12_PASSWORD).
                withSandboxDestination().build();
    }

    private String createAlertPayload(String bodyText, Map<String, String> customFields, String action) {
        PayloadBuilder payloadBuilder = APNS.newPayload().alertBody(bodyText).sound("default").customField(NOTIFICATION_TYPE, action);
        payloadBuilder = updatePayloadBuilder(customFields, payloadBuilder);
        return payloadBuilder.build();
    }

    private String createSilentPayload(String bodyText, Map<String, String> customFields, String action) {
        PayloadBuilder payloadBuilder = APNS.newPayload().instantDeliveryOrSilentNotification().customField(NOTIFICATION_TYPE, action);
        payloadBuilder = updatePayloadBuilder(customFields, payloadBuilder);
        return payloadBuilder.build();
    }

    private PayloadBuilder updatePayloadBuilder(Map<String, String> customFields, PayloadBuilder payloadBuilder) {
        if (customFields != null) {
            for (Map.Entry<String, String> entry : customFields.entrySet()) {
                payloadBuilder = payloadBuilder.customField(entry.getKey(), entry.getValue());
            }
        }
        return payloadBuilder;
    }

}
