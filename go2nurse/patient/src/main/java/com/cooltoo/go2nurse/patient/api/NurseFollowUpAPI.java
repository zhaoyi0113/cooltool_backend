package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpRecordBean;
import com.cooltoo.go2nurse.beans.NursePushCourseBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.NursePatientFollowUpRecordService;
import com.cooltoo.go2nurse.service.NursePatientFollowUpService;
import com.cooltoo.go2nurse.service.NursePushCourseService;
import com.cooltoo.go2nurse.service.UserCourseRelationService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/21.
 */
@Path("/user/nurse")
public class NurseFollowUpAPI {

    @Autowired private NursePatientFollowUpService nursePatientFollowUpService;
    @Autowired private NursePatientFollowUpRecordService nursePatientFollowUpRecordService;
    @Autowired private NursePushCourseService nursePushCourseService;
    @Autowired private UserCourseRelationService userCourseRelationService;

    @Path("/course/pushed")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCoursesPushed(@Context HttpServletRequest request,
                                     @QueryParam("nurse_id") @DefaultValue("") String strNurseId,
                                     @QueryParam("index")    @DefaultValue("0") int pageIndex,
                                     @QueryParam("number")   @DefaultValue("0") int sizePerPage
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Long nurseId = VerifyUtil.isIds(strNurseId) ? VerifyUtil.parseLongIds(strNurseId).get(0) : null;
        List<NursePushCourseBean> coursesPushed = nursePushCourseService.getCoursePushed(nurseId, userId, null, pageIndex, sizePerPage, true);
        for (int i=0; i<coursesPushed.size(); i++) {
            NursePushCourseBean tmp = coursesPushed.get(i);
            if (null!=tmp.getCourse()) {
                continue;
            }
            coursesPushed.remove(i);
            i--;
        }
        List<CourseBean> allCourse = new ArrayList<>();
        for (int i=0; i<coursesPushed.size(); i++) {
            NursePushCourseBean tmp = coursesPushed.get(i);
            allCourse.add(tmp.getCourse());
        }
        userCourseRelationService.setCourseReadStatus(userId, allCourse);
        return Response.ok(coursesPushed).build();
    }

    @Path("/follow-up")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getNurseFollowUp(@Context HttpServletRequest request,
                                     @QueryParam("nurse_id") @DefaultValue("0") long nurseId,
                                     @QueryParam("follow_up_type") @DefaultValue("") String followUpType, /* Consultation(提问), Questionnaire(发问卷) */
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        PatientFollowUpType patientFollowUpType = PatientFollowUpType.parseString(followUpType);
        List<NursePatientFollowUpRecordBean> visits = getPatientFollowUpRecord(userId, nurseId, false, patientFollowUpType, pageIndex, sizePerPage);
        return Response.ok(visits).build();
    }

    @Path("/follow-up/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getNurseFollowUp(@Context HttpServletRequest request,
                                     @QueryParam("index") @DefaultValue("0") int pageIndex,
                                     @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<NursePatientFollowUpRecordBean> visits = getPatientFollowUpRecord(userId, 0, true, null, pageIndex, sizePerPage);
        return Response.ok(visits).build();
    }

    //==============================================================
    //            Common Method
    //==============================================================
    private List<NursePatientFollowUpRecordBean> getPatientFollowUpRecord(long userId, long nurseId, boolean allFollowUp, PatientFollowUpType followUpType, int pageIndex, int sizePerPage) {
        List<NursePatientFollowUpBean> followUpBeans;
        if (allFollowUp) {
            followUpBeans = nursePatientFollowUpService.getPatientFollowUp(userId, null, null);
        }
        else {
            followUpBeans = nursePatientFollowUpService.getPatientFollowUp(userId, null, nurseId);
        }
        List<Long> followUpIds = new ArrayList<>();
        for (NursePatientFollowUpBean tmp : followUpBeans) {
            followUpIds.add(tmp.getUserId());
        }
        if (VerifyUtil.isListEmpty(followUpIds)) {
            return new ArrayList<>();
        }
        else {
            List<NursePatientFollowUpRecordBean> visits = nursePatientFollowUpRecordService.getPatientFollowUpRecordByFollowUpIds(
                    CommonStatus.DELETED,
                    followUpType,
                    null,
                    null,
                    followUpIds,
                    ConsultationTalkStatus.USER_SPEAK,
                    NursePatientFollowUpRecordService.ORDER_BY_PATIENT_REPLIED,
                    pageIndex, sizePerPage, false);
            return visits;
        }
    }
}
