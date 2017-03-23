package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NursePatientFollowUpBean;
import com.cooltoo.go2nurse.beans.PatientBean;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.converter.NursePatientFollowUpBeanConverter;
import com.cooltoo.go2nurse.entities.NursePatientFollowUpEntity;
import com.cooltoo.go2nurse.repository.NursePatientFollowUpRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/7.
 */
@Service("NursePatientFollowUpService")
public class NursePatientFollowUpService {

    private static final Logger logger = LoggerFactory.getLogger(NursePatientFollowUpService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );


    @Autowired private NursePatientFollowUpRepository repository;
    @Autowired private NursePatientFollowUpBeanConverter beanConverter;

    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    @Autowired private UserPatientRelationService userPatientRelation;
    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private Go2NurseUtility utility;

    //=================================================================
    //                 getter for administrator
    //=================================================================
    public long countPatientFollowUp(Integer hospitalId, Integer departmentId, Long nurseId, Long userId, Long patientId, List<CommonStatus> statuses) {
        long count = 0;
        if (!VerifyUtil.isListEmpty(statuses)) {
            count = repository.countByConditions(hospitalId, departmentId, nurseId, userId, patientId, statuses);
        }
        logger.info("count patient follow-up by hospital={} department={} nurseId={} user={} patientId={}, count is {}",
                hospitalId, departmentId, nurseId, userId, patientId, count);
        return count;
    }

    public List<NursePatientFollowUpBean> getPatientFollowUp(Integer hospitalId, Integer departmentId,
                                                             Long nurseId,
                                                             Long userId, Long patientId,
                                                             int pageIndex, int sizePerPage,
                                                             List<CommonStatus> statuses)
    {
        logger.info("get patient follow-up by hospital={} department={} nurseId={} user={} patientId={} at page={} sizePerPage={}",
                hospitalId, departmentId, nurseId, userId, patientId, pageIndex, sizePerPage);
        List<NursePatientFollowUpBean> beans = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(statuses)) {
            PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
            Page<NursePatientFollowUpEntity> resultSet = repository.findByConditions(hospitalId, departmentId, nurseId, userId, patientId, statuses, request);
            beans = entitiesToBeans(resultSet);
            fillOtherProperties(beans);
        }

