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
        sendPublishNotification(retVal,NotificationCode.PUBLISH_SMUG_SPEAK_CODE);
    }

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.addCathart(..))",
            returning = "retVal")
    public void addCathart(JoinPoint joinPoint, NurseSpeakBean retVal){
        sendPublishNotification(retVal,NotificationCode.PUBLISH_COMPLAIN_SPEAK_CODE);
    }

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.addAskQuestion(..))",
            returning = "retVal")
    public void addAskQuestion(JoinPoint joinPoint, NurseSpeakBean retVal){
        sendPublishNotification(retVal,NotificationCode.PUBLISH_QUESTION_SPEAK_CODE);
    }

    public void sendPublishNotification(NurseSpeakBean retVal, String code) {
        if(retVal == null){
            return;
        }
        Map<String, String> fields =new Hashtable<>();
        fields.put(NotificationCode.SPEAK_ID_FIELD, String.valueOf(retVal.getId()));
        String bodyText = "";
        notificationCenter.publishToAllDevices(bodyText, fields, code, NotificationType.SILENT);
    }


}
