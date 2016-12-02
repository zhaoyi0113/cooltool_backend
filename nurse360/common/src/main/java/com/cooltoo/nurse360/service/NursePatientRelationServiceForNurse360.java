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

import java.util.*;

/**
 * Created by zhaolisong on 16/9/28.
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

    public Map<Long, Long> getNursePatientNumber(List<Long> nursesId, CommonStatus status) {
        int size = null==nursesId ? 0 : nursesId.size();
        Map<Long, Long> nurseIdToPatientNumber = new HashMap<>();
        logger.info("get nurse patient number by nursesId size={} and status={}", size, status);

        if (size>0 && null!=status) {
            List<String> nurseUserPatient = new ArrayList<>();
            List<NursePatientRelationEntity> resultSet = repository.findByNurseIdInAndStatus(nursesId, status);
            if (!VerifyUtil.isListEmpty(resultSet)) {
                for (NursePatientRelationEntity tmp : resultSet) {
                    String key = tmp.getNurseId()+"_"+tmp.getUserId()+"_"+tmp.getPatientId();
                    if (nurseUserPatient.contains(key)) {
                        continue;
                    }
                    nurseUserPatient.add(key);
                    Long count = nurseIdToPatientNumber.get(tmp.getNurseId());
                    count = null==count ? 1L : (count+1);
                    nurseIdToPatientNumber.put(tmp.getNurseId(), count);
                }
            }
            logger.info("count is {}", nurseIdToPatientNumber.size());
        }

        return nurseIdToPatientNumber;
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
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }
        List<NursePatientRelationEntity> relations = repository.findByNurseIdAndUserIdAndPatientId(nurseId, userId, patientId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
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
