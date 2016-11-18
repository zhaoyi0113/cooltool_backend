package com.cooltoo.go2nurse.service.notification;

import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import com.cooltoo.go2nurse.repository.NurseOrderRelationRepository;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/17.
 */
@Service("NotifierForAllModule")
public class NotifierForAllModule {

    public static final String NEW_ORDER_ALERT_BODY = "有新订单，快来抢！";
    public static final String ORDER_ALERT_BODY = "订单状态有更新";
    public static final String APPOINTMENT_ALERT_BODY = "预约状态有更新";
    public static final String CONSULTATION_ALERT_BODY = "你有一条回复";
    public static final String PUSH_COURSE_ALERT_BODY = "你有一条定制推送";
    public static final String FOLLOW_UP_ALERT_BODY = "你有一条随访记录";
    public static final String FOLLOW_UP_REPLY_ALERT_BODY = "你有一条随访回复";


    @Autowired private Notifier notifier;
    @Autowired private NurseOrderRelationRepository nurseOrderRelation;


    //========================================================================================
    //
    //                                 Order Message Pushing
    //
    //========================================================================================
    public void orderAlertToNurse(long orderId, OrderStatus orderStatus, String description) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<NurseOrderRelationEntity> nurses = nurseOrderRelation.findByOrderId(orderId, sort);
        if (!VerifyUtil.isListEmpty(nurses)) {
            MessageBean messageBean = notifier.createMessage(
                    MessageType.ORDER,
                    ORDER_ALERT_BODY,
                    orderId,
                    orderStatus.name(),
                    VerifyUtil.isStringEmpty(description) ? ("order " + orderStatus.name().toLowerCase() + "!") : description
            );
            for (NurseOrderRelationEntity tmp : nurses) {
                notifier.notifyNurse(tmp.getNurseId(), messageBean);
            }
        }
    }

    public void orderAlertToNurse(long nurseId, long orderId, OrderStatus orderStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.ORDER,
                ORDER_ALERT_BODY,
                orderId,
                orderStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("order " + orderStatus.name().toLowerCase() + "!") : description
        );
        notifier.notifyNurse(nurseId, messageBean);
    }

    public void newOrderAlertToNurse(List<Long> nurseIds, long orderId, OrderStatus orderStatus, String description) {
        if (null!=nurseIds && !nurseIds.isEmpty()) {
            for (Long tmpId : nurseIds) {
                MessageBean messageBean = notifier.createMessage(
                        MessageType.ORDER,
                        NEW_ORDER_ALERT_BODY,
                        orderId,
                        orderStatus.name(),
                        VerifyUtil.isStringEmpty(description) ? ("order " + orderStatus.name().toLowerCase() + "!") : description
                );
                notifier.notifyNurse(tmpId, messageBean);
            }
        }
    }

    public void orderAlertToPatient(long userId, long orderId, OrderStatus orderStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.ORDER,
                ORDER_ALERT_BODY,
                orderId,
                orderStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("order " + orderStatus.name().toLowerCase() + "!") : description
        );

        notifier.notifyUserPatient(userId, messageBean);
    }


    //========================================================================================
    //
    //                                 Appointment Message Pushing
    //
    //========================================================================================
    public void appointmentAlertToPatient(long userId, long appointmentId, OrderStatus orderStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.APPOINTMENT,
                APPOINTMENT_ALERT_BODY,
                appointmentId,
                orderStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("appointment " + orderStatus.name().toLowerCase() + "!") : description
        );

        notifier.notifyUserPatient(userId, messageBean);
    }


    //========================================================================================
    //
    //                                 Consultation Message Pushing
    //
    //========================================================================================
    public void consultationAlertToNurse(long nurseId, long consultationId, ConsultationTalkStatus talkStatus, String talkContent) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.CONSULTATION_TALK,
                CONSULTATION_ALERT_BODY,
                consultationId,
                talkStatus.name(),
                VerifyUtil.isStringEmpty(talkContent) ? ("talk " + talkStatus.name().toLowerCase() + "!") : talkContent
        );

        notifier.notifyNurse(nurseId, messageBean);
    }

    public void consultationAlertToPatient(long userId, long consultationId, ConsultationTalkStatus talkStatus, String talkContent) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.CONSULTATION_TALK,
                CONSULTATION_ALERT_BODY,
                consultationId,
                talkStatus.name(),
                VerifyUtil.isStringEmpty(talkContent) ? ("talk " + talkStatus.name().toLowerCase() + "!") : talkContent
        );

        notifier.notifyUserPatient(userId, messageBean);
    }


    //========================================================================================
    //
    //                                 Push-Course Message Pushing
    //
    //========================================================================================

    public void pushCourseAlertToPatient(long userId, long courseId, ReadingStatus readingStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.PUSH_COURSE,
                PUSH_COURSE_ALERT_BODY,
                courseId,
                readingStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("push-course " + readingStatus.name().toLowerCase() + "!") : description
        );

        notifier.notifyUserPatient(userId, messageBean);
    }


    //========================================================================================
    //
    //                                 Follow-up Message Pushing
    //
    //========================================================================================

    public void followUpAlertToPatient(PatientFollowUpType followUpType, long userId, long relativeId, String status, String description) {
        MessageBean messageBean = notifier.createMessage(
                PatientFollowUpType.CONSULTATION.equals(followUpType)
                        ? MessageType.FOLLOW_UP_CONSULTATION
                        : MessageType.FOLLOW_UP_QUESTIONNAIRE,
                FOLLOW_UP_ALERT_BODY,
                relativeId,
                status,
                VerifyUtil.isStringEmpty(description) ? ("follow-up " + followUpType.name().toLowerCase() + "!") : description
        );

        notifier.notifyUserPatient(userId, messageBean);
    }

    public void followUpAlertToNurse(PatientFollowUpType followUpType, long nurseId, long relativeId, String status, String description) {
        MessageBean messageBean = notifier.createMessage(
                PatientFollowUpType.CONSULTATION.equals(followUpType)
                        ? MessageType.FOLLOW_UP_CONSULTATION
                        : MessageType.FOLLOW_UP_QUESTIONNAIRE,
                FOLLOW_UP_REPLY_ALERT_BODY,
                relativeId,
                status,
                VerifyUtil.isStringEmpty(description) ? ("follow-up " + followUpType.name().toLowerCase() + " replied!") : description
        );

        notifier.notifyNurse(nurseId, messageBean);
    }
}
