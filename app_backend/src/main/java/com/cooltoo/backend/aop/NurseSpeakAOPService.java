package com.cooltoo.backend.aop;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.converter.social_ability.CommentAbilityTypeConverter;
import com.cooltoo.backend.converter.social_ability.ThumbsUpAbilityTypeConverter;
import com.cooltoo.backend.entities.NurseIntegrationEntity;
import com.cooltoo.backend.repository.NurseIntegrationRepository;
import com.cooltoo.backend.services.NurseIntegrationService;
import com.cooltoo.backend.services.NurseMessageService;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.backend.services.notification.NotificationCenter;
import com.cooltoo.backend.services.notification.NotificationCode;
import com.cooltoo.backend.services.notification.NotificationType;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.util.VerifyUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Autowired private NotificationCenter notificationCenter;
    @Autowired private NurseService nurseService;
    @Autowired private NurseMessageService messageService;
    @Autowired private NurseIntegrationService integrationService;
    @Autowired private CommentAbilityTypeConverter commentAbilityTypeConverter;
    @Autowired private ThumbsUpAbilityTypeConverter thumbsUpAbilityTypeConverter;

    

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

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.addSpeakComment(..))",
            returning = "retVal")
    public void addComment(JoinPoint joinPoint, NurseSpeakCommentBean retVal) {
        updateNurseIntegration(joinPoint, retVal);
        addMessage(retVal);
        sendPublishNotification(retVal, NotificationCode.PUBLISH_MESSAGE_COUNT_CODE);
    }

    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.setNurseSpeakThumbsUp(..))",
            returning = "retVal")
    public void addComment(JoinPoint joinPoint, NurseSpeakThumbsUpBean retVal) {
        updateNurseIntegration(joinPoint, retVal);
        addMessage(retVal);
        sendPublishNotification(retVal, NotificationCode.PUBLISH_MESSAGE_COUNT_CODE);
    }


