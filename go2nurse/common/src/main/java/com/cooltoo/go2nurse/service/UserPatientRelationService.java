package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.UserPatientRelationBean;
import com.cooltoo.go2nurse.converter.UserPatientRelationBeanConverter;
import com.cooltoo.go2nurse.entities.UserPatientRelationEntity;
import com.cooltoo.go2nurse.repository.UserPatientRelationRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/14.
 */
@Service("UserPatientRelationService")
public class UserPatientRelationService {

    private static final Logger logger = LoggerFactory.getLogger(UserPatientRelationService.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private UserPatientRelationRepository repository;
    @Autowired private UserPatientRelationBeanConverter beanConverter;
    @Autowired private UserService userService;
    @Autowired private PatientService patientService;

    //============================================================================
    //                 get
    //============================================================================
    public List<Long> getPatientByUser(long lUserId, String strStatus) {
        logger.info("get patients by userId={} with status={}", lUserId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> patientIds = new ArrayList<>();
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                patientIds = repository.findPatientIdByUserIdAndStatus(lUserId, status, sort);
            }
        }
        else {
            patientIds = repository.findPatientIdByUserIdAndStatus(lUserId, status, sort);
        }
        logger.info("count is {}", patientIds.size());
        return patientIds;
    }

    public List<Long> getUserIdByPatient(List<Long> patientIds, String strStatus) {
        logger.info("get user by patientIds={} with status={}", patientIds, strStatus);
        if (VerifyUtil.isListEmpty(patientIds)) {
            return new ArrayList<>();
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> userIds = new ArrayList<>();
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                userIds = repository.findUserIdByPatientIdAndStatus(patientIds, status, sort);
            }
        }
        else {
            userIds = repository.findUserIdByPatientIdAndStatus(patientIds, status, sort);
        }
        logger.info("count is {}", userIds.size());
        return userIds;
    }

    public List<UserPatientRelationBean> getRelationByUserIdAndStatus(long lUserId, String strStatus) {
        logger.info("get patients by userId={} with status={}", lUserId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<UserPatientRelationEntity> relations = new ArrayList<>();
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                relations = repository.findByUserIdAndStatus(lUserId, status, sort);
            }
        }
        else {
            relations = repository.findByUserIdAndStatus(lUserId, status, sort);
        }
        List<UserPatientRelationBean> beans = entitiesToBeans(relations);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<UserPatientRelationBean> getRelationByPatientIdsAndStatus(List<Long> patientIds, String strStatus) {
        logger.info("get relation by patientIds={} with status={}", patientIds, strStatus);
        if (VerifyUtil.isListEmpty(patientIds)) {
            return new ArrayList<>();
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<UserPatientRelationEntity> relations = new ArrayList<>();
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                relations = repository.findByPatientIdInAndStatus(patientIds, status, sort);
            }
        }
        else {
            relations = repository.findByPatientIdInAndStatus(patientIds, status, sort);
        }

        List<UserPatientRelationBean> beans = entitiesToBeans(relations);
        logger.info("count is {}", beans.size());
        return beans;

    }

    private List<UserPatientRelationBean> entitiesToBeans(Iterable<UserPatientRelationEntity> entities) {
        List<UserPatientRelationBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (UserPatientRelationEntity entity : entities) {
            UserPatientRelationBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }


    private void fillOtherProperties(List<UserPatientRelationBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        List<Long> patientIds = new ArrayList<>();
        for (UserPatientRelationBean tmp : beans) {
            if (!userIds.contains(tmp.getUserId())) {
                userIds.add(tmp.getUserId());
            }
            if (!patientIds.contains(tmp.getPatientId())) {
                patientIds.add(tmp.getPatientId());
            }
        }

        Map<Long, UserBean> userIdToBean = userService.getUserIdToBean(userIds);
        Map<Long, PatientBean> patientIdToBean = patientService.getPatientIdToBean(patientIds);

        // fill properties
        for (UserPatientRelationBean tmp : beans) {
            UserBean user = userIdToBean.get(tmp.getUserId());
            tmp.setUser(user);
            PatientBean patient = patientIdToBean.get(tmp.getPatientId());
            tmp.setPatient(patient);
        }
    }

    //============================================================================
    //                 update
    //============================================================================
    @Transactional
    public UserPatientRelationBean updateStatus(long userId, long patientId, String strStatus) {
        logger.info("update relation status to={} between patient={} and user={}",
                strStatus, userId, patientId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<UserPatientRelationEntity> relations = repository.findByPatientIdAndUserId(patientId, userId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        UserPatientRelationEntity entity = relations.get(0);
        relations.remove(entity);

        entity.setStatus(status);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        UserPatientRelationBean bean = beanConverter.convert(entity);
        logger.info("update relation={}", bean);
        return bean;
    }

    //============================================================================
    //                 add
    //============================================================================
    @Transactional
    public UserPatientRelationBean addPatientToUser(long lPatientId, long lUserId) {
        logger.info("add patient={} to user={}", lPatientId, lUserId);
        UserPatientRelationEntity entity = null;
        List<UserPatientRelationEntity> relations = repository.findByPatientIdAndUserId(lPatientId, lUserId, sort);
        if (!VerifyUtil.isListEmpty(relations)) {
            entity = relations.get(0);
            relations.remove(entity);
        }
        else {
            entity = new UserPatientRelationEntity();
            entity.setUserId(lUserId);
            entity.setPatientId(lPatientId);
            entity.setTime(new Date());
        }
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        UserPatientRelationBean bean = beanConverter.convert(entity);
        logger.info("add relation={}", bean);
        return bean;
    }

}
