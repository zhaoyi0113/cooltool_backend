package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpRecordBean;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.constants.ConsultationCreator;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.service.NursePatientFollowUpRecordService;
import com.cooltoo.go2nurse.service.NursePatientFollowUpService;
import com.cooltoo.go2nurse.service.UserConsultationService;
import com.cooltoo.go2nurse.service.notification.NotifierForAllModule;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/8.
 */
@Path("/nurse/follow-up/patient/record")
public class NursePatientFollowUpRecordAPI {

    private static final Logger logger = LoggerFactory.getLogger(NursePatientFollowUpRecordAPI.class);

    @Autowired private NursePatientFollowUpRecordService patientFollowUpRecordService;
    @Autowired private NursePatientFollowUpService patientFollowUpService;
    @Autowired private NotifierForAllModule notifierForAllModule;
    @Autowired private UserConsultationService userConsultationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getPatientFollowUp(@Context HttpServletRequest request,
                                       @QueryParam("follow_up_id") @DefaultValue("-1") long followUpId,
                                       @QueryParam("follow_up_type") @DefaultValue("") String followUpType, /* Consultation(提问), Questionnaire(发问卷) */
                                       @QueryParam("patient_replied") @DefaultValue("") String patientReplied, /* YES/NO */
                                       @QueryParam("nurse_read") @DefaultValue("") String nurseRead, /* YES/NO */
                                       @QueryParam("index") @DefaultValue("0") int pageIndex,
                                       @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NursePatientFollowUpRecordBean> visits = patientFollowUpRecordService.getPatientFollowUpRecordByFollowUpIds(
                CommonStatus.DELETED,
                PatientFollowUpType.parseString(followUpType),
                YesNoEnum.parseString(patientReplied),
                YesNoEnum.parseString(nurseRead),
                Arrays.asList(new Long[]{followUpId}),
                ConsultationTalkStatus.NURSE_SPEAK,
                NursePatientFollowUpRecordService.ORDER_BY_NURSE_READ,
                pageIndex, sizePerPage, false);
        return Response.ok(visits).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response setPatientFollowUpStatusToDelete(@Context HttpServletRequest request,
                                                     @FormParam("follow_up_record_id") @DefaultValue("0") long followUpRecordId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NursePatientFollowUpRecordBean followUpRecord = patientFollowUpRecordService.getPatientFollowUpRecordById(followUpRecordId);
        if (null!=followUpRecord) {
            NursePatientFollowUpBean followUp = patientFollowUpService.getPatientFollowUpWithoutInfo(followUpRecord.getFollowUpId());
            if (null!=followUp) {
                if (followUp.getNurseId()!=nurseId) {
                    logger.error("this follow-up={} record={} not belong to you={}", followUp.getId(), followUpRecordId, nurseId);
                    throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
                }
            }
        }
        List<Long> updateIds = patientFollowUpRecordService.setDeleteStatusPatientFollowUpRecordByIds(Arrays.asList(new Long[]{followUpRecordId}));
        return Response.ok(updateIds).build();
    }

    @Path("/read")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response updatePatientFollowUp(@Context HttpServletRequest request,
                                          @FormParam("follow_up_record_id") @DefaultValue("0") long followUpRecordId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        long followUpRecord = patientFollowUpRecordService.updatePatientFollowUpRecordById(followUpRecordId, null, YesNoEnum.YES, null);
        return Response.ok(followUpRecord).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addPatientFollowUp(@Context HttpServletRequest request,
                                       @FormParam("follow_up_id") @DefaultValue("0") long followUpId,
                                       @FormParam("follow_up_type") @DefaultValue("") String followUpType, /* Consultation(提问), Questionnaire(发问卷) */
                                       @FormParam("consultation_id") @DefaultValue("0") long consultationId,
                                       @FormParam("questionnaire_id") @DefaultValue("0") long questionnaireId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NursePatientFollowUpBean followUp = patientFollowUpService.getPatientFollowUpWithoutInfo(followUpId);

        if (null!=followUp && followUp.getNurseId()!=nurseId) {
            logger.error("this follow-up={} not belong to you={}", followUpId, nurseId);
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }

        PatientFollowUpType patientFollowUpType = PatientFollowUpType.parseString(followUpType);
        long followUpRecordId = patientFollowUpRecordService.addPatientFollowUpRecord(
                followUpId,
                patientFollowUpType,
                consultationId,
                questionnaireId,
                YesNoEnum.YES);
        if (PatientFollowUpType.QUESTIONNAIRE.equals(patientFollowUpType)) {
            notifierForAllModule.followUpAlertToGo2nurseUser(
                    patientFollowUpType,
                    followUp.getUserId(),
                    followUpRecordId,
                    questionnaireId,
                    ReadingStatus.UNREAD.name(),
                    "nurse make questionnaire follow-up!"
            );
        }
        else if (PatientFollowUpType.CONSULTATION.equals(patientFollowUpType)) {
            UserConsultationBean consultation = userConsultationService.getUserConsultationNoProperties(consultationId);
            notifierForAllModule.followUpAlertToGo2nurseUser(
                    PatientFollowUpType.CONSULTATION,
                    followUp.getUserId(),
                    followUpRecordId,
                    consultationId,
                    ConsultationCreator.NURSE.name(),
                    consultation.getDiseaseDescription()
            );
        }

        Map<String, Long> map = new HashMap<>();
        map.put("id", followUpRecordId);
        return Response.ok(map).build();
    }
}
