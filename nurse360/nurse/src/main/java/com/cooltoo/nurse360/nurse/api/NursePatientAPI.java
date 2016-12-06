package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.UserHospitalizedStatus;
import com.cooltoo.go2nurse.service.NursePatientFollowUpService;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.go2nurse.service.UserPatientRelationService;
import com.cooltoo.go2nurse.service.UserService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NursePatientRelationServiceForNurse360;
import com.cooltoo.util.SetUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Path("/nurse/patient")
public class NursePatientAPI {

    @Autowired private NursePatientRelationServiceForNurse360 nursePatientService;
    @Autowired private UserService userService;
    @Autowired private UserPatientRelationService userPatientRelationService;
    @Autowired private PatientService patientService;
    @Autowired private NursePatientFollowUpService nursePatientFollowUpService;
    private static final SetUtil setUtil = SetUtil.newInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getPatient(@Context HttpServletRequest request,
                               @QueryParam("is_in_hospital") @DefaultValue("0") String isInHospital /* YES, NO */,
                               @QueryParam("index") @DefaultValue("0") int index,
                               @QueryParam("number") @DefaultValue("10") int number
    ) {
        YesNoEnum inHospital = YesNoEnum.parseString(isInHospital);
        UserHospitalizedStatus userInHospital = YesNoEnum.YES.equals(inHospital)
                ? UserHospitalizedStatus.IN_HOSPITAL
                : UserHospitalizedStatus.IN_HOME;
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);

        List<NursePatientRelationBean> nursePatientRelations = nursePatientService.getRelationByNurseId(nurseId, CommonStatus.ENABLED);
        List<NursePatientFollowUpBean> nursePatientFollowUps = nursePatientFollowUpService.getPatientFollowUp(null, null, nurseId);

        Map<Long, Long> userIdToFollowUpId = new HashMap<>();
        for (NursePatientFollowUpBean tmpF : nursePatientFollowUps) {
            for (NursePatientRelationBean tmpR : nursePatientRelations) {
                if (tmpF.getUserId()==tmpR.getUserId() && tmpF.getPatientId()==tmpR.getPatientId()) {
                    userIdToFollowUpId.put(tmpR.getId(), tmpF.getId());
                }
            }
        }

        List<NursePatientRelationBean> returnVal = new ArrayList<>();
        for (int i=0; i<nursePatientRelations.size(); i++) {
            NursePatientRelationBean tmp  = nursePatientRelations.get(i);

            UserBean user = tmp.getUser();
            if (null==user) { continue; }

            Long followUpId = userIdToFollowUpId.get(tmp.getId());
            user.setProperties(UserBean.FOLLOW_UP_ID, null==followUpId ? 0 : followUpId);
            UserHospitalizedStatus status = UserHospitalizedStatus.IN_HOSPITAL.equals(user.getHasDecide())
                    ? UserHospitalizedStatus.IN_HOSPITAL
                    : UserHospitalizedStatus.IN_HOME;
            if (userInHospital.equals(status)) {
                returnVal.add(tmp);
            }
        }
        returnVal = setUtil.getSetByPage(returnVal, index, number, null);
        return Response.ok(returnVal).build();
    }

    @Path("/code")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getPatientUnderGuardianship(@Context HttpServletRequest request,
                                                @QueryParam("user_code") @DefaultValue("0") String userUniqueId
    ) {
        List<UserBean> users = userService.getUserByUniqueId(userUniqueId);
        if (VerifyUtil.isListEmpty(users)) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        if (users.size()>1) {
            throw new BadRequestException(ErrorCode.NURSE360_RESULT_NOT_EXPECTED);
        }
        List<UserPatientRelationBean> relations = userPatientRelationService.getRelationByUserIdAndStatus(users.get(0).getId(), CommonStatus.ENABLED.name());

        List<Long> patientIds = new ArrayList<>();
        for (UserPatientRelationBean tmp : relations) {
            if (null==tmp) { continue; }
            patientIds.add(tmp.getPatientId());
        }
        List<PatientBean> patients = patientService.getAllByStatusAndIds(patientIds, CommonStatus.ENABLED);

        List<UserPatientRelationBean> returnVal = new ArrayList<>();
        for (PatientBean tmpP : patients) {
            if (null==tmpP) { continue; }
            for (UserPatientRelationBean tmpR : relations) {
                if (null==tmpR) { continue; }
                if (tmpR.getPatientId()==tmpP.getId()) {
                    returnVal.add(tmpR);
                    break;
                }
            }
        }
        return Response.ok(returnVal).build();

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addPatient(@Context HttpServletRequest request,
                               @FormParam("user_id") @DefaultValue("0") long userId,
                               @FormParam("patient_id") @DefaultValue("0") String strPatientIds
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Long> patientIds = VerifyUtil.isIds(strPatientIds) ? VerifyUtil.parseLongIds(strPatientIds) : null;
        if (VerifyUtil.isListEmpty(patientIds)) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        for (Long tmp : patientIds) {
            nursePatientService.addUserPatientToNurse(nurseId, tmp, userId);
        }
        return Response.ok().build();
    }

}
