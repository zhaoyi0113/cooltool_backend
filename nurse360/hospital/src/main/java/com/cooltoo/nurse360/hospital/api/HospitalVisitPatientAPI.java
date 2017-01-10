package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseVisitPatientBean;
import com.cooltoo.go2nurse.beans.NurseVisitPatientServiceItemBean;
import com.cooltoo.go2nurse.beans.UserAddressBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.service.NurseVisitPatientService;
import com.cooltoo.go2nurse.service.NurseVisitPatientServiceItemService;
import com.cooltoo.go2nurse.service.UserAddressService;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.nurse360.service.file.TemporaryFileStorageServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.nurse360.util.PdfUtil;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalVisitPatientAPI {

    @Autowired private NurseVisitPatientService visitPatientService;
    @Autowired private NurseVisitPatientServiceItemService visitPatientServiceItemService;
    private JSONUtil jsonUtil = JSONUtil.newInstance();
    @Autowired private TemporaryFileStorageServiceForNurse360 temporaryFileStorageService;
    @Autowired private Nurse360Utility nurse360Utility;
    @Autowired private UserAddressService userAddressService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelationService;


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
                vendorType, vendorId, departId, null
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
        List<NurseVisitPatientBean> record = visitPatientService.getVisitRecordByCondition(null, null, null, null,
                vendorType, vendorId, departId, null,
                pageIndex, sizePerPage
        );
        return record;
    }


    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    //=========================================
    //       Getting Patient Visit Record
    //=========================================
    @RequestMapping(path = "/visit/patient/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countVisitPatient(HttpServletRequest request,
                                  @RequestParam(defaultValue = "", name = "content") String contentLike
    ) {
        long count = 0;
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (userDetails.isNurseManager()) {
            Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
            Long hospitalId = tmp[0];
            Long departmentId = tmp[1];
            count = visitPatientService.countVisitRecordByCondition(null, null, null, contentLike,
                    ServiceVendorType.HOSPITAL, hospitalId, departmentId, CommonStatus.DELETED
            );
            return count;
        }
        else if (userDetails.isNurse()) {
            count = visitPatientService.countVisitRecordByCondition(
                    null, null, userDetails.getId(), contentLike, null, null, null, CommonStatus.DELETED
            );
            return count;
        }
        return count;
    }

    @RequestMapping(path = "/visit/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseVisitPatientBean> getVisitPatient(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "",  name = "content")String contentLike,
                                                       @RequestParam(defaultValue = "0", name = "index")     int pageIndex,
                                                       @RequestParam(defaultValue = "10",name = "number")    int sizePerPage
    ) {
        List<NurseVisitPatientBean> records = new ArrayList<>();
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (userDetails.isNurseManager()) {
            Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
            Long hospitalId = tmp[0];
            Long departmentId = tmp[1];
            records = visitPatientService.getVisitRecordByCondition(null, null, null, contentLike,
                    ServiceVendorType.HOSPITAL, hospitalId, departmentId, CommonStatus.DELETED, pageIndex, sizePerPage
            );
            return records;
        }
        else if (userDetails.isNurse()) {
            records = visitPatientService.getVisitRecordByCondition(
                    null, null, userDetails.getId(), contentLike, null, null, null, CommonStatus.DELETED, pageIndex, sizePerPage
            );
            return records;
        }
        return records;
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
        count = visitPatientService.countVisitRecordByCondition(userId, patientId, null, null, null, null, null, CommonStatus.DELETED);
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
        List<NurseVisitPatientBean> records;
        records = visitPatientService.getVisitRecordByCondition(userId, patientId, null, null, null, null, null, CommonStatus.DELETED, pageIndex, sizePerPage);
        return records;
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
                                                      @RequestParam(defaultValue = "",  name = "visit_time") String visitTime,
                                                      @RequestParam(defaultValue = "",  name = "address") String address,
                                                      @RequestParam(defaultValue = "",  name = "patient_record_no") String patientRecordNo,
                                                      @RequestParam(defaultValue = "",  name = "note") String note,
                                                      @RequestParam(defaultValue = "0", name = "order_id") long orderId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        Date dVisitTime = new Date(NumberUtil.getTime(visitTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS));
        List<NurseVisitPatientServiceItemBean> serviceItems = visitPatientServiceItemService.getVisitPatientServiceItem(serviceItemId);
        long visitId = visitPatientService.addVisitRecord(nurseId, userId, patientId, orderId, visitRecord, serviceItems, dVisitTime, address, patientRecordNo, note);

        Map<String, Long> map = new HashMap<>();
        map.put("id", visitId);
        return map;
    }

    @RequestMapping(path = "/visit/patient/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, String> addVisitPatientRecordImage(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "0",name = "visit_record_id") long visitRecordId,
                                                          @RequestParam(defaultValue = "", name = "image_name") String imageName,
                                                          @RequestPart(required = true,    name = "image") MultipartFile image
    ) {
        InputStream imageInput = null;
        try { imageInput = image.getInputStream(); } catch (Exception ex) {}
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        Map<String, String> visitImageIdUrl = visitPatientService.addVisitRecordImage(nurseId, visitRecordId, imageName, imageInput);
        return visitImageIdUrl;
    }

    @RequestMapping(path = "/visit/patient/sign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public Map<String, String> setPatientSignImage(HttpServletRequest request,
                                                   @RequestParam(required = true,  name = "visit_record_id") String visitRecordId,
                                                   @RequestParam(required = false, name = "image_name") String imageName,
                                                   @RequestPart(required = true,  name = "image") MultipartFile image
    ) {
        InputStream imageInput = null;
        try { imageInput = image.getInputStream(); } catch (Exception ex) {}
        long lVisitRecordId = VerifyUtil.isIds(visitRecordId) ? VerifyUtil.parseLongIds(visitRecordId).get(0) : 0L;
        Map<String, String> visitImageIdUrl = visitPatientService.addSignImage(false, lVisitRecordId, imageName, imageInput);
        return visitImageIdUrl;
    }

    @RequestMapping(path = "/visit/patient/nurse/sign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public Map<String, String> setNurseSignImage(HttpServletRequest request,
                                                 @RequestParam(required = true,  name = "visit_record_id") String visitRecordId,
                                                 @RequestParam(required = false, name = "image_name") String imageName,
                                                 @RequestPart(required = true,  name = "image") MultipartFile image
    ) {
        InputStream imageInput = null;
        try { imageInput = image.getInputStream(); } catch (Exception ex) {}
        long lVisitRecordId = VerifyUtil.isIds(visitRecordId) ? VerifyUtil.parseLongIds(visitRecordId).get(0) : 0L;
        Map<String, String> visitImageIdUrl = visitPatientService.addSignImage(true, lVisitRecordId, imageName, imageInput);
        return visitImageIdUrl;
    }

    //=========================================
    //       Updating Patient Visit Record
    //=========================================
    @RequestMapping(path = "/visit/patient", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public NurseVisitPatientBean updateVisitPatientRecord(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "0", name = "visit_record_id") long visitRecordId,
                                                          @RequestParam(defaultValue = "",  name = "service_item_ids") String serviceItemId,
                                                          @RequestParam(defaultValue = "",  name = "visit_record") String visitRecord,
                                                          @RequestParam(defaultValue = "",  name = "visit_time") String visitTime,
                                                          @RequestParam(defaultValue = "",  name = "address") String address,
                                                          @RequestParam(defaultValue = "",  name = "note") String note,
                                                          @RequestParam(defaultValue = "",  name = "patient_record_no") String patientRecordNo
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        Date dVisitTime = new Date(NumberUtil.getTime(visitTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS));
        List<NurseVisitPatientServiceItemBean> serviceItems = visitPatientServiceItemService.getVisitPatientServiceItem(serviceItemId);
        String serviceItemsJson = "";
        if (!VerifyUtil.isListEmpty(serviceItems)) {
            serviceItemsJson = jsonUtil.toJsonString(serviceItems);
        }
        NurseVisitPatientBean visit = visitPatientService.updateVisitRecord(nurseId, visitRecordId, visitRecord, serviceItemsJson, dVisitTime, address, patientRecordNo, note);
        return visit;
    }

    @RequestMapping(path = "/visit/patient", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public long setDeleteStatusOfVisitPatientRecord(HttpServletRequest request,
                                                    @RequestParam(defaultValue = "0", name = "visit_record_id") long visitRecordId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long nurseId = userDetails.isNurse() ? userDetails.getId() : null;
        List<Long> updateIds = visitPatientService.setDeleteStatusVisitRecordByIds(nurseId, Arrays.asList(new Long[]{visitRecordId}));
        return updateIds.get(0);
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

    @RequestMapping(path = "/visit/patient/sign", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public Map<String, Boolean> deletePatientSignImage(HttpServletRequest request,
                                                       @RequestParam(defaultValue = "0", name = "visit_record_id") long visitRecordId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        long patientSignId = visitPatientService.deletePatientSignImage(nurseId, visitRecordId);
        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("deleted", Boolean.TRUE);
        return retVal;
    }

    @RequestMapping(path = "/visit/patient/nurse/sign", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public Map<String, Boolean> deleteNurseSignImage(HttpServletRequest request,
                                                    @RequestParam(defaultValue = "0", name = "visit_record_id") long visitRecordId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        long patientSignId = visitPatientService.deleteNurseSignImage(nurseId, visitRecordId);
        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("deleted", Boolean.TRUE);
        return retVal;
    }

    @RequestMapping(path = "/visit/patient/image/ids", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public Map<String, Boolean> deleteVisitPatientRecordImage(HttpServletRequest request,
                                                              @RequestParam(defaultValue = "0", name = "visit_record_id") long visitRecordId,
                                                              @RequestParam(defaultValue = "0", name = "image_ids")     String strImageIds
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        long nurseId = userDetails.isAdmin() ? 0L : userDetails.getId();
        List<Long> imageIds = VerifyUtil.isIds(strImageIds) ? VerifyUtil.parseLongIds(strImageIds) : new ArrayList<>();
        for (Long tmp : imageIds) {
            visitPatientService.deleteVisitRecordImage(nurseId, visitRecordId, tmp);
        }
        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("deleted", Boolean.TRUE);
        return retVal;
    }

    @RequestMapping(path = "/visit/patient/pdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Map<String, String> makePdfVisitPatientRecord(HttpServletRequest request,
                                                         @RequestParam(defaultValue = "0", name = "user_id") long userId,
                                                         @RequestParam(defaultValue = "0", name = "patient_id") long patientId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
        if (userDetails.isNurse() || userDetails.isNurseManager()) {
            Long hospitalId   = tmp[0];
            Long departmentId = tmp[1];
            if (userId>0 && patientId>0) {
                List<NurseVisitPatientBean> visitPatientRecords = visitPatientService.getVisitRecordByCondition(userId, patientId, null, null, ServiceVendorType.HOSPITAL, hospitalId, departmentId, CommonStatus.DELETED, 0, 0);
                if (!VerifyUtil.isListEmpty(visitPatientRecords)) {
                    NurseBean nurse = (NurseBean) userDetails.getUserBean();
                    UserAddressBean address = userAddressService.getUserDefaultAddress(userId);
                    String temporaryFilePath = PdfUtil.createVisitPatientRecordPrint(
                            getHospitalAddress(nurse),
                            "",
                            visitPatientRecords.get(0).getUser(),
                            address,
                            visitPatientRecords.get(0).getPatient(),
                            visitPatientRecords,
                            temporaryFileStorageService,
                            nurse360Utility
                    );
                    Map<String, String> map = new HashMap<>();
                    map.put("pdf_url", nurse360Utility.getHttpPrefix()+temporaryFilePath);
                    return map;
                }
            }
        }
        throw new BadRequestException(ErrorCode.NURSE360_CREATE_PDF_FAILED);
    }

    private String getHospitalAddress(NurseBean nurse) {
        NurseHospitalRelationBean nurseHospitalRelation = nurseHospitalRelationService.getRelationByNurseId(nurse.getId(), "");
        if (null==nurseHospitalRelation) {
            return "";
        }
        else {
            StringBuilder str = new StringBuilder();
            if (null!=nurseHospitalRelation.getHospital()) {
                str.append(nurseHospitalRelation.getHospital().getName());
            }
            if (null!=nurseHospitalRelation.getDepartment().getName()) {
                str.append(nurseHospitalRelation.getDepartment().getName());
            }
            return str.toString();
        }
    }
}
