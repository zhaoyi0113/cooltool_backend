package com.cooltoo.go2nurse.service.notification;

import com.cooltoo.features.AppFeatures;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yzzhao on 4/29/16.
 */
@Component
public class AppleNotifier {

    private static final Logger logger = LoggerFactory.getLogger(AppleNotifier.class);

    private static final String P12_PASSWORD = "!Yqt0529*";
    private static final String CUSTOM_MESSAGE_JSON = "custom_message_json";

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${apple_push_notification_certificates}")
    private String P12_CER_FILE;

    @Autowired private Go2NurseUtility utility;

    public void publishToDevice(String deviceToken, MessageBean customJson, AppleNotificationType type) {
        logger.info("deviceToken={} receive customJson={} notificationType={}", deviceToken, customJson, type);
        List<String> tokens = new ArrayList<>();
        tokens.add(deviceToken);
        publishToDevices(tokens, customJson, type);
    }

    public void publishToDevices(List<String> tokens, MessageBean customJson, AppleNotificationType type) {
        logger.info("receive customJson={} notificationType={} tokens={}", customJson, type, tokens);
        if (null==tokens || tokens.isEmpty()) {
            logger.warn("the tokens doesn't have any registered device.");
            return;
        }
        ApnsService apnsService = createAPNSService();
        String payload = createPayload(customJson, type);
        if(payload != null) {
            publishToDevice(apnsService, payload, tokens);
        }
    }

    private void publishToDevice(final ApnsService apnsService, final String payload, List<String> deviceTokens) {
        if(!AppFeatures.APNS.isActive()){
            return;
        }
        for (final String token : deviceTokens) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    logger.info("push to device " + payload + "," + token);
                    apnsService.push(token, payload);
                }
            });
        }
    }

    private ApnsService createAPNSService() {
        return APNS.newService().withCert(getClass().
                getResourceAsStream(P12_CER_FILE), P12_PASSWORD).
                withSandboxDestination().build();
    }

    private String createPayload(MessageBean customJson, AppleNotificationType type) {
        String payload = null;
        switch (type){
            case ALERT:
                payload = createAlertPayload(customJson);
                break;
            case SILENT:
                payload = createSilentPayload(customJson);
                break;
        }
        return payload;
    }

    private String createAlertPayload(MessageBean customJson) {
        PayloadBuilder payloadBuilder = APNS.newPayload().alertBody(customJson.getAlertBody()).sound("default");
        payloadBuilder.customField(CUSTOM_MESSAGE_JSON, utility.toJsonString(customJson));
        return payloadBuilder.build();
    }

    private String createSilentPayload(MessageBean customJson) {
        PayloadBuilder payloadBuilder = APNS.newPayload().instantDeliveryOrSilentNotification();
        payloadBuilder.customField(CUSTOM_MESSAGE_JSON, utility.toJsonString(customJson));
        return payloadBuilder.build();
    }

    public static enum AppleNotificationType {
        ALERT, SILENT
    }
}
