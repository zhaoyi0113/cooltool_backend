package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpRecordBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.service.NursePatientFollowUpRecordService;
import com.cooltoo.go2nurse.service.NursePatientFollowUpService;
import com.cooltoo.go2nurse.service.UserQuestionnaireAnswerService;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 23/03/2017.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalFollowUpRecordAPI {

    private static final Logger logger = LoggerFactory.getLogger(HospitalFollowUpRecordAPI.class);

    @Autowired private NursePatientFollowUpRecordService patientFollowUpRecordService;
    @Autowired private NursePatientFollowUpService       patientFollowUpService;
    @Autowired private UserQuestionnaireAnswerService    patientAnswerService;



    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    //=========================================
    //       Getting Patient Visit Record
    //=========================================
    @RequestMapping(path = "/follow/up/questionnaire/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countFollowUpQuestionnaire(HttpServletRequest request) {
        long count = 0;
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (userDetails.isNurseManager()) {
            Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment("", "", userDetails);
            Integer hospitalId = tmp[0];
            Integer departmentId = tmp[1];

            List<Long> followUpIds = patientFollowUpService.getPatientFollowUpIds(hospitalId, departmentId);
            count = patientFollowUpRecordService.countPatientFollowUpRecordByFollowUpIds(
                    CommonStatus.DELETED, PatientFollowUpType.QUESTIONNAIRE, YesNoEnum.YES, null, followUpIds, null
            );
            return count;
        }
        else if (userDetails.isNurse()) {
            List<Long> followUpIds = patientFollowUpService.getPatientFollowUpIds(null, null, userDetails.getId());
            count = patientFollowUpRecordService.countPatientFollowUpRecordByFollowUpIds(
                    CommonStatus.DELETED, PatientFollowUpType.QUESTIONNAIRE, YesNoEnum.YES, null, followUpIds, null
            );
            return count;
        }
        return count;
    }

    @RequestMapping(path = "/follow/up/questionnaire", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<NursePatientFollowUpRecordBean> getFollowUpQuestionnaire(HttpServletRequest request,
                                                                         @RequestParam(required = false, defaultValue = "0", name = "index")  int pageIndex,
                                                                         @RequestParam(required = false, defaultValue = "10",name = "number") int sizePerPage
                                                                         ) {
        List<NursePatientFollowUpRecordBean> questionnaires = new ArrayList<>();
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (userDetails.isNurseManager()) {
            Integer[] tmp = SecurityUtil.newInstance().getHospitalDepartment("", "", userDetails);
            Integer hospitalId = tmp[0];
            Integer departmentId = tmp[1];

            List<Long> followUpIds = patientFollowUpService.getPatientFollowUpIds(hospitalId, departmentId);
            questionnaires = patientFollowUpRecordService.getPatientFollowUpRecordByFollowUpIds(
                    CommonStatus.DELETED, PatientFollowUpType.QUESTIONNAIRE, YesNoEnum.YES, null, followUpIds, null,
                    NursePatientFollowUpRecordService.ORDER_BY_NONE,
                    pageIndex, sizePerPage, false
            );
            return questionnaires;
        }
        else if (userDetails.isNurse()) {
            List<Long> followUpIds = patientFollowUpService.getPatientFollowUpIds(null, null, userDetails.getId());
            questionnaires = patientFollowUpRecordService.getPatientFollowUpRecordByFollowUpIds(
                    CommonStatus.DELETED, PatientFollowUpType.QUESTIONNAIRE, YesNoEnum.YES, null, followUpIds, null,
                    NursePatientFollowUpRecordService.ORDER_BY_NONE,
                    pageIndex, sizePerPage, false
            );
            return questionnaires;
        }
        return questionnaires;
    }

    @RequestMapping(path = "/follow/up/questionnaire/{follow_up_questionnaire_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public QuestionnaireBean getQuestionnaireAnswered(HttpServletRequest request,
                                                      @PathVariable(value = "follow_up_questionnaire_id") long followUpRecordId
    ) {
        NursePatientFollowUpRecordBean followUpRecord = patientFollowUpRecordService.getPatientFollowUpRecordById(followUpRecordId);
        if (null!=followUpRecord) {
            NursePatientFollowUpBean followUp = patientFollowUpService.getPatientFollowUpWithoutInfo(followUpRecord.getFollowUpId());
            if (null!=followUp) {
                QuestionnaireBean questionnaire = patientAnswerService.getUserQuestionnaireWithAnswer(
                        followUp.getUserId(),
                        followUpRecord.getRelativeQuestionnaireAnswerGroupId(),
                        YesNoEnum.YES.equals(followUpRecord.getPatientReplied())
                );
                if (null!=questionnaire) {
                    return questionnaire;
                }
            }
            return null;
        }
        throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
    }
}
