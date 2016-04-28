package com.cooltoo.backend.aop;

import com.cooltoo.backend.services.NurseSpeakService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by yzzhao on 4/28/16.
 */
@Aspect
@Component
public class NurseFriendAOPService {

    private static final Logger logger = LoggerFactory.getLogger(NurseFriendAOPService.class);


    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseFriendsService.setFriendship(..))",
        returning = "retVal")
    public void afterSetFriendship(JoinPoint joinPoint, boolean retVal){
        logger.info("set friend "+retVal);
        Object[] args = joinPoint.getArgs();
        for(Object obj : args){
            logger.info(obj.toString());
        }
    }


}
