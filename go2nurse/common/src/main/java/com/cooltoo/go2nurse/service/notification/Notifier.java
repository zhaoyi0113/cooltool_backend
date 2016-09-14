package com.cooltoo.go2nurse.service.notification;

import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.beans.UserDeviceTokensBean;
import com.cooltoo.go2nurse.service.UserDeviceTokensService;
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

    public void notifyUserPatient(long userId, MessageBean message) {
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
            if (!VerifyUtil.isListEmpty(androidTokens)) {
                leanCloudNotifier.publishToDevices(androidTokens, null, message);
            }
            if (!VerifyUtil.isListEmpty(androidTokens)) {
                appleNotifier.publishToDevices(androidTokens, message, AppleNotifier.AppleNotificationType.ALERT);
            }
        }
    }
}
