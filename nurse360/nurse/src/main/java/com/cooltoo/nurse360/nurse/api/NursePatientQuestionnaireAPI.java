package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpRecordBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.service.NursePatientFollowUpRecordService;
import com.cooltoo.go2nurse.service.NursePatientFollowUpService;
import com.cooltoo.go2nurse.service.QuestionnaireService;
import com.cooltoo.go2nurse.service.UserQuestionnaireAnswerService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/8.
 */
@Path("/nurse/follow-up/patient/questionnaire")
public class NursePatientQuestionnaireAPI {

    @Autowired private NursePatientFollowUpService followUpService;
    @Autowired private NursePatientFollowUpRecordService followUpRecordService;

    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelation;
    @Autowired private QuestionnaireService questionnaireService;
    @Autowired private UserQuestionnaireAnswerService userAnswerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getQuestionnaire(@Context HttpServletRequest request,
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseHospitalRelationBean nurseHospital = nurseHospitalRelation.getRelationByNurseId(nurseId, null);
        List<QuestionnaireBean> returnValue = new ArrayList<>();
        if (null != nurseHospital) {
            returnValue = questionnaireService.getQuestionnaireByHospitalId(nurseHospital.getHospitalId(), pageIndex, sizePerPage);
        }
        return Response.ok(returnValue).build();
    }

    @Path("/answered")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getQuestionnaireAnswered(@Context HttpServletRequest request,
                                             @QueryParam("follow_up_record_id") @DefaultValue("0") long followUpRecordId
    ) {
        NursePatientFollowUpRecordBean followUpRecord = followUpRecordService.getPatientFollowUpRecordById(followUpRecordId);
        if (null!=followUpRecord) {
            NursePatientFollowUpBean followUp = followUpService.getPatientFollowUpWithoutInfo(followUpRecord.getFollowUpId());
            if (null!=followUp) {
                QuestionnaireBean questionnaire = userAnswerService.getUserQuestionnaireWithAnswer(
                        followUp.getUserId(),
                        followUpRecord.getRelativeQuestionnaireAnswerGroupId(),
                        YesNoEnum.YES.equals(followUpRecord.getPatientReplied())
                );
                if (null!=questionnaire) {
                    return Response.ok(questionnaire).build();
                }
            }
            return Response.ok().build();
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

}
