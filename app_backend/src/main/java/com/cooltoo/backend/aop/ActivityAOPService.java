package com.cooltoo.backend.aop;

import com.cooltoo.backend.services.notification.NotificationCenter;
import com.cooltoo.backend.services.notification.NotificationCode;
import com.cooltoo.beans.ActivityBean;
import com.cooltoo.constants.ActivityStatus;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by yzzhao on 5/2/16.
 */
@Aspect
@Component
public class ActivityAOPService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityAOPService.class);

    @Autowired
    private NotificationCenter notificationCenter;

    @AfterReturning(pointcut = "execution(* com.cooltoo.services.ActivityService.createActivity(..))",
            returning = "retVal")
    public void afterCreateActivity(JoinPoint joinPoint, ActivityBean retVal) {
        publishActivityNotification(retVal);
    }

    @AfterReturning(pointcut = "execution(* com.cooltoo.services.ActivityService.updateActivityStatus(..))",
            returning = "retVal")
    public void afterUpdateActivity(JoinPoint joinPoint, ActivityBean retVal) {
        publishActivityNotification(retVal);
    }

    private void publishActivityNotification(ActivityBean retVal) {
        if (retVal != null && ActivityStatus.ENABLE.equals(retVal.getStatus())) {
            logger.info("publish official activity notification");
            String bodyText = "官方发布新活动 " + retVal.getTitle();
            notificationCenter.publishToAllDevices(bodyText, new HashMap<String, String>(), NotificationCode.PUBLISH_ACTIVITY);
        }
    }


}
