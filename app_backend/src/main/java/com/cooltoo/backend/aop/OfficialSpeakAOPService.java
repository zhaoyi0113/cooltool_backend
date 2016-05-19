package com.cooltoo.backend.aop;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.services.notification.NotificationCenter;
import com.cooltoo.backend.services.notification.NotificationCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by yzzhao on 5/2/16.
 */
@Aspect
@Component
public class OfficialSpeakAOPService {

    private static final Logger logger = LoggerFactory.getLogger(OfficialSpeakAOPService.class);

    @Autowired
    private NotificationCenter notificationCenter;

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.addOfficial(..))",
            returning = "retVal")
    public void officialSpeak(JoinPoint joinPoint, NurseSpeakBean retVal){
        if(retVal == null){
            return;
        }
        logger.info("publish a official speak "+retVal.getId());
        Map<String, String> fields =new Hashtable<>();
        fields.put(NotificationCode.SPEAK_ID_FIELD, String.valueOf(retVal.getId()));
        String bodyText = "收到一条官方发言";
        notificationCenter.publishToAllDevices(bodyText, fields, NotificationCode.OFFICIAL_SPEAK_CODE);
    }
}
