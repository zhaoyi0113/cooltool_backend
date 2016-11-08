package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpRecordBean;
import com.cooltoo.go2nurse.beans.QuestionnaireBean;
import com.cooltoo.go2nurse.beans.UserConsultationBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.constants.PatientFollowUpType;
import com.cooltoo.go2nurse.converter.NursePatientFollowUpRecordBeanConverter;
import com.cooltoo.go2nurse.entities.NursePatientFollowUpRecordEntity;
import com.cooltoo.go2nurse.repository.NursePatientFollowUpRecordRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/8.
 */
@Service("NursePatientFollowUpRecordService")
public class NursePatientFollowUpRecordService {

    private static final Logger logger = LoggerFactory.getLogger(NursePatientFollowUpRecordService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private NursePatientFollowUpRecordRepository repository;
    @Autowired private NursePatientFollowUpRecordBeanConverter beanConverter;

    @Autowired private NursePatientFollowUpService followUpService;
    @Autowired private UserConsultationService userConsultationService;
    @Autowired private QuestionnaireService questionnaireService;


    //===============================================================
    //             get ----  admin using
    //===============================================================
    public NursePatientFollowUpRecordBean getPatientFollowUpRecordById(long followUpRecordId) {
        logger.info("get patient follow-up record by followUpRecordId={}", followUpRecordId);
        NursePatientFollowUpRecordEntity one = repository.findOne(followUpRecordId);
        if (null==one) {
            return null;
        }
        return beanConverter.convert(one);
    }

    public List<NursePatientFollowUpRecordBean> getPatientFollowUpRecordByFollowUpIds(CommonStatus statusNot,
                                                                                      PatientFollowUpType followUpType,
                                                                                      YesNoEnum patientRelpied,
                                                                                      YesNoEnum nurseRead,
                                                                                      List<Long> followUpIds,
                                                                                      ConsultationTalkStatus talkStatusNotMatch,
                                                                                      int pageIndex, int sizePerPage, boolean noPage)
    {
        logger.info("get patient follow-up record by statusNot={} followUpType={} patientRelpied={} nurseRead={} followUpIds={} at page={} sizePerPage={}",
                statusNot, followUpType, patientRelpied, nurseRead, followUpIds, pageIndex, sizePerPage);
        List<NursePatientFollowUpRecordBean> beans = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(followUpIds)) {
            Iterable<NursePatientFollowUpRecordEntity> resultSet;
            if (noPage) {
                resultSet = repository.findByConditionsByFollowUpIds(statusNot, followUpType, patientRelpied, nurseRead, followUpIds, sort);
            }
            else {
                PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
                resultSet = repository.findByConditionsByFollowUpIds(statusNot, followUpType, patientRelpied, nurseRead, followUpIds, request);
            }
            beans = entitiesToBeans(resultSet);
            fillOtherProperties(beans, talkStatusNotMatch);
        }
        logger.warn("patient follow-up count={}", beans.size());
        return beans;
    }

