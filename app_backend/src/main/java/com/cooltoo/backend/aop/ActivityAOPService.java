package com.cooltoo.backend.aop;

import com.cooltoo.backend.services.notification.NotificationCenter;
import com.cooltoo.backend.services.notification.NotificationCode;
import com.cooltoo.beans.ActivityBean;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by yzzhao on 5/2/16.
 */
@Aspect
@Component
public class ActivityAOPService {


    @Autowired
    private NotificationCenter notificationCenter;

    @AfterReturning(pointcut = "execution(* com.cooltoo.services.ActivityService.createActivity(..))",
            returning = "retVal")
    public void afterCreateActivity(JoinPoint joinPoint, ActivityBean retVal){
        if (retVal != null) {
            String bodyText = "官方发布新活动 "+retVal.getTitle();
            notificationCenter.publishToAllDevices(bodyText, new HashMap<String, String>(), NotificationCode.PUBLISH_ACTIVITY);
        }
    }

}
