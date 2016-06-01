package com.cooltoo.backend.aop;

import com.cooltoo.backend.services.NurseService;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 4/29/16.
 */
@Aspect
@Component
public class NurseUserIDChecker {

    private static final Logger logger = LoggerFactory.getLogger(NurseUserIDChecker.class);

    public static final int ANONYMOUS_USER_ID = -1;

    @Autowired
    private NurseService nurseService;

    @Before("execution(* com.cooltoo.backend.services.NurseDeviceTokensService.inactiveUserDeviceToken(..))")
    public void inactiveUserDeviceToken(JoinPoint joinPoint) {
        checkUserExisted(joinPoint);
    }

    @Before("execution(* com.cooltoo.backend.services.NurseDeviceTokensService.registerUserDeviceToken(..))")
    public void registerUserDeviceToken(JoinPoint joinPoint) {
        checkUserExisted(joinPoint);
    }

    private void checkUserExisted(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            try {
                long userId = Long.parseLong(String.valueOf(args[0]));
                if (!nurseService.existNurse(userId) && userId != ANONYMOUS_USER_ID) {
                    throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
                }
            } catch (NumberFormatException e) {
                logger.error(e.getMessage());
                throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
            }
        }
    }

}
