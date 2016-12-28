package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.NurseAuthorizationJudgeService;
import com.cooltoo.go2nurse.service.NursePatientRelationService;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import com.cooltoo.util.SetUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/8/12.
 */
@Path("/user/nurse")
public class NurseAPIForPatient {

    @Autowired private NurseServiceForGo2Nurse nurseServiceForGo2Nurse;
    @Autowired private NurseAuthorizationJudgeService nurseAuthorizationJudgeService;
    @Autowired private NursePatientRelationService nursePatientRelationService;

    @Path("/{nurse_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getNurseById(@Context HttpServletRequest request,
                                 @PathParam("nurse_id") @DefaultValue("0") long nurseId
    ) {
        NurseBean nurse = nurseServiceForGo2Nurse.getNurseById(nurseId);
        return Response.ok(nurse).build();
    }

    // can_answer_nursing_question ===> yes/no/none
    @Path("/by_condition")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getNurseById(@Context HttpServletRequest request,
                                 @QueryParam("name") @DefaultValue("") String name,
                                 @QueryParam("can_answer_nursing_question") @DefaultValue("") String canAnswerNursingQuestion,
                                 @QueryParam("can_see_all_order") @DefaultValue("") String canSeeAllOrder,
                                 @QueryParam("hospital_id") @DefaultValue("") String strHospitalId,
                                 @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                 @QueryParam("register_from") @DefaultValue("") String strRegisterFrom,
                                 @QueryParam("index")  @DefaultValue("0")  int index,
                                 @QueryParam("number") @DefaultValue("10") int number

    ) {
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        RegisterFrom registerFrom = RegisterFrom.parseString(strRegisterFrom);
        List<NurseBean> nurses = nurseServiceForGo2Nurse.getNurseByCanAnswerQuestion(name, canAnswerNursingQuestion, canSeeAllOrder, hospitalId, departmentId, registerFrom, index, number);
        return Response.ok(nurses).build();
    }


    // can_answer_nursing_question ===> yes/no/none
    @Path("/query_string")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getNurseById(@Context HttpServletRequest request,
                                 @QueryParam("can_answer_nursing_question") @DefaultValue("YES") String canAnswerNursingQuestion,
                                 @QueryParam("query") @DefaultValue("") String query,
                                 @QueryParam("index")  @DefaultValue("0")  int index,
                                 @QueryParam("number") @DefaultValue("10") int number

    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);

        List<Long> nursesId = nurseServiceForGo2Nurse.getNurseIdByQueryString(
                YesNoEnum.parseString(canAnswerNursingQuestion),
                query,
                YesNoEnum.YES);
        List<Long> patientsNurseIds = nursePatientRelationService.getNurseIdByPatientId(userId, null, CommonStatus.ENABLED);
        nursesId = SetUtil.newInstance().mergeListValue(nursesId, patientsNurseIds);
        List<NurseBean> nurses = nurseServiceForGo2Nurse.getNurseByIds(nursesId, index, number);
        return Response.ok(nurses).build();
    }


    // can_answer_nursing_question ===> yes/no/none
    @Path("/can/answer/question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response canNurseAnswerConsultation(@Context HttpServletRequest request,
                                               @QueryParam("nures_id") @DefaultValue("0") long nurseId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        boolean canNurseAnswerConsultation = nurseAuthorizationJudgeService.canNurseAnswerConsultation(nurseId, userId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("result", canNurseAnswerConsultation);
        return Response.ok(result).build();
    }
}
