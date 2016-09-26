package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.beans.UserReExaminationDateBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.constants.ProcessStatus;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserDiagnosticPointRelationService;
import com.cooltoo.go2nurse.service.UserReExaminationDateService;
import com.cooltoo.go2nurse.service.UserService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/diagnostic_point_relation")
public class UserDiagnosticPointAPI {

    @Autowired private UserDiagnosticPointRelationService relationService;
    @Autowired private UserService userService;

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getRelation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserDiagnosticPointRelationBean> relations = relationService.getRelation(userId, CommonStatus.ENABLED.name());
        return Response.ok(relations).build();
    }

    @Path("/get/latest_group_diagnostic_points")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getLatestRelation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserDiagnosticPointRelationBean> relations = relationService.getUserCurrentDiagnosticRelation(userId);
        List<UserDiagnosticPointRelationBean> returnValue = new ArrayList<>();
        for (int i=relations.size()-1; i>=0; i--) {
            UserDiagnosticPointRelationBean relation = relations.get(i);
            if (!YesNoEnum.YES.equals(relation.getHasOperation())
              && DiagnosticEnumeration.OPERATION.equals(relation.getDiagnostic())) {
                continue;
            }
            returnValue.add(relations.get(i));
        }
        return Response.ok(returnValue).build();
    }

    @Path("/did_have_operation")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response disHasOperation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserDiagnosticPointRelationBean> relations = relationService.getUserCurrentDiagnosticRelation(userId);
        Map<String, YesNoEnum> ret = new HashMap<>();
        if (VerifyUtil.isListEmpty(relations)) {
            ret.put("did_have_operation", YesNoEnum.NO);
        }
        else {
            ret.put("did_have_operation", relations.get(0).getHasOperation());
        }
        return Response.ok(ret).build();
    }

    @Path("/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addRelation(@Context HttpServletRequest request,
                                @FormParam("hospitalized_date") @DefaultValue("") String hospitalizedDate,
                                @FormParam("physical_examination_date") @DefaultValue("") String examinationDate,
                                @FormParam("operation_date") @DefaultValue("") String operationDate,
                                @FormParam("rehabilitation_date") @DefaultValue("") String rehabilitationDate,
                                @FormParam("discharged_from_hospital_date") @DefaultValue("") String dischargedDate,
                                @FormParam("has_operation") @DefaultValue("true") Boolean hasOperation
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        userService.updateUser(userId, null, -1, null, -1, null, UserHospitalizedStatus.IN_HOSPITAL.name());

        List<DiagnosticEnumeration> diagnosticPoints = new ArrayList<>();
        List<Date> pointTimes = new ArrayList<>();
        parseTime(hospitalizedDate, examinationDate, operationDate, rehabilitationDate, dischargedDate, diagnosticPoints, pointTimes);

        long groupId = System.currentTimeMillis();
        List<UserDiagnosticPointRelationBean> relations = relationService.addUserDiagnosticRelation(userId, groupId, diagnosticPoints, pointTimes, hasOperation);
        return Response.ok(relations).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateRelation(@Context HttpServletRequest request,
                                   @FormParam("relation_id") @DefaultValue("0") long relationId,
                                   @FormParam("point_time") @DefaultValue("") String pointTime,
                                   @FormParam("status") @DefaultValue("") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long time = NumberUtil.getTime(pointTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date newTime = time<0 ? null : new Date(time);
        UserDiagnosticPointRelationBean relation = relationService.updateUserDiagnosticRelation(relationId, true, userId, newTime, status);
        return Response.ok(relation).build();
    }

    @Path("/edit/current_diagnostic_point_times")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateRelationByGroupIdAndDiagnostics(@Context HttpServletRequest request,
                                                          @FormParam("hospitalized_date") @DefaultValue("") String hospitalizedDate,
                                                          @FormParam("physical_examination_date") @DefaultValue("") String examinationDate,
                                                          @FormParam("operation_date") @DefaultValue("") String operationDate,
                                                          @FormParam("rehabilitation_date") @DefaultValue("") String rehabilitationDate,
                                                          @FormParam("discharged_from_hospital_date") @DefaultValue("") String dischargedDate,
                                                          @FormParam("has_operation") @DefaultValue("") String strHasOperation /* true, false */
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long currentGroupId = relationService.getUserCurrentGroupId(userId);

        List<DiagnosticEnumeration> diagnosticPoints = new ArrayList<>();
        List<Date> pointTimes = new ArrayList<>();
        parseTime(hospitalizedDate, examinationDate, operationDate, rehabilitationDate, dischargedDate, diagnosticPoints, pointTimes);

        Boolean hasOperation = VerifyUtil.isStringEmpty(strHasOperation) ? null : Boolean.valueOf(strHasOperation);
        if (null!=hasOperation) {
            relationService.updateHasOperationFlagByUserAndGroup(userId, currentGroupId, hasOperation);
        }

        List<UserDiagnosticPointRelationBean> relation = relationService.updateUserDiagnosticPointTime(currentGroupId, userId, diagnosticPoints, pointTimes);
        return Response.ok(relation).build();
    }

    @Path("/edit/by_diagnostic_id")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateRelationByDiagnosticId(@Context HttpServletRequest request,
                                                 @FormParam("group_id") @DefaultValue("-1") long groupId,
                                                 @FormParam("diagnostic_id") @DefaultValue("-1") long diagnosticId,
                                                 @FormParam("point_time") @DefaultValue("") String pointTime,
                                                 @FormParam("status") @DefaultValue("") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long time = NumberUtil.getTime(pointTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date newTime = time<0 ? null : new Date(time);
        UserDiagnosticPointRelationBean relation = relationService.updateUserDiagnosticRelation(groupId, diagnosticId, userId, newTime, status);
        return Response.ok(relation).build();
    }

//    @Path("/confirm/discharge_from_hospital")
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @LoginAuthentication(requireUserLogin = true)
//    public Response userDischargeFromHospital(@Context HttpServletRequest request) {
//        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
//        Long groupId = relationService.getUserCurrentGroupId(userId);
//        relationService.updateProcessStatusByUserAndGroup(userId, groupId, ProcessStatus.COMPLETED);
//        List<UserDiagnosticPointRelationBean> relations = relationService.getUserDiagnosticRelationByGroupId(userId, groupId);
//        List<UserReExaminationDateBean> reExamDates = reExaminationService.addReExaminationByDiagnosticDates(userId, relations);
//        return Response.ok(reExamDates).build();
//    }
//
//    @Path("/cancel")
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @LoginAuthentication(requireUserLogin = true)
//    public Response userCancelDiagnostic(@Context HttpServletRequest request) {
//        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
//        long groupId = relationService.getUserCurrentGroupId(userId);
//        List<UserDiagnosticPointRelationBean> relations = relationService.updateProcessStatusByUserAndGroup(userId, groupId, ProcessStatus.CANCELED);
//        return Response.ok(relations).build();
//    }

    @Path("/has_operation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response userCancelDiagnostic(@Context HttpServletRequest request,
                                         @FormParam("has_operation") @DefaultValue("") String strHasOperation /* true, false */
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long groupId = relationService.getUserCurrentGroupId(userId);
        Boolean hasOperation = VerifyUtil.isStringEmpty(strHasOperation) ? null : Boolean.valueOf(strHasOperation);
        List<UserDiagnosticPointRelationBean> relations = relationService.updateHasOperationFlagByUserAndGroup(userId, groupId, hasOperation);
        return Response.ok(relations).build();
    }

    private void parseTime(String hospitalizedDate,
                           String examinationDate,
                           String operationDate,
                           String rehabilitationDate,
                           String dischargedDate,
                           List<DiagnosticEnumeration> diagnosticPoints,
                           List<Date> pointTimes
    ) {
        long timeHospitalized = NumberUtil.getTime(hospitalizedDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (timeHospitalized>0) {
            diagnosticPoints.add(DiagnosticEnumeration.HOSPITALIZED_DATE);
            pointTimes.add(new Date(timeHospitalized));
        }
        long timeExamination = NumberUtil.getTime(examinationDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (timeExamination>0) {
            diagnosticPoints.add(DiagnosticEnumeration.PHYSICAL_EXAMINATION);
            pointTimes.add(new Date(timeExamination));
        }
        long timeOperation = NumberUtil.getTime(operationDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (timeOperation>0) {
            diagnosticPoints.add(DiagnosticEnumeration.OPERATION);
            pointTimes.add(new Date(timeOperation));
        }
//        long timeRehabilitation = NumberUtil.getTime(rehabilitationDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
//        if (timeRehabilitation>0) {
//            diagnosticPoints.add(DiagnosticEnumeration.HOSPITALIZED_DATE);
//            pointTimes.add(new Date(timeRehabilitation));
//        }
        long timeDischarged = NumberUtil.getTime(dischargedDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (timeDischarged>0) {
            diagnosticPoints.add(DiagnosticEnumeration.DISCHARGED_FROM_THE_HOSPITAL);
            pointTimes.add(new Date(timeDischarged));
        }
    }
}
