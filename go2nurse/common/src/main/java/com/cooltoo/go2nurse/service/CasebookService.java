package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.converter.CasebookBeanConverter;
import com.cooltoo.go2nurse.entities.CasebookEntity;
import com.cooltoo.go2nurse.repository.CasebookRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
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
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;

    //===============================================================
    //             get ----  admin using
    //===============================================================

    public long countCasebookByCondition(Long userId, Long patientId, Long nurseId, String contentLike,
                                         Integer hospitalId, Integer departmentId) {
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        long count = repository.countByConditions(userId, patientId, nurseId, contentLike, hospitalId, departmentId);
        logger.info("count casebook user={} patientId={} nurseId={} contentLike={} hospitalId={} departmentId={}, count is {}",
                userId, patientId, nurseId, contentLike, hospitalId, departmentId, count);
        return count;
    }

    public List<CasebookBean> getCasebookByCondition(Long userId, Long patientId, Long nurseId, String contentLike,
                                                     Integer hospitalId, Integer departmentId,
                                                     int pageIndex, int sizePerPage) {
        logger.info("get casebook user={} patientId={} nurseId={} contentLike={} hospitalId={} departmentId={} at page={} sizePerPage={}",
                userId, patientId, nurseId, contentLike, hospitalId, departmentId, pageIndex, sizePerPage);
        List<CasebookBean> beans;
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<CasebookEntity> resultSet = repository.findByConditions(userId, patientId, nurseId, contentLike, hospitalId, departmentId, request);
        beans = entitiesToBeansForCasebook(resultSet);
        fillOtherPropertiesForCasebook(beans);

        logger.warn("casebook count={}", beans.size());
        return beans;
    }


    //===============================================================
    //             get ----  patient using
    //===============================================================
    public long countUserCasebook(Long otherNurseId, Long userId, Long patientId,
                                  String contentLike,
                                  Integer hospitalId, Integer departmentId,
                                  Long nurseSelfId, YesNoEnum nurseSelfHidden
    ) {
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        long count = repository.countNurseCasebookByConditions(otherNurseId, userId, patientId, contentLike, CommonStatus.DELETED, hospitalId, departmentId, nurseSelfId, nurseSelfHidden);
        logger.info("count casebook otherNurseId={} user={} patientId={} contentLike={} hospitalId={} departmentId={} nurseSelfId={} nurseSelfHidden={}, count is {}",
                userId, patientId, otherNurseId, contentLike, hospitalId, departmentId, nurseSelfId, nurseSelfHidden, count);
        return count;
    }

    public List<CasebookBean> getUserCasebook(Long otherNurseId, Long userId, Long patientId,
                                              String contentLike,
                                              Integer hospitalId, Integer departmentId,
                                              Long nurseSelfId, YesNoEnum nurseSelfHidden,
                                              int pageIndex, int sizePerPage
    ) {
        logger.info("get casebook otherNurseId={} user={} patientId={} contentLike={} hospitalId={} departmentId={} nurseSelfId={} nurseSelfHidden={} at page={} sizePerPage={}",
                userId, patientId, otherNurseId, contentLike, hospitalId, departmentId, nurseSelfId, nurseSelfHidden, pageIndex, sizePerPage);

        List<CasebookBean> beans;
        if (null==otherNurseId && null==userId && null==patientId
                && VerifyUtil.isStringEmpty(contentLike)
                && null==hospitalId && null==departmentId
                && null==nurseSelfId && null==nurseSelfHidden) {
            beans = new ArrayList<>();
        }
        else {
            contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
            PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
            Page<CasebookEntity> resultSet = repository.findNurseCasebookByConditions(otherNurseId, userId, patientId, contentLike, CommonStatus.DELETED, hospitalId, departmentId, nurseSelfId, nurseSelfHidden, request);
            beans = entitiesToBeansForCasebook(resultSet);
            fillOtherPropertiesForCasebook(beans);
        }

        logger.info("get casebook count={}", beans.size());

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
        Map<Long, Date> casebookAndCaseLastRecordTime = caseService.getCaseRecentTimeInCasebooks(casebookIds);

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
            Date caseLastRecordTime = casebookAndCaseLastRecordTime.get(tmp.getId());
            tmp.setRecentRecordTime(caseLastRecordTime);
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
        Map<Long, Date> casebookAndCaseLastRecordTime = caseService.getCaseRecentTimeInCasebooks(Arrays.asList(new Long[]{casebookId}));

        // fill properties
        casebook.setUser(user);
        casebook.setPatient(patient);
        casebook.setNurse(nurse);
//        casebook.setCases(cases);
        Long caseSize = casebookAndCaseSize.get(casebookId);
        casebook.setCaseSize(null==caseSize ? 0 : caseSize);
        Date caseLastRecordTime = casebookAndCaseLastRecordTime.get(casebookId);
        casebook.setRecentRecordTime(caseLastRecordTime);
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
            logger.warn("can not delete casebook that not making by yourself={}", tmp);
            throw new BadRequestException(ErrorCode.NURSE360_CASEBOOK_OR_CASE_NOT_BELONG_TO_YOU);
        }

        for (CasebookEntity tmp : casebooks) {
            tmp.setStatus(CommonStatus.DELETED);
            retValue.add(tmp.getId());
        }
        repository.save(casebooks);


        return retValue;
    }

    @Transactional
    public CasebookBean updateCasebook(Long nurseId, Long casebookId, String name, String description, YesNoEnum hidden, Date time) {
        logger.info("update casebook={} with name={} description={} nurseId={}",
                casebookId, name, description, nurseId);
        CasebookEntity entity = repository.findOne(casebookId);
        if (null==entity) {
            logger.error("casebook is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (null!=nurseId && nurseId>0 && entity.getNurseId()!=nurseId) {
            logger.info("casebook not belong to nurse");
            throw new BadRequestException(ErrorCode.NURSE360_CASEBOOK_OR_CASE_NOT_BELONG_TO_YOU);
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
        if (null!=hidden && !hidden.equals(entity.getHidden())) {
            entity.setHidden(hidden);
            changed = true;
        }
        if (null!=time && time.getTime()!=entity.getTime().getTime()) {
            entity.setTime(time);
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
    public long addCasebook(int hospitalId, int departmentId, long nurseId, long userId, long patientId, String description, String name, YesNoEnum hidden, Date time) {
        logger.info("add casebook with nurseId={} userId={} patientId={} description={} name={}",
                nurseId, userId, patientId, description, (null!=name));

        boolean hospitalExisted = hospitalService.existHospital(hospitalId);
        boolean departmentExisted = departmentService.existsDepartment(departmentId);
        boolean nurseExisted = nurseService.existsNurse(nurseId);
        if (!nurseExisted && (!hospitalExisted || !departmentExisted)) {
            logger.info("hospital department nurse all not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        else if (!hospitalExisted || !departmentExisted) {
            NurseBean nurse = nurseService.getNurseById(nurseId);
            NurseHospitalRelationBean hospitalRelation = (NurseHospitalRelationBean) nurse.getProperty(NurseBean.HOSPITAL_DEPARTMENT);
            if (null==hospitalRelation) {
                logger.info("nurse do not set hospital department properties");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
            hospitalId = hospitalRelation.getHospitalId();
            departmentId = hospitalRelation.getDepartmentId();
            hospitalExisted = hospitalService.existHospital(hospitalId);
            departmentExisted = departmentService.existsDepartment(departmentId);
            if (!hospitalExisted || !departmentExisted) {
                logger.info("nurse setting hospital department not existed");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
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
        entity.setHospitalId(hospitalId);
        entity.setDepartmentId(departmentId);
        entity.setNurseId(nurseId<0 ? 0 : nurseId);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setDescription(description.trim());
        entity.setName(name);
        entity.setHidden(null==hidden ? YesNoEnum.NO : hidden);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(null==time ? new Date() : time);
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
        if (null==caseBean) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<CaseBean> caseBeans = Arrays.asList(new CaseBean[]{caseBean});
        fillOtherPropertiesForCase(caseBean.getCasebookId(), caseBeans);
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
            if (!nurseIds.contains(tmp.getNurseId())) {
                nurseIds.add(tmp.getNurseId());
            }
            if (!casebookIds.contains(tmp.getCasebookId())) {
                casebookIds.add(tmp.getCasebookId());
            }
        }

        Map<Long, NurseBean> userIdToBean = nurseService.getNurseIdToBean(nurseIds);
        Map<Long, Map<Long, String>> caseIdToMapOfImageIdToUrl = new HashMap<>();
        Map<Long, List<String>> caseIdToImagesUrl = imageService.getCaseIdToImagesUrl(casebookId, caseIdToMapOfImageIdToUrl);

        for (CaseBean tmp : beans) {
            NurseBean user = userIdToBean.get(tmp.getNurseId());
            tmp.setNurse(user);
            List<String> imagesUrl = caseIdToImagesUrl.get(tmp.getId());
            tmp.setImagesUrl(imagesUrl);
            Map<Long, String> imagesIdToUrl = caseIdToMapOfImageIdToUrl.get(tmp.getId());
            tmp.setImageIdToUrl(imagesIdToUrl);
        }
    }

    //=======================================
    //           deleting
    //=======================================
    @Transactional
    public List<Long> deleteCase(Long nurseId, long caseId) {
        CaseBean _case = getCaseById(caseId);
        CasebookBean casebook = getCasebook(_case.getCasebookId());
        if (null!=nurseId /* 不是管理员/护士长 */
                && nurseId!=_case.getNurseId()   /* 不是 case 的创建者 */
                && nurseId!=casebook.getNurseId()/* 不是 book 的创建者 */
        ) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_FORBIDDEN);
        }
        List<Long> deletedIds = caseService.deleteByIds(Arrays.asList(new Long[]{caseId}));
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
            logger.error("casebook is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && 0!=casebook.getNurseId() && nurseId!=casebook.getNurseId()) {
            logger.error("casebook not belong this nurse");
            throw new BadRequestException(ErrorCode.NURSE360_CASEBOOK_OR_CASE_NOT_BELONG_TO_YOU);
        }

        long caseId = caseService.addCase(casebookId, nurseId, caseRecord);
        return caseId;
    }

    //=======================================
    //           updating
    //=======================================
    @Transactional
    public long updateCase(Long nurseId, long caseId, String caseRecord) {
        logger.info("update case caseId={} caseRecord={}.", caseId, caseRecord);
        caseService.updateCase(nurseId, caseId, caseRecord);
        return caseId;
    }

    @Transactional
    public Map<String, String> addCaseImage(Long nurseId, long casebookId, long caseId, String imageName, InputStream image) {
        logger.info("nurseId={} add image to casebookId={} caseId={} name={} image={}", nurseId, casebookId, caseId, imageName, (null!=image));

        CasebookEntity casebook = repository.findOne(casebookId);
        if (null==casebook) {
            logger.error("casebook is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        CaseBean _case = caseService.getCaseWithoutInfoById(caseId);
        if (null==_case) {
            logger.error("case is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (_case.getCasebookId() != casebookId) {
            logger.error("case is not belong to casebook");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null!=nurseId && nurseId!=_case.getNurseId()) {
            logger.error("case is not belong to nurse");
            throw new BadRequestException(ErrorCode.NURSE360_CASEBOOK_OR_CASE_NOT_BELONG_TO_YOU);
        }

        long count = imageService.countImage(casebookId, caseId);
        if (count>=9) {
            logger.warn("the casebook do not need image more than nine");
            return new HashMap<>();
        }

        Map<String, String> idAndUrl = imageService.addImage(casebookId, caseId, imageName, image);
        return idAndUrl;
    }

    @Transactional
    public List<Long> deleteCaseImage(Long nurseId, long casebookId, long caseId) {
        CasebookEntity casebook = repository.findOne(casebookId);
        if (null==casebook) {
            logger.error("casebook is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        CaseBean _case = caseService.getCaseWithoutInfoById(caseId);
        if (null==_case) {
            logger.error("case is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (_case.getCasebookId() != casebookId) {
            logger.error("case is not belong to casebook");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null!=nurseId && nurseId!=_case.getNurseId()) {
            logger.error("case is not belong to nurse");
            throw new BadRequestException(ErrorCode.NURSE360_CASEBOOK_OR_CASE_NOT_BELONG_TO_YOU);
        }

        List<Long> imageIds = imageService.deleteByCaseIds(Arrays.asList(new Long[]{caseId}));
        return imageIds;
    }

    @Transactional
    public List<Long> deleteCaseImage(Long nurseId, long casebookId, long caseId, long imageId) {
        CasebookEntity casebook = repository.findOne(casebookId);
        if (null==casebook) {
            logger.error("casebook is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        CaseBean _case = caseService.getCaseWithoutInfoById(caseId);
        if (null==_case) {
            logger.error("case is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (_case.getCasebookId() != casebookId) {
            logger.error("case is not belong to casebook");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null!=nurseId && nurseId!=_case.getNurseId()) {
            logger.error("case is not belong to nurse");
            throw new BadRequestException(ErrorCode.NURSE360_CASEBOOK_OR_CASE_NOT_BELONG_TO_YOU);
        }
        if (!imageService.existImage(casebookId, caseId, imageId)) {
            logger.error("image is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<Long> imageIds = imageService.deleteByImageIds(Arrays.asList(new Long[]{imageId}));
        return imageIds;
    }
}
