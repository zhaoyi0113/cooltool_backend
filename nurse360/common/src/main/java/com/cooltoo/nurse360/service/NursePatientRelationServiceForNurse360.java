package com.cooltoo.nurse360.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NursePatientRelationBean;
import com.cooltoo.go2nurse.converter.NursePatientRelationBeanConverter;
import com.cooltoo.go2nurse.entities.NursePatientRelationEntity;
import com.cooltoo.go2nurse.repository.NursePatientRelationRepository;
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

/**
 * Created by hp on 2016/6/14.
 */
@Service("NursePatientRelationServiceForNurse360")
public class NursePatientRelationServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NursePatientRelationServiceForNurse360.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private NursePatientRelationRepository repository;
    @Autowired private NursePatientRelationBeanConverter beanConverter;

    //============================================================================
    //                 get
    //============================================================================
    public List<Long> getUserByNurseId(long nurseId, String strStatus) {
        logger.info("get patients_user by nurseId={} with status={}", nurseId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<NursePatientRelationEntity> resultSet = repository.findByNurseIdAndStatus(nurseId, status, sort);
        List<Long> userIds = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(resultSet)) {
            for (NursePatientRelationEntity tmp : resultSet) {
                if (userIds.contains(tmp.getUserId())) {
                    continue;
                }
                userIds.add(tmp.getUserId());
            }
        }
        logger.info("count is {}", userIds.size());
        return userIds;
    }


    //============================================================================
    //                 update
    //============================================================================
    @Transactional
    public NursePatientRelationBean updateStatus(long nurseId, long userId, long patientId, String strStatus) {
        logger.info("update relation status to={} between nurse={} user={} and patient={}",
                strStatus, nurseId, userId, patientId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<NursePatientRelationEntity> relations = repository.findByNurseIdAndUserIdAndPatientId(nurseId, userId, patientId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        NursePatientRelationEntity entity = relations.get(0);
        relations.remove(entity);

        entity.setStatus(status);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        NursePatientRelationBean bean = beanConverter.convert(entity);
        logger.info("update relation={}", bean);
        return bean;
    }

    //============================================================================
    //                 add
    //============================================================================
    @Transactional
    public NursePatientRelationBean addUserPatientToNurse(long nurseId, long patientId, long userId) {
        logger.info("add patient={} user={} to nurse={}", patientId, userId, nurseId);
        NursePatientRelationEntity entity = null;
        List<NursePatientRelationEntity> relations = repository.findByNurseIdAndUserIdAndPatientId(nurseId, userId, patientId, sort);
        if (!VerifyUtil.isListEmpty(relations)) {
            entity = relations.get(0);
            relations.remove(entity);
        }
        else {
            entity = new NursePatientRelationEntity();
            entity.setNurseId(nurseId);
            entity.setUserId(userId);
            entity.setPatientId(patientId);
            entity.setTime(new Date());
        }
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        NursePatientRelationBean bean = beanConverter.convert(entity);
        logger.info("add relation={}", bean);
        return bean;
    }

}
