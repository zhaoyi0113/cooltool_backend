package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.go2nurse.repository.PatientRepository;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.converter.PatientBeanConverter;
import com.cooltoo.go2nurse.entities.PatientEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 2/29/16.
 */
@Service("PatientService")
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private PatientRepository repository;
    @Autowired private PatientBeanConverter beanConverter;

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

    //====================================================================
    //                     add
    //====================================================================
    @Transactional
    public PatientBean create(String name, int iGender, Date birthday, String identityCard, String mobile) {
        logger.info("create patient with name={} gender={} birthday={} identityCard={} mobile={}",
                name, iGender, birthday, identityCard, mobile);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.info("name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();

        GenderType gender = GenderType.parseInt(iGender);
        if (null==gender) {
            logger.info("gender is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (VerifyUtil.isStringEmpty(identityCard)) {
            logger.info("identityCard is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        identityCard = identityCard.trim();

        if (VerifyUtil.isStringEmpty(mobile)) {
            logger.info("mobile is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        mobile = mobile.trim();

        PatientEntity entity = new PatientEntity();
        entity.setName(name);
        entity.setGender(gender);
        entity.setBirthday(birthday);
        entity.setIdentityCard(identityCard);
        entity.setMobile(mobile);
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
    public PatientBean update(long id, String name, int iGender, Date birthday, String identityCard, String mobile, String strStatus) {
        logger.info("update patient={} by name={} gender={} birthday={} identityCard={} mobile={} status={}",
                id, name, iGender, birthday, identityCard, mobile, strStatus);
        PatientEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
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
        if (changed) {
            entity = repository.save(entity);
        }
        logger.info("patient is {}", entity);
        return beanConverter.convert(entity);
    }
}
