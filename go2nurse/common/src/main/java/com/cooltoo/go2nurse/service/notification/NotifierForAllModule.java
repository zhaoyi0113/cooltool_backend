package com.cooltoo.go2nurse.service.notification;

import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.beans.NurseWalletBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.constants.WalletProcess;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import com.cooltoo.go2nurse.repository.NurseOrderRelationRepository;
import com.cooltoo.go2nurse.service.NurseWalletService;
import com.cooltoo.util.NetworkUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/17.
 */
@Service("NotifierForAllModule")
public class NotifierForAllModule {

    public static final String NEW_COURSE_ALERT_BODY = "你有一个新课程！";
    public static final String NEW_NOTIFICATION_ALERT_BODY = "你有一条新通知！";
    public static final String NEW_ORDER_TO_DISPATCH_ALERT_BODY = "您有新订单，请尽快分派！";
    public static final String NEW_ORDER_ALERT_BODY = "有新订单，快来抢！";
    public static final String ORDER_ALERT_BODY = "订单状态有更新";
    public static final String APPOINTMENT_ALERT_BODY = "预约状态有更新";
    public static final String CONSULTATION_ALERT_BODY = "你有一条回复";
    public static final String PUSH_COURSE_ALERT_BODY = "你有一条定制推送";
    public static final String FOLLOW_UP_ALERT_BODY = "你有一条随访记录";
    public static final String FOLLOW_UP_REPLY_ALERT_BODY = "你有一条随访回复";
    public static final String WITHDRAW_COMPLETED_ALERT_BODY = "提现成功，请查收";
    public static final String WITHDRAW_REFUSED_ALERT_BODY = "拒绝提现";


    @Value("${nurse360_notifier_url}")
    private String nurse360NotifierUrl;
    @Value("${go2nurse_notifier_url}")
    private String go2nurseNotifierUrl;

    @Autowired private Notifier notifier;
    @Autowired private NurseOrderRelationRepository nurseOrderRelation;
    @Autowired private NurseWalletService nurseWalletService;


    //========================================================================================
    //
    //                              LeanCloud Request SMS Code
    //
    //========================================================================================
    public void withdrawAlertToNurse360(long nurseId, long withdrawRecordId, String description) {
        NurseWalletBean walletFlowRecord = nurseWalletService.getNurseWalletRecord(withdrawRecordId);
        if (null!=walletFlowRecord) {
            WalletProcess process = walletFlowRecord.getProcess();
            String body = WalletProcess.COMPLETED.equals(process)
                    ? WITHDRAW_COMPLETED_ALERT_BODY
                    : WITHDRAW_REFUSED_ALERT_BODY;
            MessageBean messageBean = notifier.createMessage(
                    MessageType.NURSE_WITHDRAW,
                    body,
                    withdrawRecordId,
                    process.name(),
                    VerifyUtil.isStringEmpty(description) ? ("withdraw " + process.name().toLowerCase() + "!") : description
            );

            StringBuilder msg = messageBean.toHtmlParam();
            msg.append("&nurse_id=").append(nurseId);
            NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
        }
    }

    //========================================================================================
    //
    //                              LeanCloud Request SMS Code
    //
    //========================================================================================
    public void leanCloudRequestSmsCodeWithdrawSuccess(List<String> mobiles, String summary) {
        Map<String, String> param = new HashMap<>();
        param.put("summary", summary);
        notifier.leanCloudRequestSmsCode(mobiles, MessageBean.LEANCLOUD_MSG_TEMPLATE_QSHL_WITHDRAW_SUCCESS, param);
    }

    public void leanCloudRequestSmsCodeWithdrawRefused(List<String> mobiles, String reason) {
        Map<String, String> param = new HashMap<>();
        param.put("reason", reason);
        notifier.leanCloudRequestSmsCode(mobiles, MessageBean.LEANCLOUD_MSG_TEMPLATE_QSHL_WITHDRAW_REFUSED, param);
    }

    public void leanCloudRequestSmsCodeNewOrder(List<String> mobiles, String orderNo) {
        Map<String, String> param = new HashMap<>();
        param.put("orderNo", orderNo);
        notifier.leanCloudRequestSmsCode(mobiles, MessageBean.LEANCLOUD_MSG_TEMPLATE_QSHL_NEW_ORDER, param);
    }

    public void leanCloudRequestSmsCodeRedispatch(List<String> mobiles, String orderNo) {
        Map<String, String> param = new HashMap<>();
        param.put("orderNo", orderNo);
        notifier.leanCloudRequestSmsCode(mobiles, MessageBean.LEANCLOUD_MSG_TEMPLATE_QSHL_REDISPATCH, param);
    }


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

