package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.converter.UserHospitalizedRelationBeanConverter;
import com.cooltoo.go2nurse.entities.UserHospitalizedRelationEntity;
import com.cooltoo.go2nurse.repository.UserHospitalizedRelationRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
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

/**
 * Created by hp on 2016/6/15.
 */
@Service("UserHospitalizedRelationService")
public class UserHospitalizedRelationService {

    private static final Logger logger = LoggerFactory.getLogger(UserHospitalizedRelationService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "groupId"),
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private UserHospitalizedRelationRepository repository;
    @Autowired private UserHospitalizedRelationBeanConverter beanConverter;

    @Autowired private UserDiagnosticPointRelationService userDiagnosticRelationService;
    @Autowired private UserRepository userRepository;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private HospitalDepartmentRepository departmentRepository;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private Go2NurseUtility utility;


    //===================================================
    //               getting for user
    //===================================================
    public List<UserHospitalizedRelationBean> getUserHospitalizedRelationByGroupId(long userId, long currentGroupId) {
        logger.info("get user={}'s current hospitalized hospital and department by current groupId={}", userId, currentGroupId);
        List<UserHospitalizedRelationEntity> entities = repository.findByUserIdAndGroupIdAndStatus(userId, currentGroupId, CommonStatus.ENABLED, sort);
        List<UserHospitalizedRelationBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("user current hospitalized relation count is {}", beans.size());
        return beans;
    }

    public List<UserHospitalizedRelationBean> getUserAllHospitalizedRelation(long userId) {
        logger.info("get user={}'s all hospitalized hospital and department", userId);
        List<UserHospitalizedRelationEntity> entities = repository.findByUserIdAndStatus(userId, CommonStatus.ENABLED, sort);
        List<UserHospitalizedRelationBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("user all hospitalized relation count is {}", beans.size());
        return beans;
    }

