package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.converter.CasebookBeanConverter;
import com.cooltoo.go2nurse.entities.CasebookEntity;
import com.cooltoo.go2nurse.repository.CasebookRepository;
import com.cooltoo.go2nurse.service.notification.Notifier;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by hp on 2016/8/28.
 */
@Service("CasebookService")
public class CasebookService {

    private static final Logger logger = LoggerFactory.getLogger(CasebookService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private CasebookRepository repository;
    @Autowired private CasebookBeanConverter beanConverter;
    @Autowired private ImageInCaseService imageService;
    @Autowired private CaseService caseService;

    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    @Autowired private NurseServiceForGo2Nurse nurseService;

    @Autowired private Notifier notifier;

    //===============================================================
    //             get ----  admin using
    //===============================================================

    public long countCasebookByCondition(Long userId, Long patientId, Long nurseId, String contentLike) {
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        long count = repository.countByConditions(userId, patientId, nurseId, contentLike);
        logger.info("count casebook user={} patientId={} nurseId={} contentLike={}, count is {}",
                userId, patientId, nurseId, contentLike, count);
        return count;
    }

    public List<CasebookBean> getCasebookByCondition(Long userId, Long patientId, Long nurseId, String contentLike, int pageIndex, int sizePerPage) {
        logger.info("get casebook user={} patientId={} nurseId={} contentLike={} at page={} sizePerPage={}",
                userId, patientId, nurseId, contentLike, pageIndex, sizePerPage);
        List<CasebookBean> beans;
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<CasebookEntity> resultSet = repository.findByConditions(userId, patientId, nurseId, contentLike, request);
        beans = entitiesToBeansForCasebook(resultSet);
        fillOtherPropertiesForCasebook(beans);

        logger.warn("casebook count={}", beans.size());
        return beans;
    }


    //===============================================================
    //             get ----  patient using
    //===============================================================

    public List<CasebookBean> getUserCasebook(Long userId, Long nurseId, String contentLike, int pageIndex, int sizePerPage) {
        logger.info("user={} nusre={} get casebook (contentLike={}) at page={} sizePerPage={}",
                userId, nurseId, contentLike, pageIndex, sizePerPage);
        List<CasebookBean> beans;
        if (null==userId && VerifyUtil.isStringEmpty(contentLike)) {
            beans = new ArrayList<>();
        }
        else {
            contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
            PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
            Page<CasebookEntity> resultSet = repository.findByUserNurseStatusNotAndContentLike(userId, nurseId, CommonStatus.DELETED, contentLike, request);
            beans = entitiesToBeansForCasebook(resultSet);
            fillOtherPropertiesForCasebook(beans);
        }
        logger.warn("casebook count={}", beans.size());
        return beans;
    }

    public List<CasebookBean> getUserCasebook(Long userId, Long nurseId, int pageIndex, int sizePerPage) {
        logger.info("user={} get casebook nurseId={} at page={} sizePerPage={}", userId, nurseId, pageIndex, sizePerPage);
        List<CasebookBean> beans;
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<CasebookEntity> resultSet = repository.findByUserIdAndStatusNotAndNurseId(userId, CommonStatus.DELETED, nurseId, request);
        beans = entitiesToBeansForCasebook(resultSet);
        fillOtherPropertiesForCasebook(beans);
        logger.warn("casebook count={}", beans.size());
        return beans;
    }

    public CasebookBean getCasebook(long casebookId) {
        logger.info("get casebookId={}", casebookId);
        CasebookEntity resultSet = repository.findOne(casebookId);
        if (null==resultSet) {
            logger.info("there is no record");
            return null;
        }
        List<CasebookEntity> entities = new ArrayList<>();
        entities.add(resultSet);

        List<CasebookBean> casebook = entitiesToBeansForCasebook(entities);
        fillOtherPropertiesForCasebook(casebook);
        return casebook.get(0);
    }

    public CasebookBean getCasebookWithCases(Long casebookId) {
        logger.info("get casebook={} with cases", casebookId);
        CasebookBean casebook = getCasebook(casebookId);
        List<CaseBean> cases = getCaseByCasebookId(casebookId);
        casebook.setCases(cases);
        return casebook;
    }

    public boolean existsCasebook(long casebookId) {
        return repository.exists(casebookId);
    }

    private List<CasebookBean> entitiesToBeansForCasebook(Iterable<CasebookEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<CasebookBean> beans = new ArrayList<>();
        for(CasebookEntity tmp : entities) {
            CasebookBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherPropertiesForCasebook(List<CasebookBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        List<Long> patientIds = new ArrayList<>();
        List<Long> nurseIds = new ArrayList<>();
        List<Long> casebookIds = new ArrayList<>();
        for (CasebookBean tmp : beans) {
            if (!userIds.contains(tmp.getUserId())) {
                userIds.add(tmp.getUserId());
            }
            if (!patientIds.contains(tmp.getPatientId())) {
                patientIds.add(tmp.getPatientId());
            }
            if (!casebookIds.contains(tmp.getId())) {
                casebookIds.add(tmp.getId());
            }
            if (!nurseIds.contains(tmp.getNurseId())) {
                nurseIds.add(tmp.getNurseId());
            }
        }

        Map<Long, UserBean> userIdToBean = userService.getUserIdToBean(userIds);
        Map<Long, PatientBean> patientIdToBean = patientService.getPatientIdToBean(patientIds);
        Map<Long, NurseBean> nurseIdToBean = nurseService.getNurseIdToBean(nurseIds);
        Map<Long, Long> casebookAndCaseSize = caseService.getCaseSizeInCasebooks(casebookIds);

        // fill properties
        for (CasebookBean tmp : beans) {
            UserBean user = userIdToBean.get(tmp.getUserId());
            tmp.setUser(user);
            PatientBean patient = patientIdToBean.get(tmp.getPatientId());
            tmp.setPatient(patient);
            NurseBean nurse = nurseIdToBean.get(tmp.getNurseId());
            tmp.setNurse(nurse);
            Long caseSize = casebookAndCaseSize.get(tmp.getId());
            tmp.setCaseSize(null==caseSize ? 0 : caseSize);
        }
    }

    private void fillOtherPropertiesForSingleCasebook(CasebookBean casebook) {
        if (null==casebook) {
            return;
        }

        Long userId = casebook.getUserId();
        Long patientId = casebook.getPatientId();
        long nurseId = casebook.getNurseId();
        Long casebookId = casebook.getId();

        UserBean user = userService.getUser(userId);
        PatientBean patient = patientService.getOneById(patientId);
        NurseBean nurse = nurseId<=0 ? null : nurseService.getNurseById(nurseId);
//        List<CaseBean> cases = caseService.getCaseByCasebookId(casebookId);
        Map<Long, Long> casebookAndCaseSize = caseService.getCaseSizeInCasebooks(Arrays.asList(new Long[]{casebookId}));

        // fill properties
        casebook.setUser(user);
        casebook.setPatient(patient);
        casebook.setNurse(nurse);
//        casebook.setCases(cases);
        Long caseSize = casebookAndCaseSize.get(casebookId);
        casebook.setCaseSize(null==caseSize ? 0 : caseSize);
    }


    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public List<Long> deleteCasebookByIds(Long nurseId, List<Long> casebookIds) {
        logger.info("delete casebook by casebookIds={}.", casebookIds);
        List<Long> retValue = new ArrayList<>();
        if (VerifyUtil.isListEmpty(casebookIds)) {
            return retValue;
        }

        List<CasebookEntity> casebooks = repository.findAll(casebookIds);
        if (VerifyUtil.isListEmpty(casebooks)) {
            logger.info("delete nothing");
            return retValue;
        }

        for (CasebookEntity tmp : casebooks) {
            if (tmp.getNurseId()==nurseId) {
                continue;
            }
            logger.warn("can not delete consultation that not making by yourself={}", tmp);
            return retValue;
        }

        for (CasebookEntity tmp : casebooks) {
            tmp.setStatus(CommonStatus.DELETED);
            retValue.add(tmp.getId());
        }
        repository.save(casebooks);


        return retValue;
    }

    @Transactional
    public CasebookBean updateCasebook(Long nurseId, Long casebookId, String name, String description) {
        logger.info("update casebook={} with name={} description={} nurseId={}",
                casebookId, name, description, nurseId);
        CasebookEntity entity = repository.findOne(casebookId);
        if (null==entity) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (null!=nurseId && nurseId>0 && entity.getNurseId()!=nurseId) {
            logger.info("consultation not belong to nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(name) && !name.trim().equals(entity.getName())) {
            entity.setName(name.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(description) && !description.trim().equals(entity.getDescription())) {
            entity.setDescription(description.trim());
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }

        CasebookBean bean = beanConverter.convert(entity);
        fillOtherPropertiesForSingleCasebook(bean);
        return bean;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addCasebook(long nurseId, long userId, long patientId, String description, String name) {
        logger.info("add casebook with nurseId={} userId={} patientId={} description={} name={}",
                nurseId, userId, patientId, description, (null!=name));
        if (nurseId>0 && !nurseService.existsNurse(nurseId)) {
            logger.info("nurse not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!userService.existUser(userId)) {
            logger.info("userId not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!patientService.existPatient(patientId)) {
            patientId = 0;
        }
        if (VerifyUtil.isStringEmpty(name)) {
            logger.info("case book name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        description = VerifyUtil.isStringEmpty(description) ? "" : description.trim();

        CasebookEntity entity = new CasebookEntity();
        entity.setNurseId(nurseId<0 ? 0 : nurseId);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setDescription(description.trim());
        entity.setName(name);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        return entity.getId();
    }

    //=================================================================================================================
    //*****************************************************************************************************************
    //                                          Case Service
    //*****************************************************************************************************************
    //=================================================================================================================

    //=======================================
    //           getting
    //=======================================
    public CaseBean getCaseById(Long caseId) {
        CaseBean caseBean = caseService.getCaseWithoutInfoById(caseId);
        if (null!=caseBean) {
            List<CaseBean> caseBeans = Arrays.asList(new CaseBean[]{caseBean});
            fillOtherPropertiesForCase(caseBean.getCasebookId(), caseBeans);
        }
        return caseBean;
    }

    private List<CaseBean> getCaseByCasebookId(Long casebookId) {
        List<CaseBean> caseBeans = caseService.getCaseByCasebookId(casebookId);
        fillOtherPropertiesForCase(casebookId, caseBeans);
        return caseBeans;
    }

    private void fillOtherPropertiesForCase(Long casebookId, List<CaseBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> nurseIds = new ArrayList<>();
        List<Long> casebookIds = new ArrayList<>();
        for (CaseBean tmp : beans) {
            nurseIds.add(tmp.getNurseId());
            casebookIds.add(tmp.getCasebookId());
        }

        Map<Long, NurseBean> userIdToBean = nurseService.getNurseIdToBean(nurseIds);
        Map<Long, List<String>> casebookIdToImagesUrl = imageService.getCaseIdToImagesUrl(casebookId);

        for (CaseBean tmp : beans) {
            NurseBean user = userIdToBean.get(tmp.getNurseId());
            tmp.setNurse(user);
            List<String> imagesUrl = casebookIdToImagesUrl.get(tmp.getId());
            tmp.setImagesUrl(imagesUrl);
        }
    }

    //=======================================
    //           deleting
    //=======================================
    @Transactional
    public List<Long> deleteCase(List<Long> caseIds) {
        List<Long> deletedIds = caseService.deleteByIds(caseIds);
        return deletedIds;
    }

    //=======================================
    //           adding
    //=======================================
    @Transactional
    public long addCase(long casebookId, long nurseId, String caseRecord) {
        logger.info("add case, casebookId={} nurseId={} caseRecord={}.",
                casebookId, nurseId, caseRecord);
        CasebookEntity casebook = repository.findOne(casebookId);
        if (null==casebook) {
            logger.error("consultation is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && 0!=casebook.getNurseId() && nurseId!=casebook.getNurseId()) {
            logger.error("consultation not belong this nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long caseId = caseService.addCase(casebookId, nurseId, caseRecord);
        return caseId;
    }

    //=======================================
    //           updating
    //=======================================
    @Transactional
    public long updateCase(long caseId, String caseRecord) {
        logger.info("update case caseId={} caseRecord={}.", caseId, caseRecord);
        caseService.updateCase(caseId, caseRecord);
        return caseId;
    }

    @Transactional
    public Map<String, String> addCaseImage(long userId, long casebookId, long caseId, String imageName, InputStream image) {
        logger.info("user={} add image to casebookId={} caseId={} name={} image={}", userId, casebookId, caseId, imageName, (null!=image));

        CasebookEntity casebook = repository.findOne(casebookId);
        if (null==casebook) {
            logger.error("casebook is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        CaseBean _case = caseService.getCaseWithoutInfoById(caseId);
        if (null==_case) {
            logger.error("case is not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (_case.getCasebookId() != casebookId) {
            logger.error("case is not belong to consultation");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long count = imageService.countImage(casebookId, caseId);
        if (count>=9) {
            logger.warn("the casebook do not need image more than nine");
            return new HashMap<>();
        }

        Map<String, String> idAndUrl = imageService.addImage(casebookId, caseId, imageName, image);
        return idAndUrl;
    }
}
