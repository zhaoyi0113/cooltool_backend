package com.cooltoo.backend.aop;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.services.notification.NotificationCenter;
import com.cooltoo.backend.services.notification.NotificationCode;
import com.cooltoo.backend.services.notification.NotificationType;
import com.sun.nio.sctp.Notification;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by yzzhao on 5/20/16.
 */
@Aspect
@Component
public class NurseSpeakAOPService {

    @Autowired
    private NotificationCenter notificationCenter;

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.addSmug(..))",
            returning = "retVal")
    public void addSmug(JoinPoint joinPoint, NurseSpeakBean retVal){
        if(retVal == null){
            return;
        }
        Map<String, String> fields =new Hashtable<>();
        fields.put(NotificationCode.SPEAK_ID_FIELD, String.valueOf(retVal.getId()));
        String bodyText = "发布一条臭美";
        notificationCenter.publishToAllDevices(bodyText, fields, NotificationCode.PUBLISH_SMUG_SPEAK_CODE, NotificationType.SILENT);
    }
}
