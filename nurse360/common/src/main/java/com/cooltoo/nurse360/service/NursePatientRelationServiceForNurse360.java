package com.cooltoo.nurse360.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NursePatientRelationBean;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.converter.NursePatientRelationBeanConverter;
import com.cooltoo.go2nurse.entities.NursePatientRelationEntity;
import com.cooltoo.go2nurse.repository.NursePatientRelationRepository;
import com.cooltoo.go2nurse.service.PatientService;
import com.cooltoo.go2nurse.service.UserService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @Autowired private UserService userService;
    @Autowired private PatientService patientService;

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

    public List<NursePatientRelationBean> getRelationByNurseId(long nurseId, CommonStatus status, int pageIndex, int sizePerPage) {
        logger.info("get nurse patient by nursesId={} and status={}", nurseId, status);
        if (null==status) {
            logger.error("status is null");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NursePatientRelationEntity> entities = repository.findByNurseIdInAndStatus(Arrays.asList(new Long[]{nurseId}), status, page);
        List<NursePatientRelationBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);

        logger.info("get nurse patient by nursesId, count={}", beans.size());
        return beans;
    }

    private List<NursePatientRelationBean> entitiesToBeans(Iterable<NursePatientRelationEntity> entities) {
        List<NursePatientRelationBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (NursePatientRelationEntity tmp : entities) {
            NursePatientRelationBean tmpBean = beanConverter.convert(tmp);
            beans.add(tmpBean);
        }
        return beans;
    }

    private void fillOtherProperties(List<NursePatientRelationBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }
        List<Long> userIds = new ArrayList<>();
        List<Long> patientIds = new ArrayList<>();
        for (NursePatientRelationBean bean :beans) {
            long userId = bean.getUserId();
            if (!patientIds.contains(userId)) {
                patientIds.add(userId);
            }
            long patientId = bean.getPatientId();
            if (!userIds.contains(patientId)) {
                userIds.add(patientId);
            }
        }

        Map<Long, UserBean> userIdToBean = userService.getUserIdToBean(userIds);
        Map<Long, PatientBean> patientIdToBean = patientService.getPatientIdToBean(patientIds);
        for (NursePatientRelationBean bean : beans) {
            long patientId = bean.getPatientId();
            PatientBean patientBean = patientIdToBean.get(patientId);
            if (null!=patientBean) {
                bean.setPatient(patientBean);
            }

            long userId = bean.getUserId();
            UserBean userBean = userIdToBean.get(userId);
            if (null!=userBean) {
                bean.setUser(userBean);
            }
        }
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
