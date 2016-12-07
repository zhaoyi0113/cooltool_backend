package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.repository.PatientRepository;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.converter.PatientBeanConverter;
import com.cooltoo.go2nurse.entities.PatientEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.repository.UserPatientRelationRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 2/29/16.
 */
@Service("PatientService")
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    public static final Sort userPatientRelationSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private PatientRepository repository;
    @Autowired private PatientBeanConverter beanConverter;
    @Autowired private UserPatientRelationRepository userPatientRelationRep;
    @Autowired private UserGo2NurseFileStorageService userStorage;
    @Autowired private Go2NurseUtility utility;

    public boolean existPatient(long patientId) {
        return repository.exists(patientId);
    }

    public long countAll(String name, int iGender, String mobile, String identityCard, String strStatus){
        logger.info("count all by name={} gender={} mobile={} identity={} status={}",
                name, iGender, mobile, identityCard, strStatus);

        name = VerifyUtil.isStringEmpty(name) ? null : VerifyUtil.reconstructSQLContentLike(name.trim());
        mobile = VerifyUtil.isStringEmpty(mobile) ? null : VerifyUtil.reconstructSQLContentLike(mobile.trim());
        identityCard = VerifyUtil.isStringEmpty(identityCard) ? null : VerifyUtil.reconstructSQLContentLike(identityCard.trim());
        CommonStatus status = CommonStatus.parseString(strStatus);
        GenderType gender = GenderType.parseInt(iGender);

        long count = repository.countByConditions(status, name, gender, identityCard, mobile);

        logger.info("count is {}", count);
        return count;
    }

    public List<PatientBean> getAll(String name, int iGender, String mobile, String identityCard, String strStatus, int pageIndex, int sizePerPage){
        logger.info("get all by name={} gender={} mobile={} identity={} status={}, at page={} size={}",
                name, iGender, mobile, identityCard, strStatus, pageIndex, sizePerPage);

        name = VerifyUtil.isStringEmpty(name) ? null : VerifyUtil.reconstructSQLContentLike(name.trim());
        mobile = VerifyUtil.isStringEmpty(mobile) ? null : VerifyUtil.reconstructSQLContentLike(mobile.trim());
        identityCard = VerifyUtil.isStringEmpty(identityCard) ? null : VerifyUtil.reconstructSQLContentLike(identityCard.trim());
        CommonStatus status = CommonStatus.parseString(strStatus);
        GenderType gender = GenderType.parseInt(iGender);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Iterable<PatientEntity> entities = repository.findByConditions(status, name, gender, identityCard, mobile, page);
        List<PatientBean> beans = entities2Beans(entities);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public PatientBean getOneById(long id){
        PatientEntity entity = repository.findOne(id);
        if(entity == null){
            return null;
        }
        PatientBean bean = beanConverter.convert(entity);
        List<PatientBean> beans = Arrays.asList(new PatientBean[]{bean});
        fillOtherProperties(beans);
        return bean;
    }

    public Map<Long, PatientBean> getAllIdToBeanByStatusAndIds(List<Long> ids, CommonStatus status) {
        List<PatientBean> beans = getAllByStatusAndIds(ids, status);
        Map<Long, PatientBean> idToBean = new HashMap<>();
        for (PatientBean bean : beans) {
            idToBean.put(bean.getId(), bean);
        }
        return idToBean;
    }

    public List<PatientBean> getAllByStatusAndIds(List<Long> ids, CommonStatus status) {
        List<PatientEntity> entities;
        if (null==status) {
            entities = repository.findByIdIn(ids, sort);
        }
        else {
            entities = repository.findByStatusAndIdIn(status, ids, sort);
        }
        List<PatientBean> beans = entities2Beans(entities);
        fillOtherProperties(beans);
        return beans;
    }

    public Map<Long, PatientBean> getPatientIdToBean(List<Long> patientIds) {
        logger.info("get patient by patientIds={}", patientIds);
        if (VerifyUtil.isListEmpty(patientIds)) {
            return new HashMap<>();
        }
        List<PatientEntity> resultSet = repository.findAll(patientIds);
        List<PatientBean> beans = entities2Beans(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        Map<Long, PatientBean> map = new HashMap<>();
        for (PatientBean tmp : beans) {
            map.put(tmp.getId(), tmp);
        }
        return map;
    }

    private List<PatientBean> entities2Beans(Iterable<PatientEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<PatientBean> beans = new ArrayList<>();
        for (PatientEntity tmp : entities) {
            PatientBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<PatientBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (PatientBean tmp : beans) {
            if (!imageIds.contains(tmp.getHeadImageId())) {
                imageIds.add(tmp.getHeadImageId());
            }
        }

        Map<Long, String> imageIdToUrl = userStorage.getFileUrl(imageIds, utility.getHttpPrefix());

        // fill properties
        for (PatientBean tmp : beans) {
            String imageUrl = imageIdToUrl.get(tmp.getHeadImageId());
            tmp.setHeadImageUrl(imageUrl);
        }
    }

    //====================================================================
    //                     add
    //====================================================================
    @Transactional
    public PatientBean create(String name, int iGender, Date birthday, String identityCard, String mobile, YesNoEnum isDefault, YesNoEnum isSelf) {
        logger.info("create patient with name={} gender={} birthday={} identityCard={} mobile={} isDefault={} isSelf={}",
                name, iGender, birthday, identityCard, mobile, isDefault, isSelf);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.info("name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();

        GenderType gender = GenderType.parseInt(iGender);
        if (null==gender) {
            logger.warn("gender is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (VerifyUtil.isStringEmpty(identityCard)) {
            logger.warn("identityCard is empty");
        }
        else {
            identityCard = identityCard.trim();
        }

        if (VerifyUtil.isStringEmpty(mobile)) {
            logger.warn("mobile is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        mobile = mobile.trim();

        PatientEntity entity = new PatientEntity();
        entity.setName(name);
        entity.setGender(gender);
        entity.setBirthday(birthday);
        entity.setIdentityCard(identityCard);
        entity.setMobile(mobile);
        entity.setIsDefault(isDefault==null ? YesNoEnum.NO : isDefault);
        entity.setIsSelf(isSelf==null ? YesNoEnum.NO : isSelf);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        entity = repository.save(entity);
        logger.info("add patient={}", entity);
        PatientBean bean = beanConverter.convert(entity);
        return bean;
    }

    //====================================================================
    //                     update
    //====================================================================
    @Transactional
    public PatientBean update(long userId, long patientId, String name, int iGender, Date birthday, String identityCard, String mobile, YesNoEnum isDefault, YesNoEnum isSelf, String strStatus) {
        logger.info("update patient={} by userId={} name={} gender={} birthday={} identityCard={} mobile={} isDefault={} isSelf={} status={}",
                patientId, userId, name, iGender, birthday, identityCard, mobile, isDefault, isSelf, strStatus);
        PatientEntity entity = repository.findOne(patientId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(name) && !name.trim().equals(entity.getName())) {
            entity.setName(name.trim());
            changed = true;
        }
        GenderType gender = GenderType.parseInt(iGender);
        if (null!=gender && !gender.equals(entity.getGender())) {
            entity.setGender(gender);
            changed = true;
        }
        if (null!=birthday && !birthday.equals(entity.getBirthday())) {
            entity.setBirthday(birthday);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(identityCard) && !identityCard.trim().equals(entity.getIdentityCard())) {
            entity.setIdentityCard(identityCard.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(mobile) && !mobile.trim().equals(entity.getMobile())) {
            entity.setMobile(mobile.trim());
            changed = true;
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }
        boolean isSetSelf = false;
        if (null!=isSelf && !isSelf.equals(entity.getIsSelf())) {
            entity.setIsSelf(isSelf);
            changed = true;
            isSetSelf = true;
        }
        boolean isSetDefault = false;
        if (null!=isDefault && !isDefault.equals(entity.getIsDefault())) {
            entity.setIsDefault(isDefault);
            changed = true;
            isSetDefault = true;
        }
        if (changed) {
            entity = repository.save(entity);
            if (isSetDefault) {
                setDefault(userId, entity.getId(), isDefault);
            }
            if (isSetSelf) {
                setSelf(userId, entity.getId(), isSelf);
            }
        }
        logger.info("patient is {}", entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    private void setDefault(long userId, long patientId, YesNoEnum isDefault) {
        logger.info("update user={} 's patient={} by isDefault={}", userId, patientId, isDefault);
        if (!YesNoEnum.YES.equals(isDefault)) {
            logger.info("not set the default patient");
            return;
        }
        List<Long> patientsId = userPatientRelationRep.findPatientIdByUserIdAndStatus(
                userId, null, userPatientRelationSort);
        List<PatientEntity> patients = repository.findAll(patientsId);

        for (PatientEntity patient : patients) {
            patient.setIsDefault(patient.getId()!=patientId ? YesNoEnum.NO : YesNoEnum.YES);
        }

        repository.save(patients);
    }

    @Transactional
    private void setSelf(long userId, long patientId, YesNoEnum isSelf) {
        logger.info("update user={} 's patient={} by isSelf={}", userId, patientId, isSelf);
        if (!YesNoEnum.YES.equals(isSelf)) {
            logger.info("not set the self patient");
            return;
        }
        List<Long> patientsId = userPatientRelationRep.findPatientIdByUserIdAndStatus(
                userId, null, userPatientRelationSort);
        List<PatientEntity> patients = repository.findAll(patientsId);

        for (PatientEntity patient : patients) {
            patient.setIsSelf(patient.getId()!=patientId ? YesNoEnum.NO : YesNoEnum.YES);
        }

        repository.save(patients);
    }

    @Transactional
    public PatientBean updateHeaderImage(long patientId, String headImageName, InputStream headImage) {
        logger.info("update header image for patientId={}, imageName={}, image={}", patientId, headImageName, headImage);
        PatientEntity entity = repository.findOne(patientId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (VerifyUtil.isStringEmpty(headImageName)) {
            headImageName = "patient_head_image_"+System.currentTimeMillis();
        }
        long imageId = 0;
        String imageUrl = "";
        imageId = userStorage.addFile(entity.getHeadImageId(), headImageName, headImage);
        if (imageId > 0) {
            entity.setHeadImageId(imageId);
            imageUrl = userStorage.getFileURL(imageId);
            entity = repository.save(entity);
        }
        PatientBean bean = beanConverter.convert(entity);
        bean.setHeadImageUrl(imageUrl);
        return bean;
    }
}
