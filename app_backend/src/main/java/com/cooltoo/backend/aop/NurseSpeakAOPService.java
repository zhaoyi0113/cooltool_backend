package com.cooltoo.backend.aop;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.entities.NurseIntegrationEntity;
import com.cooltoo.backend.repository.NurseIntegrationRepository;
import com.cooltoo.backend.services.NurseIntegrationService;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.backend.services.notification.NotificationCenter;
import com.cooltoo.backend.services.notification.NotificationCode;
import com.cooltoo.backend.services.notification.NotificationType;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.exception.BadRequestException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by yzzhao on 5/20/16.
 */
@Aspect
@Component
public class NurseSpeakAOPService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakAOPService.class);

    @Autowired
    private NotificationCenter notificationCenter;

    @Autowired
    private NurseService nurseService;

    @Autowired
    private NurseIntegrationService integrationService;

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.addSmug(..))",
            returning = "retVal")
    public void addSmug(JoinPoint joinPoint, NurseSpeakBean retVal) {
        sendPublishNotification(retVal, NotificationCode.PUBLISH_SMUG_SPEAK_CODE);
        updateNurseIntegration(joinPoint, SpeakType.SMUG, retVal);
    }

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.addCathart(..))",
            returning = "retVal")
    public void addCathart(JoinPoint joinPoint, NurseSpeakBean retVal) {
        sendPublishNotification(retVal, NotificationCode.PUBLISH_COMPLAIN_SPEAK_CODE);
        updateNurseIntegration(joinPoint, SpeakType.CATHART, retVal);
    }

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.addAskQuestion(..))",
            returning = "retVal")
    public void addAskQuestion(JoinPoint joinPoint, NurseSpeakBean retVal) {
        sendPublishNotification(retVal, NotificationCode.PUBLISH_QUESTION_SPEAK_CODE);
        updateNurseIntegration(joinPoint, SpeakType.ASK_QUESTION, retVal);
    }

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.deleteByIds(..))",
            returning = "speaks")
    public void deleteNurseSpeak(JoinPoint joinPoint, List<NurseSpeakBean> speaks){
        for(NurseSpeakBean bean : speaks) {
            integrationService.deleteNurseSpeak(bean.getId());
        }
    }

    private void sendPublishNotification(NurseSpeakBean retVal, String code) {
        if (retVal == null) {
            return;
        }
        Map<String, String> fields = new Hashtable<>();
        fields.put(NotificationCode.SPEAK_ID_FIELD, String.valueOf(retVal.getId()));
        String bodyText = "";
        notificationCenter.publishToAllDevices(bodyText, fields, code, NotificationType.SILENT);
    }

    private void updateNurseIntegration(JoinPoint joinPoint, SpeakType type, NurseSpeakBean nurseSpeakBean) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length < 2) {
            return;
        }
        try{
            long userId = Long.parseLong(args[0].toString());
            integrationService.nurseSpeakIntegration(userId, type, nurseSpeakBean);
        }catch(NumberFormatException e){
            logger.error(e.getMessage());
        }

    }

}
