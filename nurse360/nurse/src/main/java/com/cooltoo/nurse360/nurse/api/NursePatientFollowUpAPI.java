package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.*;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.service.NursePatientFollowUpService;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.go2nurse.service.UserPatientRelationService;
import com.cooltoo.go2nurse.service.UserService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.BadRequestException;
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
@Path("/nurse/follow-up/patient")
public class NursePatientFollowUpAPI {

    @Autowired private UserService userService;
    @Autowired private UserPatientRelationService userPatientRelation;
    @Autowired private NursePatientFollowUpService patientFollowUpService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getPatientFollowUp(@Context HttpServletRequest request,
                                       @QueryParam("user_id") @DefaultValue("-1") long userId,
                                       @QueryParam("patient_id") @DefaultValue("-1") long patientId,
                                       @QueryParam("index") @DefaultValue("0") int pageIndex,
                                       @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NursePatientFollowUpBean> visits = patientFollowUpService.getPatientFollowUp(
                0>userId ? null : userId,
                0>patientId ? null : patientId,
                nurseId,
                pageIndex, sizePerPage);
        return Response.ok(visits).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response setPatientFollowUpStatusToDelete(@Context HttpServletRequest request,
                                                     @FormParam("follow_up_id") @DefaultValue("0") long followUpId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Long> updateIds = patientFollowUpService.setDeleteStatusPatientFollowUpByIds(nurseId, Arrays.asList(new Long[]{followUpId}));
        return Response.ok(updateIds).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addPatientFollowUp(@Context HttpServletRequest request,
                                       @FormParam("user_id") @DefaultValue("0") String strUserId,
                                       @FormParam("patient_id") @DefaultValue("0") String strPatientId
    ) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseHospitalRelationBean nurseHospital = nurseHospitalRelation.getRelationByNurseId(nurseId, null);

        int hospitalId = 0;
        int departmentId = 0;
        if (null!=nurseHospital) {
            hospitalId = nurseHospital.getHospitalId();
            departmentId = nurseHospital.getDepartmentId();
        }

        Long userId    = VerifyUtil.isIds(strUserId)    ? VerifyUtil.parseLongIds(strUserId).get(0)    : 0L;
        Long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : 0L;
        long followUpId = patientFollowUpService.addPatientFollowUp(hospitalId, departmentId, nurseId, userId, patientId);

        Map<String, Long> map = new HashMap<>();
        map.put("id", followUpId);
        return Response.ok(map).build();
    }
}