        logger.warn("patient follow-up count={}", beans.size());
        return beans;
    }

    public List<Long> getPatientFollowUpIds(Integer hospitalId, Integer departId) {
        logger.info("get patient follow-up ids by hospitalId={} departId={}", hospitalId, departId);
        List<Long> followUpIds = new ArrayList<>();
        if (null==hospitalId && null==departId) {
        }
        else {
            List<NursePatientFollowUpEntity> resultSet = repository.findByConditionsAndStatusNot(hospitalId, departId, null, null, null, CommonStatus.DELETED, sort);
            for (NursePatientFollowUpEntity tmp : resultSet) {
                followUpIds.add(tmp.getId());
            }
        }
        logger.warn("patient follow-up count={}", followUpIds.size());
        return followUpIds;
    }


    //==================================================================
    //                   getter for nurse/patient
    //==================================================================
    public List<NursePatientFollowUpBean> getPatientFollowUp(Long userId, Long patientId, Long nurseId) {
        logger.info("get patient follow-up by nurseId={} user={} patientId={}",
                nurseId, userId, patientId);
        List<NursePatientFollowUpBean> beans;
        if (null==nurseId && null==userId) {
            beans = new ArrayList<>();
        }
        else {
            List<NursePatientFollowUpEntity> resultSet = repository.findByConditionsAndStatusNot(null, null, nurseId, userId, patientId, CommonStatus.DELETED, sort);
            beans = entitiesToBeans(resultSet);
            fillOtherProperties(beans);
        }
        logger.warn("patient follow-up count={}", beans.size());
        return beans;
    }

    public List<Long> getPatientFollowUpIds(Long userId, Long patientId, Long nurseId) {
        logger.info("get patient follow-up ids by nurseId={} user={} patientId={}",
                nurseId, userId, patientId);
        List<Long> followUpIds = new ArrayList<>();
        if (null==nurseId && null==userId) {
        }
        else {
            List<NursePatientFollowUpEntity> resultSet = repository.findByConditionsAndStatusNot(null, null, nurseId, userId, patientId, CommonStatus.DELETED, sort);
            for (NursePatientFollowUpEntity tmp : resultSet) {
                followUpIds.add(tmp.getId());
            }
        }
        logger.warn("patient follow-up count={}", followUpIds.size());
        return followUpIds;
    }

    public List<NursePatientFollowUpBean> getPatientFollowUp(Long userId, Long patientId, Long nurseId, int pageIndex, int sizePerPage) {
        logger.info("get patient follow-up by nurseId={} user={} patientId={} at page={} sizePerPage={}",
                nurseId, userId, patientId, pageIndex, sizePerPage);
        List<NursePatientFollowUpBean> beans;
        if (null==nurseId && null==userId) {
            beans = new ArrayList<>();
        }
        else {
            PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
            Page<NursePatientFollowUpEntity> resultSet = repository.findByConditionsAndStatusNot(null, null, nurseId, userId, patientId, CommonStatus.DELETED, request);
            beans = entitiesToBeans(resultSet);
            fillOtherProperties(beans);
        }
        logger.warn("patient follow-up count={}", beans.size());
        return beans;
    }

    public NursePatientFollowUpBean getPatientFollowUpWithoutInfo(long followUpId) {
        logger.info("get patient follow-up by followUpId={}", followUpId);
        NursePatientFollowUpEntity one = repository.findOne(followUpId);
        if (null==one) {
            return null;
        }
        return beanConverter.convert(one);
    }

    public NursePatientFollowUpBean getPatientFollowUp(long followUpId) {
        logger.info("get patientFollowUpId={}", followUpId);
        NursePatientFollowUpEntity resultSet = repository.findOne(followUpId);
        if (null==resultSet) {
            logger.info("there is no record");
            return null;
        }
        List<NursePatientFollowUpEntity> entities = new ArrayList<>();
        entities.add(resultSet);

        List<NursePatientFollowUpBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);

        NursePatientFollowUpBean bean = beans.get(0);
        bean.setNurse(nurseService.getNurseById(bean.getNurseId()));
        bean.setHospital(hospitalService.getHospital(bean.getHospitalId()));
        bean.setDepartment(departmentService.getById(bean.getDepartmentId(), utility.getHttpPrefixForNurseGo()));
        return beans.get(0);
    }

    public boolean existsPatientFollowUp(long followUpId) {
        return repository.exists(followUpId);
    }

    private List<NursePatientFollowUpBean> entitiesToBeans(Iterable<NursePatientFollowUpEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<NursePatientFollowUpBean> beans = new ArrayList<>();
        for(NursePatientFollowUpEntity tmp : entities) {
            NursePatientFollowUpBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<NursePatientFollowUpBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        List<Long> patientIds = new ArrayList<>();
        List<Long> nurseIds = new ArrayList<>();
        List<Integer> hospitalIds = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (NursePatientFollowUpBean tmp : beans) {
            if (!userIds.contains(tmp.getUserId())) {
                userIds.add(tmp.getUserId());
            }
            if (!patientIds.contains(tmp.getPatientId())) {
                patientIds.add(tmp.getPatientId());
            }
            if (!nurseIds.contains(tmp.getNurseId())) {
                nurseIds.add(tmp.getNurseId());
            }
            if (!hospitalIds.contains(tmp.getHospitalId())) {
                hospitalIds.add(tmp.getHospitalId());
            }
            if (!departmentIds.contains(tmp.getDepartmentId())) {
                departmentIds.add(tmp.getDepartmentId());
            }
        }

        Map<Long, UserBean> userIdToBean = userService.getUserIdToBean(userIds);
        Map<Long, PatientBean> patientIdToBean = patientService.getPatientIdToBean(patientIds);
        Map<Long, NurseBean> nurseIdToBean = nurseService.getNurseIdToBean(nurseIds);
        Map<Integer, HospitalBean> hospitalIdToBean = hospitalService.getHospitalIdToBeanMapByIds(hospitalIds);
        Map<Integer, HospitalDepartmentBean> departmentIdToBean = departmentService.getDepartmentIdToBean(departmentIds, utility.getHttpPrefixForNurseGo());

        // fill properties
        for (NursePatientFollowUpBean tmp : beans) {
            UserBean user = userIdToBean.get(tmp.getUserId());
            tmp.setUser(user);
            PatientBean patient = patientIdToBean.get(tmp.getPatientId());
            tmp.setPatient(patient);
            NurseBean nurse = nurseIdToBean.get(tmp.getNurseId());
            tmp.setNurse(nurse);
            HospitalBean hospital = hospitalIdToBean.get(tmp.getHospitalId());
            tmp.setHospital(hospital);
            HospitalDepartmentBean department = departmentIdToBean.get(tmp.getDepartmentId());
            tmp.setDepartment(department);
        }
    }


    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public List<Long> setDeleteStatusPatientFollowUpByIds(Long nurseId, List<Long> patientFollowUpId) {
        logger.info("delete patient follow-up by patientFollowUpId={}.", patientFollowUpId);
        List<Long> retValue = new ArrayList<>();
        if (VerifyUtil.isListEmpty(patientFollowUpId)) {
            return retValue;
        }

        List<NursePatientFollowUpEntity> patientFollowUp = repository.findAll(patientFollowUpId);
        if (VerifyUtil.isListEmpty(patientFollowUp)) {
            logger.info("delete nothing");
            return retValue;
        }

        if (null!=nurseId) {
            for (NursePatientFollowUpEntity tmp : patientFollowUp) {
                if (tmp.getNurseId() == nurseId) {
                    continue;
                }
                logger.warn("can not patient follow-up that not making by yourself={}", tmp);
                return retValue;
            }
        }

        for (NursePatientFollowUpEntity tmp : patientFollowUp) {
            tmp.setStatus(CommonStatus.DELETED);
            retValue.add(tmp.getId());
        }
        repository.save(patientFollowUp);


        return retValue;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addPatientFollowUp(int hospitalId, int departmentId, long nurseId, long userId, long patientId) {
        logger.info("add patient follow-up with hospitalId={} departmentId={} nurseId={} userId={} patientId={}",
                hospitalId, departmentId, nurseId, userId, patientId);
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
        if (patientId>0 && !userPatientRelation.existRelation(userId, patientId)) {
            logger.info("userId -- patientId do not has relation");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        hospitalId   = hospitalId<0 ? 0 : hospitalId;
        departmentId = departmentId<0 ? 0 : departmentId;

        NursePatientFollowUpEntity entity;
        List<NursePatientFollowUpEntity> set = repository.findByConditions(null, null, nurseId, userId, patientId, sort);
        if (VerifyUtil.isListEmpty(set)) {
            entity = new NursePatientFollowUpEntity();
        }
        else {
            entity = set.get(0);
        }

        entity.setHospitalId(hospitalId);
        entity.setDepartmentId(departmentId);
        entity.setNurseId(nurseId);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        set = repository.findByConditions(null, null, nurseId, userId, patientId, sort);
        for (int i = 0; i < set.size(); i ++) {
            NursePatientFollowUpEntity tmp = set.get(i);
            if (tmp.getId()==entity.getId()) {
                set.remove(i);
                break;
            }
        }
        if (!VerifyUtil.isListEmpty(set)) {
            repository.delete(set);
        }

        return entity.getId();
    }

}
