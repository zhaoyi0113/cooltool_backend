package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseVisitPatientService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalVisitPatientAPI {

    @Autowired private NurseVisitPatientService visitPatientService;



    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================
    @RequestMapping(path = "/admin/visit/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countVisitPatient(HttpServletRequest request,
                                  @RequestParam(defaultValue = "", name = "vendor_type") String strVendorType,
                                  @RequestParam(defaultValue = "", name = "vendor_id")   String strVendorId,
                                  @RequestParam(defaultValue = "", name = "depart_id")   String strDepartId
    ) {
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Long vendorId = VerifyUtil.isIds(strVendorId) ? VerifyUtil.parseLongIds(strVendorId).get(0) : 0L;
        Long departId = VerifyUtil.isIds(strDepartId) ? VerifyUtil.parseLongIds(strDepartId).get(0) : 0L;
        long count = visitPatientService.countVisitRecordByCondition(null, null, null, null,
                vendorType, vendorId, departId
        );
        return count;
    }

    @RequestMapping(path = "/admin/visit/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseVisitPatientBean> getVisitPatient(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "",  name = "vendor_type") String strVendorType,
                                                       @RequestParam(defaultValue = "",  name = "vendor_id")   String strVendorId,
                                                       @RequestParam(defaultValue = "",  name = "depart_id")   String strDepartId,
                                                       @RequestParam(defaultValue = "0", name = "index")  int pageIndex,
                                                       @RequestParam(defaultValue = "10",name = "number") int sizePerPage
    ) {
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Long vendorId = VerifyUtil.isIds(strVendorId) ? VerifyUtil.parseLongIds(strVendorId).get(0) : 0L;
        Long departId = VerifyUtil.isIds(strDepartId) ? VerifyUtil.parseLongIds(strDepartId).get(0) : 0L;
        List<NurseVisitPatientBean> set = visitPatientService.getVisitRecordByCondition(null, null, null, null,
                vendorType, vendorId, departId,
                pageIndex, sizePerPage
        );
        return set;
    }


    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/visit/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countVisitPatient(HttpServletRequest request) {
        long count = 0;
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (userDetails.isNurseManager()) {
            Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
            Long hospitalId = tmp[0];
            Long departmentId = tmp[1];
            count = visitPatientService.countVisitRecordByCondition(null, null, null, null,
                    ServiceVendorType.HOSPITAL, hospitalId, departmentId
            );
            return count;
        }
        else if (userDetails.isNurse()) {
            count = visitPatientService.countVisitRecordByCondition(
                    null, null, userDetails.getId(), null, null, null, null
            );
            return count;
        }
        return count;
    }

    @RequestMapping(path = "/visit/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseVisitPatientBean> getVisitPatient(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "0", name = "index")  int pageIndex,
                                                       @RequestParam(defaultValue = "10",name = "number") int sizePerPage
    ) {
        List<NurseVisitPatientBean> count = new ArrayList<>();
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (userDetails.isNurseManager()) {
            Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
            Long hospitalId = tmp[0];
            Long departmentId = tmp[1];
            count = visitPatientService.getVisitRecordByCondition(null, null, null, null,
                    ServiceVendorType.HOSPITAL, hospitalId, departmentId, pageIndex, sizePerPage
            );
            return count;
        }
        else if (userDetails.isNurse()) {
            count = visitPatientService.getVisitRecordByCondition(
                    null, null, userDetails.getId(), null, null, null, null, pageIndex, sizePerPage
            );
            return count;
        }
        return count;
    }

}