    public List<NursePatientFollowUpRecordBean> getPatientFollowUpRecordByIds(CommonStatus statusNot,
                                                                              PatientFollowUpType followUpType,
                                                                              YesNoEnum patientRelpied,
                                                                              YesNoEnum nurseRead,
                                                                              List<Long> ids,
                                                                              ConsultationTalkStatus talkStatusNotMatch,
                                                                              int pageIndex, int sizePerPage, boolean noPage)
    {
        logger.info("get patient follow-up record by statusNot={} followUpType={} patientRelpied={} nurseRead={} ids={} at page={} sizePerPage={}",
                statusNot, followUpType, patientRelpied, nurseRead, ids, pageIndex, sizePerPage);
        List<NursePatientFollowUpRecordBean> beans = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(ids)) {
            Iterable<NursePatientFollowUpRecordEntity> resultSet;
            if (noPage) {
                resultSet = repository.findByConditionsByIds(statusNot, followUpType, patientRelpied, nurseRead, ids, sort);
            }
            else {
                PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
                resultSet = repository.findByConditionsByIds(statusNot, followUpType, patientRelpied, nurseRead, ids, request);
            }
            beans = entitiesToBeans(resultSet);
            fillOtherProperties(beans, talkStatusNotMatch);
        }
        logger.warn("patient follow-up count={}", beans.size());
        return beans;
    }

    private List<NursePatientFollowUpRecordBean> entitiesToBeans(Iterable<NursePatientFollowUpRecordEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<NursePatientFollowUpRecordBean> beans = new ArrayList<>();
        for(NursePatientFollowUpRecordEntity tmp : entities) {
            NursePatientFollowUpRecordBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<NursePatientFollowUpRecordBean> beans, ConsultationTalkStatus talkStatusNotMatch) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> consultationIds = new ArrayList<>();
        List<Long> questionnaireIds = new ArrayList<>();
        List<Long> questionnaireAnswerIds = new ArrayList<>();
        for (NursePatientFollowUpRecordBean tmp : beans) {
            if (PatientFollowUpType.CONSULTATION.equals(tmp.getFollowUpType())
                    && !consultationIds.contains(tmp.getRelativeConsultationId())) {
                consultationIds.add(tmp.getRelativeConsultationId());
            }
            if (PatientFollowUpType.QUESTIONNAIRE.equals(tmp.getFollowUpType())) {
                if (!questionnaireIds.contains(tmp.getRelativeQuestionnaireId())) {
                    questionnaireIds.add(tmp.getRelativeQuestionnaireId());
                }
                if (!questionnaireAnswerIds.contains(tmp.getRelativeQuestionnaireAnswerGroupId())) {
                    questionnaireAnswerIds.add(tmp.getRelativeQuestionnaireAnswerGroupId());
                }
            }
        }

        Map<Long, UserConsultationBean> consultationIdToBean = userConsultationService.getUserConsultationIdToBean(consultationIds, talkStatusNotMatch);
        Map<Long, QuestionnaireBean> questionnaireIdToBean = questionnaireService.getQuestionnaireWithQuestionIdToBeanMapByIds(questionnaireIds);

        // fill properties
        for (NursePatientFollowUpRecordBean tmp : beans) {
            Object followUpContent = null;
            if (PatientFollowUpType.CONSULTATION.equals(tmp.getFollowUpType())) {
                long consultationId = tmp.getRelativeConsultationId();
                followUpContent = consultationIdToBean.get(consultationId);
                tmp.setFollowUpContent(followUpContent);
            }
            else if (PatientFollowUpType.QUESTIONNAIRE.equals(tmp.getFollowUpType())) {
                long questionnaireId = tmp.getRelativeQuestionnaireId();
                followUpContent = questionnaireIdToBean.get(questionnaireId);
                tmp.setFollowUpContent(followUpContent);
            }
        }
    }

    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public List<Long> setDeleteStatusPatientFollowUpRecordByIds(List<Long> patientFollowUpRecordId) {
        logger.info("delete patient follow-up record by patientFollowUpRecordId={}.", patientFollowUpRecordId);
        List<Long> retValue = new ArrayList<>();
        if (VerifyUtil.isListEmpty(patientFollowUpRecordId)) {
            return retValue;
        }

        List<NursePatientFollowUpRecordEntity> patientFollowUp = repository.findAll(patientFollowUpRecordId);
        if (VerifyUtil.isListEmpty(patientFollowUp)) {
            logger.info("delete nothing");
            return retValue;
        }

        for (NursePatientFollowUpRecordEntity tmp : patientFollowUp) {
            tmp.setStatus(CommonStatus.DELETED);
            retValue.add(tmp.getId());
        }
        repository.save(patientFollowUp);


        return retValue;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addPatientFollowUpRecord(long followUpId, PatientFollowUpType followUpType, long consultationId, long questionnaireId) {
        logger.info("add patient follow-up record with followUpId={} followUpType={} consultationId={} questionnaireId={}",
                followUpId, followUpType, consultationId, questionnaireId);
        if (!followUpService.existsPatientFollowUp(followUpId)) {
            logger.info("patient follow-up not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (consultationId>0 && !userConsultationService.existsConsultation(consultationId)) {
            logger.info("consultation not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (questionnaireId>0 && !questionnaireService.existsQuestionnaire(questionnaireId)) {
            logger.info("questionnaire not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null==followUpType) {
            logger.info("patient follow-up type is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (consultationId<0 && questionnaireId<0) {
            logger.info("consultation and questionnaire are not set both");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        NursePatientFollowUpRecordEntity entity = new NursePatientFollowUpRecordEntity();
        entity.setFollowUpId(followUpId);
        entity.setFollowUpType(followUpType);
        entity.setRelativeConsultationId(consultationId);
        entity.setRelativeQuestionnaireId(questionnaireId);
        entity.setRelativeQuestionnaireAnswerGroupId(0);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        return entity.getId();
    }
}
