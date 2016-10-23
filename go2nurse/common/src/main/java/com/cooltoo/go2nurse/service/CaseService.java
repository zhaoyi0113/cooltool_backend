package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CaseBean;
import com.cooltoo.go2nurse.converter.CaseBeanConverter;
import com.cooltoo.go2nurse.entities.CaseEntity;
import com.cooltoo.go2nurse.repository.CaseRepository;
import com.cooltoo.go2nurse.repository.CasebookRepository;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhaolisong on 2016/10/23.
 */
@Service("CaseService")
public class CaseService {

    private static final Logger logger = LoggerFactory.getLogger(CaseService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "time"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private CaseRepository repository;
    @Autowired private CaseBeanConverter beanConverter;

    @Autowired private CasebookRepository casebookRepository;
    @Autowired private NurseRepository nurseRepository;
    @Autowired private NurseServiceForGo2Nurse nurseService;


    //==================================================================
    //              GET
    //==================================================================
    public boolean existCase(long caseId) {
        return repository.exists(caseId);
    }

    public CaseBean getCaseWithoutInfoById(long caseId) {
        CaseEntity _case = repository.findOne(caseId);
        if (null==_case) {
            return null;
        }
        return beanConverter.convert(_case);
    }

    public List<CaseBean> getCaseByCasebookId(long casebookId) {
        logger.info("get case by casebookId={}.", casebookId);
        List<Long> casebookIds = new ArrayList<>();
        casebookIds.add(casebookId);
        List<CaseEntity> comments = repository.findByStatusNotAndCasebookIdIn(CommonStatus.DELETED, casebookIds, sort);
        List<CaseBean> retValue = entitiesToBeans(comments);
        logger.info("count is {}", retValue.size());
        return retValue;
    }

    private List<CaseBean> entitiesToBeans(Iterable<CaseEntity> entities) {
        List<CaseBean> retVal = new ArrayList<>();
        if (null==entities) {
            return retVal;
        }

        for (CaseEntity tmp : entities) {
            CaseBean bean = beanConverter.convert(tmp);
            retVal.add(bean);
        }

        return retVal;
    }

    //==================================================================
    //              delete
    //==================================================================
    @Transactional
    public List<Long> deleteByIds(List<Long> caseIds) {
        logger.info("delete case by caseIds {}.", caseIds);
        List<Long> retValue = new ArrayList<>();
        if (null==caseIds || caseIds.isEmpty()) {
            return retValue;
        }

        List<CaseEntity> comments = repository.findByStatusNotAndIdIn(CommonStatus.DELETED, caseIds);
        if (!VerifyUtil.isListEmpty(comments)) {
            for (CaseEntity comment : comments) {
                comment.setStatus(CommonStatus.DELETED);
                retValue.add(comment.getId());
            }
            repository.save(comments);
        }
        logger.info("count is {}", retValue.size());
        return retValue;
    }

    //==================================================================
    //                           adding
    //==================================================================
    @Transactional
    public long addCase(long casebookId, long nurseId, String caseRecord) {
        logger.info("add case by casebookId={} nurseId={} caseRecord={}."
                , casebookId, nurseId, caseRecord);
        if (!casebookRepository.exists(casebookId)) {
            logger.error("casebook not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!nurseRepository.exists(nurseId)) {
            logger.error("nurse not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (VerifyUtil.isStringEmpty(caseRecord)) {
            logger.error("talk content is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        CaseEntity entity = new CaseEntity();
        entity.setCasebookId(casebookId);
        entity.setNurseId(nurseId);
        entity.setCaseRecord(caseRecord.trim());
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        return entity.getId();
    }

    //==================================================================
    //                           updating
    //==================================================================
    @Transactional
    public long updateCase(long caseId, String caseRecord) {
        logger.info("update case by caseId={} caseRecord={}.", caseId, caseRecord);
        CaseEntity _case = repository.findOne(caseId);
        if (null==_case) {
            logger.error("case not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(caseRecord) && !caseRecord.trim().equals(_case.getCaseRecord())) {
            _case.setCaseRecord(caseRecord.trim());
            changed = true;
        }
        if (changed) {
            repository.save(_case);
        }

        return caseId;
    }
}
