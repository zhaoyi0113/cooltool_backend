package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.beans.NurseQualificationBean;
import com.cooltoo.beans.NurseQualificationFileBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.constants.WorkFileType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.service.NurseDoctorScoreService;
import com.cooltoo.go2nurse.service.NurseOrderRelationService;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.nurse360.service.NursePatientRelationServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.NurseQualificationService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired private NurseQualificationService nurseQualificationService;
    @Autowired private Nurse360Utility utility;

    //=============================================================
    //            Authentication of ADMINISTRATOR Role
    //=============================================================


    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/nurse/{nurse_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public NurseBean getNurse(HttpServletRequest request,
                              @PathVariable long nurseId
    ) {
        return nurseService.getNurseById(nurseId);
    }

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

    //===============================================================
    //                   Qualification Service
    //===============================================================
    @RequestMapping(path = "/nurse/qualification", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NurseQualificationBean> getNurseQualification(HttpServletRequest request,
                                                              @RequestParam(defaultValue = "", name = "nurse_id") long nurseId
    ) {
        NurseBean nurse = nurseService.getNurseById(nurseId);
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (userDetails.isNurse() && nurseId==userDetails.getId()) {
            return nurseQualificationService.getAllNurseQualifications(nurseId, utility.getHttpPrefixForNurseGo());
        }
        else if (userDetails.isNurseManager()) {
            Integer hospitalId = (Integer) userDetails.getProperty(HospitalAdminUserDetails.HOSPITAL_ID);
            Integer departmentId = (Integer) userDetails.getProperty(HospitalAdminUserDetails.DEPARTMENT_ID);
            NurseHospitalRelationBean nurseHospitalRelation = (NurseHospitalRelationBean) nurse.getProperty(NurseBean.HOSPITAL_DEPARTMENT);
            if (null!=nurseHospitalRelation
                    && hospitalId==nurseHospitalRelation.getHospitalId()
                    && departmentId==nurseHospitalRelation.getDepartmentId()) {
                return nurseQualificationService.getAllNurseQualifications(nurseId, utility.getHttpPrefixForNurseGo());
            }
        }
        else if (userDetails.isAdmin()) {
            return nurseQualificationService.getAllNurseQualifications(nurseId, utility.getHttpPrefixForNurseGo());
        }
        return new ArrayList<>();
    }

    @RequestMapping(path = "/nurse/qualification", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public NurseQualificationFileBean editNurseQualification(HttpServletRequest request,
                                                               @RequestParam(defaultValue = "", name = "nurse_id") long nurseId,
                                                               @RequestParam(defaultValue = "", name = "qualification_file_id") long qualificationFileId,
                                                               @RequestParam(defaultValue = "", name = "type") String workfileType,
                                                               @RequestParam(defaultValue = "", name = "expiry") String expiryTime,
                                                               @RequestParam(defaultValue = "", name = "file_name") String fileName,
                                                               @RequestParam(defaultValue = "", name = "file") MultipartFile file
    ) {
        InputStream workFileInputStream = null;
        Date expiryDate = null;
        try {
            workFileInputStream = file.getInputStream();
            if (!VerifyUtil.isStringEmpty(expiryTime)) {
                long millisecond = NumberUtil.getTime(expiryTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
                expiryDate = new Date(millisecond);
            }
        } catch (Exception ex) {
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }

        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = "nurse_qualification_file_" + System.currentTimeMillis();
        }
        NurseQualificationFileBean qualificationFile = nurseQualificationService.updateQualificationFile(
                qualificationFileId,
                workfileType,
                fileName,
                workFileInputStream,
                expiryDate,
                utility.getHttpPrefixForNurseGo());
        return qualificationFile;
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
        Map<Long, List<NurseQualificationFileBean>> nurseQualificationFiles = nurseQualificationService.getAllNurseQualificationFiles(nurseIds, "");
        for (NurseBean tmp : nurses) {
            Long patientNumber = nursePatientNumber.get(tmp.getId());
            Long orderNumber = nurseOrderCompleted.get(tmp.getId());
            Float score = nurseScore.get(tmp.getId());
            List<NurseQualificationFileBean> qualificationFiles = nurseQualificationFiles.get(tmp.getId());
            tmp.setProperty(NurseBean.SCORE, null==score ? 0.0 : score);
            tmp.setProperty(NurseBean.COMPLETED_ORDER_COUNT, null==orderNumber ? 0 : orderNumber);
            tmp.setProperty(NurseBean.PATIENT_COUNT, null==patientNumber ? 0 : patientNumber);
            tmp.setProperty(NurseBean.QUALIFICATION, nurseQualificationFiles);
            boolean hasEmployeeCard = false;
            boolean hasQualification = false;
            if (!VerifyUtil.isListEmpty(qualificationFiles)) {
                for (NurseQualificationFileBean tmpFile : qualificationFiles) {
                    if (null==tmpFile) {
                        continue;
                    }
                    if (null==tmpFile.getWorkfileType()) {
                        continue;
                    }
                    if (tmpFile.getWorkfileType().getType() == WorkFileType.EMPLOYEES_CARD) {
                        hasEmployeeCard = true;
                    }
                    if (tmpFile.getWorkfileType().getType() == WorkFileType.QUALIFICATION) {
                        hasQualification = true;
                    }
                }
            }
            tmp.setProperty(NurseBean.HAS_CARD_AND_QUALIFICATION, hasEmployeeCard && hasQualification);
        }
    }
}