    public void newOrderToDispatchAlertToNurse360(List<Long> nurseIds, long orderId, OrderStatus orderStatus, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.ORDER,
                NEW_ORDER_TO_DISPATCH_ALERT_BODY,
                orderId,
                orderStatus.name(),
                VerifyUtil.isStringEmpty(description) ? ("order " + orderStatus.name().toLowerCase() + "!") : description
        );

        if (null!=nurseIds && !nurseIds.isEmpty()) {
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

    public void followUpAlertToGo2nurseUser(PatientFollowUpType followUpType, long userId, long followUpRecordId, long relativeId, String status, String description) {
        MessageBean messageBean = notifier.createMessage(
                PatientFollowUpType.CONSULTATION.equals(followUpType)
                        ? MessageType.FOLLOW_UP_CONSULTATION
                        : MessageType.FOLLOW_UP_QUESTIONNAIRE,
                FOLLOW_UP_ALERT_BODY,
                relativeId,
                status,
                VerifyUtil.isStringEmpty(description) ? ("follow-up " + followUpType.name().toLowerCase() + "!") : description
        );
        messageBean.setProperties("followUpRecordId", followUpRecordId);

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&user_id=").append(userId);
        NetworkUtil.newInstance().httpsRequest(go2nurseNotifierUrl, "PUT", msg.toString());
    }

    public void followUpAlertToNurse360(PatientFollowUpType followUpType, long nurseId, long followUpRecordId, long relativeId, String status, String description) {
        MessageBean messageBean = notifier.createMessage(
                PatientFollowUpType.CONSULTATION.equals(followUpType)
                        ? MessageType.FOLLOW_UP_CONSULTATION
                        : MessageType.FOLLOW_UP_QUESTIONNAIRE,
                FOLLOW_UP_REPLY_ALERT_BODY,
                relativeId,
                status,
                VerifyUtil.isStringEmpty(description) ? ("follow-up " + followUpType.name().toLowerCase() + " replied!") : description
        );
        messageBean.setProperties("followUpRecordId", followUpRecordId);

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&nurse_id=").append(nurseId);
        NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
    }

    public void followUpTalkAlertToGo2nurseUser(long userId, long relativeId, String status, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.FOLLOW_UP_CONSULTATION_TALK,
                FOLLOW_UP_ALERT_BODY,
                relativeId,
                status,
                VerifyUtil.isStringEmpty(description) ? ("follow-up consultation talk!") : description
        );

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&user_id=").append(userId);
        NetworkUtil.newInstance().httpsRequest(go2nurseNotifierUrl, "PUT", msg.toString());
    }

    public void followUpTalkAlertToNurse360(long nurseId, long followUpRecordId, long relativeId, String status, String description) {
        MessageBean messageBean = notifier.createMessage(
                MessageType.FOLLOW_UP_CONSULTATION_TALK,
                FOLLOW_UP_REPLY_ALERT_BODY,
                relativeId,
                status,
                VerifyUtil.isStringEmpty(description) ? ("follow-up consultation talk replied!") : description
        );
        messageBean.setProperties("followUpRecordId", followUpRecordId);

        StringBuilder msg = messageBean.toHtmlParam();
        msg.append("&nurse_id=").append(nurseId);
        NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
    }


    //========================================================================================
    //
    //                          Push notification Message to Nurse
    //
    //========================================================================================
    public void newNotificationAlertToNurse360(List<Long> nurseIds, long notificationId, String status, String description) {
        if (null!=nurseIds && !nurseIds.isEmpty()) {
            MessageBean messageBean = notifier.createMessage(
                    MessageType.NURSE_NOTIFICATION,
                    NEW_NOTIFICATION_ALERT_BODY,
                    notificationId,
                    status,
                    VerifyUtil.isStringEmpty(description) ? ("new notification !") : description
            );

            StringBuilder msg = messageBean.toHtmlParam();
            msg.append("&nurse_id=0");
            for (Long tmpId : nurseIds) {
                msg.append(",").append(tmpId);
            }
            NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
        }
    }


    //========================================================================================
    //
    //                          Push Course Message to Nurse
    //
    //========================================================================================
    public void newCourseAlertToNurse360(List<Long> nurseIds, long courseId, String status, String description) {
        if (null!=nurseIds && !nurseIds.isEmpty()) {
            MessageBean messageBean = notifier.createMessage(
                    MessageType.NURSE_COURSE_LEARN,
                    NEW_COURSE_ALERT_BODY,
                    courseId,
                    status,
                    VerifyUtil.isStringEmpty(description) ? ("new course !") : description
            );

            StringBuilder msg = messageBean.toHtmlParam();
            msg.append("&nurse_id=0");
            for (Long tmpId : nurseIds) {
                msg.append(",").append(tmpId);
            }
            NetworkUtil.newInstance().httpsRequest(nurse360NotifierUrl, "PUT", msg.toString());
        }
    }
}