    //===================================================
    //               getting for admin user
    //===================================================
    public long countByUserAndStatus(long userId, String strStatus) {
        logger.info("count the hospitalized with user={} status={}",
                userId, strStatus);

        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countByUserIdAndStatus(userId, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<UserHospitalizedRelationBean> getRelation(long userId, String strStatus) {
        logger.info("get the hospitalized with user={} status={}",
                userId, strStatus);

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<UserHospitalizedRelationEntity> resultSet = repository.findByUserIdAndStatus(userId, status, sort);
        List<UserHospitalizedRelationBean> relations = entitiesToBeans(resultSet);
        fillOtherProperties(relations);
        logger.info("count is {}", relations.size());
        return relations;
    }

    public List<UserHospitalizedRelationBean> getRelation(long userId, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get the hospitalized with user={} status={} at page={} sizePerPage={}",
                userId, strStatus, pageIndex, sizePerPage);

        CommonStatus status = CommonStatus.parseString(strStatus);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<UserHospitalizedRelationEntity> resultSet = repository.findByUserIdAndStatus(userId, status, page);
        List<UserHospitalizedRelationBean> relations = entitiesToBeans(resultSet);
        fillOtherProperties(relations);
        logger.info("count is {}", relations.size());
        return relations;
    }

    private List<UserHospitalizedRelationBean> entitiesToBeans(Iterable<UserHospitalizedRelationEntity> entities) {
        List<UserHospitalizedRelationBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        UserHospitalizedRelationBean bean;
        for (UserHospitalizedRelationEntity entity : entities) {
            bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<UserHospitalizedRelationBean> beans) {
        if(VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Integer> hospitalIds = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (UserHospitalizedRelationBean bean : beans) {
            if (!hospitalIds.contains(bean.getHospitalId())) {
                hospitalIds.add(bean.getHospitalId());
            }
            if (!departmentIds.contains(bean.getDepartmentId())) {
                departmentIds.add(bean.getDepartmentId());
            }
        }
        List<HospitalBean> hospitals = hospitalService.getHospitalByIds(hospitalIds);
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, utility.getHttpPrefixForNurseGo());
        for (UserHospitalizedRelationBean bean : beans) {
            int hospitalId = bean.getHospitalId();
            for (HospitalBean hospital : hospitals) {
                if (hospitalId<0) {
                    break;
                }
                if(hospital.getId()==hospitalId) {
                    bean.setHospital(hospital);
                    break;
                }
            }
            int departmentId = bean.getDepartmentId();
            for (HospitalDepartmentBean department : departments) {
                if (departmentId<0) {
                    break;
                }
                if(department.getId()==departmentId) {
                    bean.setDepartment(department);
                    break;
                }
            }
        }
    }

    //===================================================
    //               adding for user
    //===================================================
    @Transactional
    public UserHospitalizedRelationBean addRelation(long userId, String hospitalUniqueId, String departmentUniqueId) {
        logger.info("user={} add hospitalized relation with hospitalUniqueId={} and departmentUniqueId={}",
                userId, hospitalUniqueId, departmentUniqueId);
        long groupId = userDiagnosticRelationService.getUserCurrentGroupId(userId);
        return addRelation(userId, groupId, hospitalUniqueId, departmentUniqueId);
    }

    //===================================================
    //               adding for admin user
    //===================================================

    @Transactional
    public UserHospitalizedRelationBean addRelation(long userId, long groupId, String hospitalUniqueId, String departmentUniqueId) {
        logger.info("add hospitalized relation to user={} with groupId={} hospitalUniqueId={} departmentUniqueId={}",
                userId, groupId, hospitalUniqueId, departmentUniqueId);

        List<HospitalEntity> hospital = hospitalRepository.findByUniqueId(hospitalUniqueId);
        int hospitalSize = VerifyUtil.isListEmpty(hospital) ? 0 : hospital.size();
        if (hospitalSize!=1) {
            logger.error("hospital size is not 1, size is {}", hospitalSize);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        int hospitalId = hospital.get(0).getId();
        logger.info("hospitalId={}", hospitalId);

        List<HospitalDepartmentEntity> department = departmentRepository.findByUniqueId(departmentUniqueId);
        int departmentSize = VerifyUtil.isListEmpty(department) ? 0 : department.size();
        if (departmentSize!=1) {
            logger.error("department size is not 1, size is {}", departmentSize);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        int departmentId = department.get(0).getId();
        logger.info("departmentId={}", departmentId);

        return addRelation(userId, groupId, hospitalId, departmentId);
    }

    @Transactional
    public UserHospitalizedRelationBean addRelation(long userId, long groupId, int hospitalId, int departmentId) {
        logger.info("add hospitalized relation to user={} with groupId={} hospitalId={} departmentId={}",
                userId, groupId, hospitalId, departmentId);
        if (!userRepository.exists(userId)) {
            logger.error("user not exist");
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        if (!hospitalRepository.exists(hospitalId)) {
            logger.error("hospital id={} not exists", hospitalId);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!departmentRepository.exists(departmentId)) {
            logger.error("department id={} not exists", departmentId);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        UserHospitalizedRelationEntity entity;
        entity = new UserHospitalizedRelationEntity();
        entity.setUserId(userId);
        entity.setGroupId(groupId);
        entity.setHospitalId(hospitalId);
        entity.setDepartmentId(departmentId);
        entity.setHasLeave(YesNoEnum.NO);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);
        long relationId = entity.getId();

        List<UserHospitalizedRelationEntity> entities;
        entities = repository.findByUserIdAndHospitalIdAndDepartmentIdAndGroupId(userId, hospitalId, departmentId, groupId, sort);
        boolean changed = false;
        for (int i = 0, count = entities.size(); i < count; i ++) {
            UserHospitalizedRelationEntity tmp = entities.get(i);
            if (tmp.getId()==relationId) {
                entities.remove(i);
                i --;
                count --;
                continue;
            }
            tmp.setStatus(CommonStatus.DISABLED);
            changed = true;
        }
        if (changed) {
            repository.save(entities);
        }

        UserHospitalizedRelationBean relation = beanConverter.convert(entity);
        logger.info("add relations is {}", relation);
        return relation;
    }

    //===================================================
    //               update
    //===================================================
    @Transactional
    public UserHospitalizedRelationBean updateRelation(long relationId, boolean checkUser, long userId, String strHasLeave, String strStatus) {
        logger.info("user={} update relation={} with readingStatus={} and hasLeave={} status={}",
                userId, relationId, strHasLeave, strStatus);
        UserHospitalizedRelationEntity entity = repository.findOne(relationId);
        if (null==entity) {
            logger.error("relation not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser && entity.getUserId()!=userId) {
            logger.info("not user's relation");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        YesNoEnum yesOrNo = YesNoEnum.parseString(strHasLeave);
        if (null!=yesOrNo && !yesOrNo.equals(entity.getHasLeave())) {
            entity.setHasLeave(yesOrNo);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
            logger.info("after updating is {}", entity);
        }
        UserHospitalizedRelationBean bean = beanConverter.convert(entity);
        return bean;
    }

    @Transactional
    public UserHospitalizedRelationBean updateRelation(long groupId, int hospitalId, int departmentId, long userId, String strHasLeave, String strStatus) {
        logger.info("user={} update groupId={} hospital={} department={} relation with hasLeave={} and status={}",
                userId, groupId, hospitalId, departmentId, strHasLeave, strStatus);
        List<UserHospitalizedRelationEntity> entities = repository.findByUserIdAndHospitalIdAndDepartmentIdAndGroupId(userId, hospitalId, departmentId, groupId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.error("relation not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        UserHospitalizedRelationEntity entity = entities.get(0);
        boolean changed = false;

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        YesNoEnum yesOrNo = YesNoEnum.parseString(strHasLeave);
        if (null!=yesOrNo && !yesOrNo.equals(entity.getHasLeave())) {
            entity.setHasLeave(yesOrNo);
            changed = true;
        }

        if (changed) {
            for (UserHospitalizedRelationEntity tmp : entities) {
                if (tmp.getId() != entity.getId()) {
                    tmp.setStatus(CommonStatus.DISABLED);
                }
            }
            entities = repository.save(entities);
            entity = entities.get(0);
            logger.info("after updating is {}", entity);
        }
        UserHospitalizedRelationBean bean = beanConverter.convert(entity);
        return bean;
    }
}
