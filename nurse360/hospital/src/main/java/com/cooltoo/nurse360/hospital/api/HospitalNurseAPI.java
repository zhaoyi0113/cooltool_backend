package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.go2nurse.service.NurseDoctorScoreService;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.nurse360.service.NursePatientRelationServiceForNurse360;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/22.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalNurseAPI {

    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private NursePatientRelationServiceForNurse360 nursePatientRelation;
    @Autowired private NurseOrderRelationService nurseOrderRelationService;
    @Autowired private NurseDoctorScoreService nurseDoctorScoreService;

    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================


    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/nurse/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countNurse(HttpServletRequest request,
                           @RequestParam(defaultValue = "",  name = "fuzzy_name") String fuzzyName,
                           @RequestParam(defaultValue = "",  name = "hospital_id") String strHospitalId,
                           @RequestParam(defaultValue = "",  name = "department_id") String strDepartmentId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment(strHospitalId, strDepartmentId, userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];

        long count = nurseService.countNurseByCanAnswerQuestion(fuzzyName, null, null, hospitalId, departmentId, null);
        return count;
    }

    @RequestMapping(path = "/nurse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseBean> getNurse(HttpServletRequest request,
                                    @RequestParam(defaultValue = "",  name = "fuzzy_name") String fuzzyName,
                                    @RequestParam(defaultValue = "",  name = "hospital_id") String strHospitalId,
                                    @RequestParam(defaultValue = "",  name = "department_id") String strDepartmentId,
                                    @RequestParam(defaultValue = "0",  name = "index") int index,
                                    @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment(strHospitalId, strDepartmentId, userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];

        List<NurseBean> nurses = nurseService.getNurseByCanAnswerQuestion(fuzzyName, null, null, hospitalId, departmentId, null, index, number);
        fillNurseOtherProperties(nurses);
        return nurses;
    }

    private void fillNurseOtherProperties(List<NurseBean> nurses) {
        List<Long> nurseIds = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(nurses)) {
            for (NurseBean tmp : nurses) {
                if (!nurseIds.contains(tmp.getId())) {
                    nurseIds.add(tmp.getId());
                }
            }
        }
        if (VerifyUtil.isListEmpty(nurseIds)) {
            return;
        }

        Map<Long, Long> nursePatientNumber = nursePatientRelation.getNursePatientNumber(nurseIds, CommonStatus.ENABLED);
        Map<Long, Long> nurseOrderCompleted= nurseOrderRelationService.getNurseCompletedOrderNumber(nurseIds, CommonStatus.ENABLED);
        Map<Long, Float> nurseScore = nurseDoctorScoreService.getScoreByReceiverTypeAndIds(UserType.NURSE, nurseIds);
        for (NurseBean tmp : nurses) {
            Long patientNumber = nursePatientNumber.get(tmp.getId());
            Long orderNumber = nurseOrderCompleted.get(tmp.getId());
            Float score = nurseScore.get(tmp.getId());
            tmp.setProperty(NurseBean.SCORE, null==score ? 0.0 : score);
            tmp.setProperty(NurseBean.COMPLETED_ORDER_COUNT, null==orderNumber ? 0 : orderNumber);
            tmp.setProperty(NurseBean.PATIENT_COUNT, null==patientNumber ? 0 : patientNumber);
        }
    }
}
