package com.cooltoo.go2nurse.service.notification;

import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import com.cooltoo.go2nurse.repository.NurseOrderRelationRepository;
import com.cooltoo.go2nurse.util.HttpUtils;
import com.cooltoo.util.NetworkUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${nurse360_notifier_url}")
    private String nurse360NotifierUrl;
    @Value("${go2nurse_notifier_url}")
    private String go2nurseNotifierUrl;

    @Autowired private Notifier notifier;
    @Autowired private NurseOrderRelationRepository nurseOrderRelation;


    //========================================================================================
    //
    //                                 Order Message Pushing
    //
    //========================================================================================
    public void orderAlertToNurse360(long orderId, OrderStatus orderStatus, String description) {
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

            StringBuilder msg = messageBean.toHtmlParam();
            msg.append("&nurse_id=0");
            for (NurseOrderRelationEntity tmp : nurses) {
                msg.append(",").append(tmp.getNurseId());
            }
            NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
        }
    }

    public void orderAlertToNurse360(long nurseId, long orderId, OrderStatus orderStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.ORDER,
                ORDER_ALERT_BODY,
                orderId,
                orderStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("order " + orderStatus.name().toLowerCase() + "!") : description
        );

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&nurse_id=").append(nurseId);
        NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
    }

    public void newOrderAlertToNurse360(List<Long> nurseIds, long orderId, OrderStatus orderStatus, String description) {
        if (null!=nurseIds && !nurseIds.isEmpty()) {
            MessageBean messageBean = notifier.createMessage(
                    MessageType.ORDER,
                    NEW_ORDER_ALERT_BODY,
                    orderId,
                    orderStatus.name(),
                    VerifyUtil.isStringEmpty(description) ? ("order " + orderStatus.name().toLowerCase() + "!") : description
            );

            StringBuilder msg = messageBean.toHtmlParam();
            msg.append("&nurse_id=0");
            for (Long tmpId : nurseIds) {
                msg.append(",").append(tmpId);
            }
            NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
        }
    }

    public void orderAlertToGo2nurseUser(long userId, long orderId, OrderStatus orderStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.ORDER,
                ORDER_ALERT_BODY,
                orderId,
                orderStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("order " + orderStatus.name().toLowerCase() + "!") : description
        );

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&user_id=").append(userId);
        NetworkUtil.newInstance().httpsRequest(go2nurseNotifierUrl, "PUT", msg.toString());
    }


    //========================================================================================
    //
    //                                 Appointment Message Pushing
    //
    //========================================================================================
    public void appointmentAlertToGo2nurseUser(long userId, long appointmentId, OrderStatus orderStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.APPOINTMENT,
                APPOINTMENT_ALERT_BODY,
                appointmentId,
                orderStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("appointment " + orderStatus.name().toLowerCase() + "!") : description
        );

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&user_id=").append(userId);
        NetworkUtil.newInstance().httpsRequest(go2nurseNotifierUrl, "PUT", msg.toString());
    }


    //========================================================================================
    //
    //                                 Consultation Message Pushing
    //
    //========================================================================================
    public void consultationAlertToNurse360(long nurseId, long consultationId, ConsultationTalkStatus talkStatus, String talkContent) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.CONSULTATION_TALK,
                CONSULTATION_ALERT_BODY,
                consultationId,
                talkStatus.name(),
                VerifyUtil.isStringEmpty(talkContent) ? ("talk " + talkStatus.name().toLowerCase() + "!") : talkContent
        );

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&nurse_id=").append(nurseId);
        NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
    }

    public void consultationAlertToGo2nurseUser(long userId, long consultationId, ConsultationTalkStatus talkStatus, String talkContent) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.CONSULTATION_TALK,
                CONSULTATION_ALERT_BODY,
                consultationId,
                talkStatus.name(),
                VerifyUtil.isStringEmpty(talkContent) ? ("talk " + talkStatus.name().toLowerCase() + "!") : talkContent
        );

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&user_id=").append(userId);
        NetworkUtil.newInstance().httpsRequest(go2nurseNotifierUrl, "PUT", msg.toString());
    }


    //========================================================================================
    //
    //                                 Push-Course Message Pushing
    //
    //========================================================================================

    public void pushCourseAlertToGo2nurseUser(long userId, long courseId, ReadingStatus readingStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.PUSH_COURSE,
                PUSH_COURSE_ALERT_BODY,
                courseId,
                readingStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("push-course " + readingStatus.name().toLowerCase() + "!") : description
        );

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&user_id=").append(userId);
        NetworkUtil.newInstance().httpsRequest(go2nurseNotifierUrl, "PUT", msg.toString());
    }


    //========================================================================================
    //
    //                                 Follow-up Message Pushing
    //
    //========================================================================================

    public void followUpAlertToGo2nurseUser(PatientFollowUpType followUpType, long userId, long relativeId, String status, String description) {
        MessageBean messageBean = notifier.createMessage(
                PatientFollowUpType.CONSULTATION.equals(followUpType)
                        ? MessageType.FOLLOW_UP_CONSULTATION
                        : MessageType.FOLLOW_UP_QUESTIONNAIRE,
                FOLLOW_UP_ALERT_BODY,
                relativeId,
                status,
                VerifyUtil.isStringEmpty(description) ? ("follow-up " + followUpType.name().toLowerCase() + "!") : description
        );

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&user_id=").append(userId);
        NetworkUtil.newInstance().httpsRequest(go2nurseNotifierUrl, "PUT", msg.toString());
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

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&nurse_id=").append(nurseId);
        NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
    }

}
