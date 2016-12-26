package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.NurseBean;
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
import java.util.List;

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
                                 @QueryParam("can_answer_nursing_question") @DefaultValue("") String canAnswerNursingQuestion,
                                 @QueryParam("query") @DefaultValue("") String query,
                                 @QueryParam("index")  @DefaultValue("0")  int index,
                                 @QueryParam("number") @DefaultValue("10") int number

    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<NurseBean> nurses = nurseServiceForGo2Nurse.getNurseByQueryString(canAnswerNursingQuestion, query, null);
        nurses = nurseAuthorizationJudgeService.canNurseAnswerConsultation(nurses, userId);
        List<Long> nurseIds = nursePatientRelationService.getNurseIdByPatientId(userId, null, CommonStatus.ENABLED);
        for (int i=0; i<nurses.size(); i++) {
            NurseBean tmp = nurses.get(i);
            if (!nurseIds.contains(tmp.getId())) {
                nurses.remove(i);
                i--;
            }
        }
        nurses = SetUtil.newInstance().getSetByPage(nurses, index, number, null);
        return Response.ok(nurses).build();
    }

}
