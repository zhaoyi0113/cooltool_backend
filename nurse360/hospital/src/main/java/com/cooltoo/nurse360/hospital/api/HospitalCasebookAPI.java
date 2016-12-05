package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.go2nurse.beans.CaseBean;
import com.cooltoo.go2nurse.beans.CasebookBean;
import com.cooltoo.go2nurse.service.CasebookService;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/25.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalCasebookAPI {

    private static final Logger logger = LoggerFactory.getLogger(HospitalCasebookAPI.class);

    @Autowired private CasebookService casebookService;

    //==================================================================
    //            Authentication of ADMINISTRATOR Role
    //==================================================================
    @RequestMapping(path = "/admin/casebook/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countCasebook(HttpServletRequest request,
                              @RequestParam(defaultValue = "0",  name = "hospital_id")   String strHospitalId,
                              @RequestParam(defaultValue = "0",  name = "department_id") String strDepartmentId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment(strHospitalId, strDepartmentId, userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];
        long count = casebookService.countCasebookByCondition(null, null, null, null, hospitalId, departmentId);
        return count;
    }

    @RequestMapping(path = "/admin/casebook", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<CasebookBean> getCasebook(HttpServletRequest request,
                                          @RequestParam(defaultValue = "0",  name = "hospital_id")   String strHospitalId,
                                          @RequestParam(defaultValue = "0",  name = "department_id") String strDepartmentId,
                                          @RequestParam(defaultValue = "0",  name = "index") int pageIndex,
                                          @RequestParam(defaultValue = "10", name = "number") int sizePerPage
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment(strHospitalId, strDepartmentId, userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];
        List<CasebookBean> casebook = casebookService.getCasebookByCondition(null, null, null, null, hospitalId, departmentId, pageIndex, sizePerPage);
        return casebook;
    }

    //=================================================================================
    //
    //                     Authentication of NURSE/MANAGER Role
    //
    //=================================================================================

    //==========================================
    //           Casebook Service
    //==========================================
    @RequestMapping(path = "/casebook/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countCasebook(HttpServletRequest request) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment("", "", userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];
        long count = casebookService.countCasebookByCondition(null, null, null, null, hospitalId, departmentId);
        return count;
    }

    @RequestMapping(path = "/casebook", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<CasebookBean> getCasebook(HttpServletRequest request,
                                          @RequestParam(defaultValue = "0",  name = "index") int pageIndex,
                                          @RequestParam(defaultValue = "10", name = "number") int sizePerPage
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment("", "", userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];
        List<CasebookBean> casebook = casebookService.getCasebookByCondition(null, null, null, null, hospitalId, departmentId, pageIndex, sizePerPage);
        return casebook;
    }

    @RequestMapping(path = "/casebook/{casebook_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public CasebookBean getCasebookWithCases(HttpServletRequest request,
                                             @PathVariable long casebook_id
    ) {
        CasebookBean casebook = casebookService.getCasebookWithCases(casebook_id);
        return casebook;
    }

    @RequestMapping(path = "/casebook", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public List<Long> deleteCasebook(HttpServletRequest request,
                                     @RequestParam(defaultValue = "0", name = "casebook_id") long casebookId
    ) {
        List<Long> allIds = new ArrayList<>();
        allIds.add(casebookId);
        allIds = casebookService.deleteCasebookByIds(null, allIds);
        return allIds;
    }

    @RequestMapping(path = "/casebook", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, Long> addCasebook(HttpServletRequest request,
                                         @RequestParam(defaultValue = "0", name = "user_id")       long userId,
                                         @RequestParam(defaultValue = "0", name = "patient_id")    long patientId,
                                         @RequestParam(defaultValue = "",  name = "description") String description,
                                         @RequestParam(defaultValue = "",  name = "name")        String name
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment("", "", userDetails);
        Integer hospitalId   = tmp[0];
        Integer departmentId = tmp[1];
        Long nurseId = 0L;
        if (userDetails.isNurse() || userDetails.isNurseManager()) {
            nurseId = userDetails.getId();
        }
        long casebookId = casebookService.addCasebook(hospitalId, departmentId, nurseId, userId, patientId, description, name);
        Map<String, Long> retValue = new HashMap<>();
        retValue.put("id", casebookId);
        return retValue;
    }

    @RequestMapping(path = "/casebook", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public CasebookBean editCasebook(HttpServletRequest request,
                                     @RequestParam(defaultValue = "0", name = "casebook_id")   long casebookId,
                                     @RequestParam(defaultValue = "",  name = "name")        String name,
                                     @RequestParam(defaultValue = "",  name = "description") String description
    ) {
        CasebookBean casebook = casebookService.updateCasebook(null, casebookId, name, description);
        return casebook;
    }


    //==========================================
    //           Case Service
    //==========================================
    @RequestMapping(path = "/casebook/case/{case_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public CaseBean getCase(@PathVariable long case_id) {
        CaseBean _case = casebookService.getCaseById(case_id);
        return _case;
    }

    @RequestMapping(path = "/casebook/case/{case_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public List<Long> deleteCase(@PathVariable long case_id) {
        List<Long> allIds = new ArrayList<>();
        allIds.add(case_id);
        allIds = casebookService.deleteCase(allIds);
        return allIds;
    }

    @RequestMapping(path = "/casebook/case", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Map<String, Long> addCase(HttpServletRequest request,
                                     @RequestParam(defaultValue = "0", name = "casebook_id") long casebookId,
                                     @RequestParam(defaultValue = "",  name = "case_record") String caseRecord
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long nurseId = 0L;
        if (userDetails.isNurse() || userDetails.isNurseManager()) {
            nurseId = userDetails.getId();
        }
        long caseId = casebookService.addCase(casebookId, nurseId, caseRecord);
        Map<String, Long> returnValue = new HashMap<>();
        returnValue.put("case_id", caseId);
        return returnValue;
    }

    @RequestMapping(path = "/casebook/case", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Map<String, Long> updateCase(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0", name = "case_id") long caseId,
                                        @RequestParam(defaultValue = "",  name = "case_record") String caseRecord
    ) {
        caseId = casebookService.updateCase(caseId, caseRecord);
        Map<String, Long> returnValue = new HashMap<>();
        returnValue.put("case_id", caseId);
        return returnValue;
    }

    @RequestMapping(path = "/casebook/case/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public Map<String, String> addCaseImage(HttpServletRequest request,
                                            @RequestParam(defaultValue = "0", name = "casebook_id")  long casebookId,
                                            @RequestParam(defaultValue = "0", name = "case_id")      long caseId,
                                            @RequestParam(defaultValue = "",  name = "image_name") String imageName,
                                            @RequestPart(name = "image", required = true)   MultipartFile image
    ) throws IOException {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        Long nurseId = 0L;
        if (userDetails.isNurse() || userDetails.isNurseManager()) {
            nurseId = userDetails.getId();
        }
        Map<String, String> imageIdToUrl = casebookService.addCaseImage(nurseId, casebookId, caseId, imageName, image.getInputStream());
        return imageIdToUrl;
    }

    @RequestMapping(path = "/casebook/case/image", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public Map<String, Boolean> deleteCaseImage(HttpServletRequest request,
                                                @RequestParam(defaultValue = "0", name = "casebook_id") long casebookId,
                                                @RequestParam(defaultValue = "0", name = "case_id")     long caseId
    ) {
        casebookService.deleteCaseImage(null, casebookId, caseId);
        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("deleted", Boolean.TRUE);
        return retVal;
    }

    @RequestMapping(path = "/casebook/case/image/{image_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
    public Map<String, Boolean> deleteCaseImage(HttpServletRequest request,
                                                @PathVariable long imageId

    ) {
        casebookService.deleteCaseImage(imageId);
        Map<String, Boolean> retVal = new HashMap<>();
        retVal.put("deleted", Boolean.TRUE);
        return retVal;
    }
}
