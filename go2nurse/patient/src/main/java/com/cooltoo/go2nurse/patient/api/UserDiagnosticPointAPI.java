package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserDiagnosticPointRelationService;
import com.cooltoo.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/diagnostic_point_relation")
public class UserDiagnosticPointAPI {

    @Autowired private UserDiagnosticPointRelationService relationService;

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getRelation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserDiagnosticPointRelationBean> relations = relationService.getRelation(userId, CommonStatus.ENABLED.name());
        return Response.ok(relations).build();
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
                                @FormParam("discharged_from_hospital_date") @DefaultValue("") String dischargedDate
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);

        List<DiagnosticEnumeration> diagnosticPoints = new ArrayList<>();
        List<Date> pointTimes = new ArrayList<>();
        parseTime(hospitalizedDate, examinationDate, operationDate, rehabilitationDate, dischargedDate, diagnosticPoints, pointTimes);

        List<UserDiagnosticPointRelationBean> relations = relationService.addUserDiagnosticRelation(userId, diagnosticPoints, pointTimes);
        return Response.ok(relations).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateRelation(@Context HttpServletRequest request,
                                   @FormParam("relation_id") @DefaultValue("0") long relationId,
                                   @FormParam("point_time") @DefaultValue("") String pointTime,
                                   @FormParam("status") @DefaultValue("disabled") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long time = NumberUtil.getTime(pointTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date newTime = time<0 ? null : new Date(time);
        UserDiagnosticPointRelationBean relation = relationService.updateUserDiagnosticRelation(relationId, true, userId, newTime, status);
        return Response.ok(relation).build();
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
            diagnosticPoints.add(DiagnosticEnumeration.HOSPITALIZED_DATE);
            pointTimes.add(new Date(timeOperation));
        }
        long timeRehabilitation = NumberUtil.getTime(rehabilitationDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (timeRehabilitation>0) {
            diagnosticPoints.add(DiagnosticEnumeration.HOSPITALIZED_DATE);
            pointTimes.add(new Date(timeRehabilitation));
        }
        long timeDischarged = NumberUtil.getTime(dischargedDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (timeDischarged>0) {
            diagnosticPoints.add(DiagnosticEnumeration.HOSPITALIZED_DATE);
            pointTimes.add(new Date(timeDischarged));
        }
    }
}
