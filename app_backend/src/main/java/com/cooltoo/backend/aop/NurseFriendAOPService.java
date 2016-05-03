package com.cooltoo.backend.aop;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.services.NurseService;
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
 * Created by yzzhao on 4/28/16.
 */
@Aspect
@Component
public class NurseFriendAOPService {

    private static final Logger logger = LoggerFactory.getLogger(NurseFriendAOPService.class);

    @Autowired
    private NotificationCenter notificationCenter;

    @Autowired
    private NurseService nurseService;

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseFriendsService.addFriendship(..))",
            returning = "retVal")
    public void afterSetFriendship(JoinPoint joinPoint, boolean retVal) {
        logger.info("set friend " + retVal);
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length == 2) {
            try {
                long userId = Long.parseLong(args[0].toString());
                long friendId = Long.parseLong(args[1].toString());
                NurseBean nurse = nurseService.getNurse(userId);
                if (nurse == null) {
                    return;
                }
                String bodyText = nurse.getName() + " 请求加你为好友";
                Map<String, String> fields = new Hashtable<>();
                notificationCenter.publishToUser(friendId, bodyText, fields, String.valueOf(NotificationCode.REQUEST_ADD_FRIEND_CODE));
            } catch (NumberFormatException e) {
                logger.error(e.getMessage());
            }
        }
    }


}