//    @AfterReturning(pointcut = "execution(* com.cooltoo.backend.services.NurseSpeakService.deleteByIds(..))",
//            returning = "speaks")
//    public void deleteNurseSpeak(JoinPoint joinPoint, List<NurseSpeakBean> speaks){
//        for(NurseSpeakBean bean : speaks) {
//            integrationService.deleteNurseSpeakIntegration(bean.getId());
//        }
//    }


    //====================================================================
    //                  积分
    //====================================================================
    // 发言计分
    private void updateNurseIntegration(JoinPoint joinPoint, SpeakType type, NurseSpeakBean nurseSpeakBean) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length < 2) {
            logger.error("the parameter for add speak is not valid ={}", joinPoint);
            return;
        }
        try{
            long userId = Long.parseLong(args[0].toString());
            integrationService.addNurseSpeakIntegration(userId, type, nurseSpeakBean);
        }catch(NumberFormatException e){
            logger.error(e.getMessage());
        }
    }

    // 评论/答题计分
    private void updateNurseIntegration(JoinPoint joinPoint, NurseSpeakCommentBean speakCommentBean) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length != 4) {
            logger.error("the parameter for add comment to speak is not valid ={}", joinPoint);
            return;
        }
        try{
            integrationService.addNurseCommentIntegration(speakCommentBean);

        }catch(NumberFormatException e){
            logger.error(e.getMessage());
        }
    }

    // 点赞/被赞计分
    private void updateNurseIntegration(JoinPoint joinPoint, NurseSpeakThumbsUpBean speakThumbsUpBean) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length != 2) {
            logger.error("the parameter for add thumbs up to speak is not valid ={}", joinPoint);
            return;
        }
        try{
            integrationService.addNurseThumbsUpIntegration(speakThumbsUpBean);
        }catch(NumberFormatException e){
            logger.error(e.getMessage());
        }
    }

    //====================================================================
    //                  添加消息
    //====================================================================
    private void addMessage(NurseSpeakCommentBean retVal) {
        logger.info("add comment message to table, value={}", retVal);
        if (null==retVal) {
            return;
        }
        SpecificSocialAbility ability = messageService.getSpeakTypeAbility(retVal.getNurseSpeakTypeId());
        if (null==ability) {
            logger.warn("speak type ability not exist, value={}", retVal);
            return;
        }
        if (SpeakType.ASK_QUESTION.equals(ability.getProperty(SpecificSocialAbility.Speak_Type))) {
            ability = commentAbilityTypeConverter.getItem(CommentAbilityTypeConverter.ANSWER);
        }
        else {
            ability = commentAbilityTypeConverter.getItem(CommentAbilityTypeConverter.COMMENT);
        }
        long speakMakerId = retVal.getSpeakMakerId();
        long commentMakerId = retVal.getCommentMakerId();
        long commentReceiverId = retVal.getCommentReceiverId();
        // 给晒图/吐槽/提问的用户添加消息
        if (speakMakerId!=commentMakerId && speakMakerId>0) {
            messageService.addMessage(speakMakerId, UserType.NURSE, ability.getAbilityType(), ability.getAbilityId(), retVal.getId());
        }
        // 给评论者指定的用户添加消息
        if (speakMakerId!=commentReceiverId && commentMakerId!=commentReceiverId && commentReceiverId>0) {
            messageService.addMessage(retVal.getCommentReceiverId(), UserType.NURSE, ability.getAbilityType(), ability.getAbilityId(), retVal.getId());
        }
    }

    private void addMessage(NurseSpeakThumbsUpBean retVal) {
        logger.info("add comment message to table, value={}", retVal);
        if (null==retVal) {
            return;
        }
        SpecificSocialAbility ability = thumbsUpAbilityTypeConverter.getItem(ThumbsUpAbilityTypeConverter.BEEN_THUMBS_UP);
        // 给晒图/吐槽/提问的用户添加消息
        long userIdBeenThumbsUp = retVal.getUserIdBeenThumbsUp();
        long thumbsUpUserId = retVal.getThumbsUpUserId();
        if (thumbsUpUserId!=userIdBeenThumbsUp && userIdBeenThumbsUp>0) {
            messageService.addMessage(retVal.getUserIdBeenThumbsUp(), UserType.NURSE, ability.getAbilityType(), ability.getAbilityId(), retVal.getId());
        }
    }

    //====================================================================
    //                  发送消息
    //====================================================================
    private void sendPublishNotification(NurseSpeakBean retVal, String code) {
        if (retVal == null) {
            return;
        }
        Map<String, String> fields = new Hashtable<>();
        fields.put(NotificationCode.SPEAK_ID_FIELD, String.valueOf(retVal.getId()));
        String bodyText = "";
        notificationCenter.publishToAllDevices(bodyText, fields, code, NotificationType.SILENT);
    }

    private void sendPublishNotification(NurseSpeakCommentBean retVal, String code) {
        if (retVal == null) {
            return;
        }
        Map<String, String> fields = new Hashtable<>();
        fields.put(NotificationCode.SPEAK_ID_FIELD, String.valueOf(retVal.getNurseSpeakId()));
        String bodyText = "你有一条回复";
        // 发送给晒图/吐槽/提问的用户
        notificationCenter.publishToUser(retVal.getSpeakMakerId(), bodyText, fields, code, NotificationType.ALERT);
        // 发送给评论者指定的用户
        if (retVal.getSpeakMakerId()!=retVal.getCommentReceiverId() && retVal.getCommentReceiverId()>0) {
            notificationCenter.publishToUser(retVal.getCommentReceiverId(), bodyText, fields, code, NotificationType.ALERT);
        }
    }

    private void sendPublishNotification(NurseSpeakThumbsUpBean retVal, String code) {
        if (retVal == null) {
            return;
        }
        Map<String, String> fields = new Hashtable<>();
        NurseBean nurse = nurseService.getNurse(retVal.getThumbsUpUserId());
        String bodyText = null==nurse ? "你获得一个赞" : (nurse.getName() + " 赞了你");
        // 发送给被点赞的用户
        notificationCenter.publishToUser(retVal.getUserIdBeenThumbsUp(), bodyText, fields, code, NotificationType.ALERT);
    }
}
