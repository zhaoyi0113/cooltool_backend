package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.WhoDenyPatient;
import com.cooltoo.go2nurse.service.*;
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
import java.util.*;

/**
 * Created by zhaolisong on 2016/11/17.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalPatientAPI {

    @Autowired private ViewVendorPatientRelationService vendorPatientRelationService;
    @Autowired private UserService userService;
    @Autowired private UserPatientRelationService userPatientRelation;
    @Autowired private PatientService patientService;
    @Autowired private NursePatientRelationServiceForNurse360 nursePatientRelationService;
    @Autowired private DenyPatientService denyPatientService;

    //=============================================================
    //            Permit ALL Role
    //=============================================================
    @RequestMapping(path = "/patient/information", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Map<String, Object> getPatient(@RequestParam(defaultValue = "0", name = "user_id")    long userId,
                                          @RequestParam(defaultValue = "0", name = "patient_id") long patientId
    ) {
        Map<String, Object> returnValue = new HashMap<>();
        UserBean user = userService.getUser(userId);
        returnValue.put("user", user);

        PatientBean patient = patientService.getOneById(patientId);
        List<Long> relativeUserIds = userPatientRelation.getUserIdByPatient(Arrays.asList(new Long[]{patientId}), CommonStatus.ENABLED.name());
        if (!VerifyUtil.isListEmpty(relativeUserIds) && relativeUserIds.contains(userId)) {
            returnValue.put("patient", patient);
        }

        return returnValue;
    }


    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================
    @RequestMapping(path = "/admin/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countPatient(@RequestParam(defaultValue = "0", name = "vendor_type") String strVendorType,
                             @RequestParam(defaultValue = "0", name = "vendor_id")   String strVendorId,
                             @RequestParam(defaultValue = "0", name = "depart_id")   String strDepartId,
                             @RequestParam(defaultValue = "",  name = "patient_name")String userOrPatientName
    ) {
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Long vendorId = VerifyUtil.isIds(strVendorId) ? VerifyUtil.parseLongIds(strVendorId).get(0) : 0L;
        Long departId = VerifyUtil.isIds(strDepartId) ? VerifyUtil.parseLongIds(strDepartId).get(0) : 0L;
        long count = vendorPatientRelationService.countVendorsPatientByCondition(vendorType, vendorId, departId, userOrPatientName);
        return count;
    }

    @RequestMapping(path = "/admin/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ViewVendorPatientRelationBean> getPatient(@RequestParam(defaultValue = "0", name = "vendor_type") String strVendorType,
                                                          @RequestParam(defaultValue = "0", name = "vendor_id")   String strVendorId,
                                                          @RequestParam(defaultValue = "0", name = "depart_id")   String strDepartId,
                                                          @RequestParam(defaultValue = "",  name = "patient_name")String userOrPatientName,
                                                          @RequestParam(defaultValue = "0",  name = "index")  int index,
                                                          @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Long vendorId = VerifyUtil.isIds(strVendorId) ? VerifyUtil.parseLongIds(strVendorId).get(0) : 0L;
        Long departId = VerifyUtil.isIds(strDepartId) ? VerifyUtil.parseLongIds(strDepartId).get(0) : 0L;
        List<ViewVendorPatientRelationBean> vendorsPatient = vendorPatientRelationService.getVendorsPatientByCondition(
                vendorType, vendorId, departId, userOrPatientName, index, number
        );
        return vendorsPatient;
    }



    //===============================================================================
    //                    Authentication of NURSE/MANAGER Role
    //===============================================================================
    //============================================
    //         Nurse Patient Relation
    //============================================
    @RequestMapping(path = "/nurse/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countPatient(HttpServletRequest request,
                             @RequestParam(defaultValue = "0", name = "nurse_id") long nurseId
    ) {
        Map<Long, Long> nursePatientCount = nursePatientRelationService.getNursePatientNumber(Arrays.asList(new Long[]{nurseId}), CommonStatus.ENABLED);
        Long count = null==nursePatientCount ? 0L : nursePatientCount.get(nurseId);
        count = null==count ? 0L : count;
        return count;
    }

    @RequestMapping(path = "/nurse/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NursePatientRelationBean> getPatient(HttpServletRequest request,
                                                     @RequestParam(defaultValue = "0", name = "nurse_id") long nurseId,
                                                     @RequestParam(defaultValue = "0",  name = "index")  int index,
                                                     @RequestParam(defaultValue = "10", name = "number") int number
    ) {

        List<NursePatientRelationBean> vendorsPatient = nursePatientRelationService.getRelationByNurseId(nurseId, CommonStatus.ENABLED, index, number);
        return vendorsPatient;
    }


    //============================================
    //         Vendor Patient Relation
    //============================================
    @RequestMapping(path = "/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countPatient(HttpServletRequest request,
                             @RequestParam(defaultValue = "", name = "patient_name")String userOrPatientName
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("0", "0", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        long count = vendorPatientRelationService.countHospitalPatientByCondition(hospitalId, departmentId, userOrPatientName);
        return count;
    }

    @RequestMapping(path = "/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ViewVendorPatientRelationBean> getPatient(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "", name = "patient_name")String userOrPatientName,
                                                          @RequestParam(defaultValue = "0",  name = "index")  int index,
                                                          @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("0", "0", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        Long nurseId = userDetails.isAdmin() ? null : userDetails.getId();
        List<ViewVendorPatientRelationBean> vendorsPatient = vendorPatientRelationService.getHospitalPatientByCondition(hospitalId, departmentId, userOrPatientName, index, number);
        vendorPatientRelationService.setForbiddenFlag(vendorsPatient, nurseId, ServiceVendorType.HOSPITAL, hospitalId, departmentId);
        return vendorsPatient;
    }

    @RequestMapping(path = "/patient/forbid", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public void forbidPatient(HttpServletRequest request,
                              @RequestParam(defaultValue = "0",     name = "user_id")      long userId,
                              @RequestParam(defaultValue = "false", name = "forbidden") boolean forbidden
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (userDetails.isNurseManager()) {
            Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("0", "0", userDetails);
            Long hospitalId = tmp[0];
            Long departmentId = tmp[1];
            if (forbidden) {
                denyPatientService.denyPatient(WhoDenyPatient.VENDOR, null, ServiceVendorType.HOSPITAL, hospitalId, departmentId, userId, null);
            }
            else {
                denyPatientService.enablePatient(WhoDenyPatient.VENDOR, null, ServiceVendorType.HOSPITAL, hospitalId, departmentId, userId, null);
            }
        }
        else if (userDetails.isNurse()) {
            long nurseId = userDetails.getId();
            if (forbidden) {
                denyPatientService.denyPatient(WhoDenyPatient.NURSE, nurseId, null, null, null, userId, null);
            }
            else {
                denyPatientService.enablePatient(WhoDenyPatient.NURSE, nurseId, null, null, null, userId, null);
            }

        }
        return;
    }
}
