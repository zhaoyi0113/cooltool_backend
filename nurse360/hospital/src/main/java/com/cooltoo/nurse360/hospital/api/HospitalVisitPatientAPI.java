package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseVisitPatientService;
import com.cooltoo.go2nurse.service.NurseVisitPatientServiceItemService;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalVisitPatientAPI {

    @Autowired private NurseVisitPatientService visitPatientService;
    @Autowired private NurseVisitPatientServiceItemService visitPatientServiceItemService;
    @Autowired private JSONUtil jsonUtil;


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
    //=========================================
    //       Getting Patient Visit Record
    //=========================================
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

    @RequestMapping(path = "/visit/patient/userid/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countVisitPatientByUser(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0",name = "user_id")     long userId,
                                        @RequestParam(defaultValue = "", name = "patient_id")String strPatientId
    ) {
//        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
//        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
//        Long hospitalId   = tmp[0];
//        Long departmentId = tmp[1];
        long count = 0;
        Long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : null;
        count = visitPatientService.countVisitRecordByCondition(userId, patientId, null, null, null, null, null);
        return count;
}

    @RequestMapping(path = "/visit/patient/userid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseVisitPatientBean> getVisitPatientByUser(HttpServletRequest request,
                                                             @RequestParam(defaultValue = "0", name = "user_id")     long userId,
                                                             @RequestParam(defaultValue = "",  name = "patient_id")String strPatientId,
                                                             @RequestParam(defaultValue = "0", name = "index")  int pageIndex,
                                                             @RequestParam(defaultValue = "10",name = "number") int sizePerPage
    ) {
//        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
//        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
//        Long hospitalId   = tmp[0];
//        Long departmentId = tmp[1];
        Long patientId = VerifyUtil.isIds(strPatientId) ? VerifyUtil.parseLongIds(strPatientId).get(0) : null;
        List<NurseVisitPatientBean> count;
        count = visitPatientService.getVisitRecordByCondition(userId, patientId, null, null, null, null, null, pageIndex, sizePerPage);
        return count;
    }

    @RequestMapping(path = "/visit/patient/{visit_patient_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public NurseVisitPatientBean getVisitPatient(HttpServletRequest request,
                                                 @PathVariable long visit_patient_id
    ) {
        NurseVisitPatientBean visitPatient = visitPatientService.getVisitRecord(visit_patient_id);
        return visitPatient;
    }

    //=========================================
    //       Creating Patient Visit Record
    //=========================================
    @RequestMapping(path = "/visit/patient", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, Long> createVisitPatientRecord(HttpServletRequest request,
                                                      @RequestParam(defaultValue = "0", name = "user_id") long userId,
                                                      @RequestParam(defaultValue = "0", name = "patient_id") long patientId,
                                                      @RequestParam(defaultValue = "",  name = "service_item_ids") String serviceItemId,
                                                      @RequestParam(defaultValue = "",  name = "visit_record") String visitRecord,
                                                      @RequestParam(defaultValue = "0", name = "order_id") long orderId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        List<NurseVisitPatientServiceItemBean> serviceItems = visitPatientServiceItemService.getVisitPatientServiceItem(serviceItemId);
        long visitId = visitPatientService.addVisitRecord(nurseId, userId, patientId, orderId, visitRecord, serviceItems);

        Map<String, Long> map = new HashMap<>();
        map.put("id", visitId);
        return map;
    }

    @RequestMapping(path = "/visit/patient/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, String> addVisitPatientRecordImage(HttpServletRequest request,
                                                          @RequestPart(required = true,  name = "visit_record_id") long visitRecordId,
                                                          @RequestPart(required = false, name = "image_name") String imageName,
                                                          @RequestPart(required = true,  name = "image") MultipartFile image
    ) {
        InputStream imageInput = null;
        try { imageInput = image.getInputStream(); } catch (Exception ex) {}
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        Map<String, String> visitImageIdUrl = visitPatientService.addVisitRecordImage(nurseId, visitRecordId, imageName, imageInput);
        return visitImageIdUrl;
    }

    @RequestMapping(path = "/visit/patient/sign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, String> setPatientSignImage(HttpServletRequest request,
                                                   @RequestPart(required = true,  name = "visit_record_id") long visitRecordId,
                                                   @RequestPart(required = false, name = "image_name") String imageName,
                                                   @RequestPart(required = true,  name = "image") MultipartFile image
    ) {
        InputStream imageInput = null;
        try { imageInput = image.getInputStream(); } catch (Exception ex) {}
        Map<String, String> visitImageIdUrl = visitPatientService.addPatientSignImage(visitRecordId, imageName, imageInput);
        return visitImageIdUrl;
    }

    //=========================================
    //       Updating Patient Visit Record
    //=========================================
    @RequestMapping(path = "/visit/patient", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public NurseVisitPatientBean updateVisitPatientRecord(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "0", name = "visit_record_id") long visitRecordId,
                                                          @RequestParam(defaultValue = "",  name = "service_item_ids") String serviceItemId,
                                                          @RequestParam(defaultValue = "",  name = "visit_record") String visitRecord
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        List<NurseVisitPatientServiceItemBean> serviceItems = visitPatientServiceItemService.getVisitPatientServiceItem(serviceItemId);
        String serviceItemsJson = "";
        if (!VerifyUtil.isListEmpty(serviceItems)) {
            serviceItemsJson = jsonUtil.toJsonString(serviceItems);
        }
        NurseVisitPatientBean visit = visitPatientService.updateVisitRecord(nurseId, visitRecordId, visitRecord, serviceItemsJson);
        return visit;
    }

    @RequestMapping(path = "/visit/patient/image", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public Map<String, Boolean> deleteVisitPatientRecordImage(HttpServletRequest request,
                                                              @RequestParam(defaultValue = "0", name = "visit_record_id") long visitRecordId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        visitPatientService.deleteVisitRecordImage(nurseId, visitRecordId);
        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("deleted", Boolean.TRUE);
        return retVal;
    }
}
