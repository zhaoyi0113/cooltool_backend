package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.go2nurse.entities.UserPatientRelationEntity;
import com.cooltoo.go2nurse.repository.PatientRepository;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.converter.PatientBeanConverter;
import com.cooltoo.go2nurse.entities.PatientEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.repository.UserPatientRelationRepository;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
        List<PatientBean> beans = new ArrayList<PatientBean>();
        for(PatientEntity entity: entities){
            PatientBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        logger.info("count is {}", beans.size());
        return beans;
    }

    public PatientBean getOneById(long id){
        PatientEntity entity = repository.findOne(id);
        if(entity == null){
            return null;
        }
        return beanConverter.convert(entity);
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
        List<PatientBean> beans = new ArrayList<>();
        List<PatientEntity> entities;
        if (null==status) {
            entities = repository.findByIdIn(ids, sort);
        }
        else {
            entities = repository.findByStatusAndIdIn(status, ids, sort);
        }
        if (VerifyUtil.isListEmpty(entities)) {
            return beans;
        }
        for (PatientEntity entity :entities) {
            PatientBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    public Map<Long, PatientBean> getPatientIdToBean(List<Long> patientIds) {
        logger.info("get patient by patientIds={}", patientIds);
        if (VerifyUtil.isListEmpty(patientIds)) {
            return new HashMap<>();
        }
        List<PatientEntity> resultSet = repository.findAll(patientIds);
        List<PatientBean> beans = entities2Beans(resultSet);
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

    //====================================================================
    //                     add
    //====================================================================
    @Transactional
    public PatientBean create(String name, int iGender, Date birthday, String identityCard, String mobile, YesNoEnum isDefault) {
        logger.info("create patient with name={} gender={} birthday={} identityCard={} mobile={} isDefault={}",
                name, iGender, birthday, identityCard, mobile, isDefault);
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
    public PatientBean update(long userId, long patientId, String name, int iGender, Date birthday, String identityCard, String mobile, YesNoEnum isDefault, String strStatus) {
        logger.info("update patient={} by userId={} name={} gender={} birthday={} identityCard={} mobile={} isDefault={} status={}",
                patientId, userId, name, iGender, birthday, identityCard, mobile, isDefault, strStatus);
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
                userId, CommonStatus.ENABLED, userPatientRelationSort);
        List<PatientEntity> patients = repository.findAll(patientsId);

        for (PatientEntity patient : patients) {
            patient.setIsDefault(patient.getId()!=patientId ? YesNoEnum.NO : YesNoEnum.YES);
        }

        repository.save(patients);
    }
}
