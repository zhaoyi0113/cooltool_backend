package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.ServiceVendorAuthorizationBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.ViewVendorPatientRelationBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.*;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
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
    @Autowired private ServiceVendorAuthorizationService vendorAuthorizationService;
    @Autowired private UserService userService;
    @Autowired private UserPatientRelationService userPatientRelation;
    @Autowired private PatientService patientService;

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
    public long countPatient(@RequestParam(defaultValue = "0", name = "vendor_type")   String strVendorType,
                             @RequestParam(defaultValue = "0", name = "vendor_id")   String strVendorId,
                             @RequestParam(defaultValue = "0", name = "depart_id") String strDepartId
    ) {
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Long vendorId = VerifyUtil.isIds(strVendorId) ? VerifyUtil.parseLongIds(strVendorId).get(0) : 0L;
        Long departId = VerifyUtil.isIds(strDepartId) ? VerifyUtil.parseLongIds(strDepartId).get(0) : 0L;
        long count = vendorPatientRelationService.countVendorsPatientByCondition(vendorType, vendorId, departId);
        return count;
    }

    @RequestMapping(path = "/admin/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ViewVendorPatientRelationBean> getPatient(@RequestParam(defaultValue = "0", name = "vendor_type")   String strVendorType,
                                                          @RequestParam(defaultValue = "0", name = "vendor_id")   String strVendorId,
                                                          @RequestParam(defaultValue = "0", name = "depart_id") String strDepartId,
                                                          @RequestParam(defaultValue = "0",  name = "index")  int index,
                                                          @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Long vendorId = VerifyUtil.isIds(strVendorId) ? VerifyUtil.parseLongIds(strVendorId).get(0) : 0L;
        Long departId = VerifyUtil.isIds(strDepartId) ? VerifyUtil.parseLongIds(strDepartId).get(0) : 0L;
        List<ViewVendorPatientRelationBean> vendorsPatient = vendorPatientRelationService.getVendorsPatientByCondition(
                vendorType, vendorId, departId, index, number
        );
        return vendorsPatient;
    }



    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countPatient(HttpServletRequest request) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("0", "0", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        long count = vendorPatientRelationService.countHospitalPatientByCondition(hospitalId, departmentId);
        return count;
    }

    @RequestMapping(path = "/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ViewVendorPatientRelationBean> getPatient(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "0",  name = "index")  int index,
                                                          @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("0", "0", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        List<ViewVendorPatientRelationBean> vendorsPatient = vendorPatientRelationService.getHospitalPatientByCondition(hospitalId, departmentId, index, number);
        return vendorsPatient;
    }

    @RequestMapping(path = "/patient/forbid", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public void forbidPatient(HttpServletRequest request,
                              @RequestParam(defaultValue = "0",     name = "user_id")      long userId,
                              @RequestParam(defaultValue = "false", name = "forbidden") boolean forbidden
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("0", "0", userDetails);
        Long hospitalId   = tmp[0];
        Long departmentId = tmp[1];
        CommonStatus forbiddenStatus = forbidden ? CommonStatus.ENABLED : CommonStatus.DISABLED;
        vendorAuthorizationService.forbidUser(userId, ServiceVendorType.HOSPITAL, hospitalId, departmentId, forbiddenStatus);
        return;
    }
}
