package com.cooltoo.go2nurse.service.notification;

import com.cooltoo.beans.NurseDeviceTokensBean;
import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.beans.UserDeviceTokensBean;
import com.cooltoo.go2nurse.service.UserDeviceTokensService;
import com.cooltoo.services.NurseDeviceTokensService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/9/14.
 */
@Component
public class Notifier {

    @Autowired private AppleNotifier appleNotifier;
    @Autowired private LeanCloudNotifier leanCloudNotifier;
    @Autowired private UserDeviceTokensService userDeviceTokensService;
    @Autowired private NurseDeviceTokensService nurseDeviceTokensService;

    public void notifyUserPatient(long userId, MessageBean message) {
        if (null==message) {
            return;
        }
        List<UserDeviceTokensBean> tokens = userDeviceTokensService.getUserDeviceTokens(userId);
        if (!VerifyUtil.isListEmpty(tokens)) {
            List<String> iosTokens = new ArrayList<>();
            List<String> androidTokens = new ArrayList<>();
            for (UserDeviceTokensBean tmp : tokens) {
                if (DeviceType.Android.equals(tmp.getDeviceType())) {
                    androidTokens.add(tmp.getDeviceToken());
                }
                if (DeviceType.iOS.equals(tmp.getDeviceType())) {
                    iosTokens.add(tmp.getDeviceToken());
                }
            }
            notifyDevice(androidTokens, iosTokens, message);
        }
    }

    public void notifyNurse(long nurseId, MessageBean message) {
        if (null==message) {
            return;
        }
        List<NurseDeviceTokensBean> tokens = nurseDeviceTokensService.getNurseDeviceTokens(nurseId);
        if (!VerifyUtil.isListEmpty(tokens)) {
            List<String> iosTokens = new ArrayList<>();
            List<String> androidTokens = new ArrayList<>();
            for (NurseDeviceTokensBean tmp : tokens) {
                if (DeviceType.Android.equals(tmp.getDeviceType())) {
                    androidTokens.add(tmp.getDeviceToken());
                }
                if (DeviceType.iOS.equals(tmp.getDeviceType())) {
                    iosTokens.add(tmp.getDeviceToken());
                }
            }
            notifyDevice(androidTokens, iosTokens, message);
        }
    }

    private void notifyDevice(List<String> androidTokens, List<String> iosTokens, MessageBean message) {
        if (null==message) {
            return;
        }
        if (!VerifyUtil.isListEmpty(androidTokens)) {
            leanCloudNotifier.publishToDevices(androidTokens, null, message);
        }
        if (!VerifyUtil.isListEmpty(iosTokens)) {
            appleNotifier.publishToDevices(iosTokens, message, AppleNotifier.AppleNotificationType.ALERT);
        }
    }

    public MessageBean createMessage(MessageType type, String alertBody, long relativeId, String status, String description) {
        MessageBean message = new MessageBean();
        message.setType(type.name());
        message.setAlertBody(alertBody);
        message.setRelativeId(relativeId);
        message.setStatus(status);
        message.setDescription(description);
        return message;
    }
}
