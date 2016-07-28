package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.constants.ProcessStatus;
import com.cooltoo.go2nurse.converter.UserDiagnosticPointRelationBeanConverter;
import com.cooltoo.go2nurse.entities.UserDiagnosticPointRelationEntity;
import com.cooltoo.go2nurse.repository.UserDiagnosticPointRelationRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
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
 * Created by hp on 2016/6/15.
 */
@Service("UserDiagnosticPointRelationService")
public class UserDiagnosticPointRelationService {

    private static final Logger logger = LoggerFactory.getLogger(UserDiagnosticPointRelationService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "groupId"),
            new Sort.Order(Sort.Direction.DESC, "diagnosticTime"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private UserRepository userRepository;
    @Autowired private UserDiagnosticPointRelationRepository repository;
    @Autowired private UserDiagnosticPointRelationBeanConverter beanConverter;

    //===================================================
    //               getting for user
    //===================================================
    public long getUserCurrentGroupId(long userId) {
        logger.info("get user={} current hospitalized group ID", userId);
        List<UserDiagnosticPointRelationEntity> entities = repository.findByUserIdStatusAndProcessStatus(userId, CommonStatus.ENABLED, ProcessStatus.GOING, sort);
        long groupId = Long.MIN_VALUE;
        if (VerifyUtil.isListEmpty(entities)) {
            logger.error("there is no diagnostic point for user");
        }
        else {
            groupId = entities.get(0).getGroupId();
        }
        if (Long.MIN_VALUE==groupId) {
            logger.error("there is no valid group ID for user");
        }
        logger.info("group ID is {}", groupId);
        return groupId;
    }

    public List<Long> getUserAllGroupIds(long userId) {
        logger.info("get user={} hospitalized group IDs");
        List<UserDiagnosticPointRelationEntity> entities = repository.findByUserIdAndStatus(userId, CommonStatus.ENABLED, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.error("there is no diagnostic point for user");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<Long> groupIds = new ArrayList<>();
        for (UserDiagnosticPointRelationEntity entity : entities) {
            if (!groupIds.contains(entity.getGroupId())) {
                groupIds.add(entity.getGroupId());
            }
        }
        if (groupIds.isEmpty()) {
            logger.error("there is no valid group IDs for user");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        logger.info("group IDs are {}", groupIds);
        return groupIds;
    }

    public List<UserDiagnosticPointRelationBean> getUserDiagnosticRelationByGroupId(long userId, long groupId) {
        logger.info("get user={}'s hospitalized group diagnostic point by groupId={}", userId, groupId);
        List<UserDiagnosticPointRelationEntity> entities = repository.findByUserIdAndGroupIdAndStatus(userId, groupId, CommonStatus.ENABLED, sort);
        List<UserDiagnosticPointRelationBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }


    public List<UserDiagnosticPointRelationBean> getUserCurrentDiagnosticRelation(long userId) {
        logger.info("get user={}'s hospitalized group diagnostic point of current groupId", userId);
        List<UserDiagnosticPointRelationEntity> entities = repository.findByUserIdStatusAndProcessStatus(userId, CommonStatus.ENABLED, ProcessStatus.GOING, sort);
        List<UserDiagnosticPointRelationBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    //==========================================================================
    //                  getting for admin user
    //==========================================================================

    public long countByUserAndStatus(long userId, String strStatus) {
        logger.info("count the diagnostic point with user={} status={}", userId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countByUserIdAndStatus(userId, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<UserDiagnosticPointRelationBean> getRelation(long userId, String strStatus) {
        logger.info("get the diagnostic point with user={} status={}", userId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<UserDiagnosticPointRelationEntity> resultSet = repository.findByUserIdAndStatus(userId, status, sort);
        List<UserDiagnosticPointRelationBean> relations = entitiesToBeans(resultSet);
        logger.info("count is {}", relations.size());
        return relations;
    }

    private List<UserDiagnosticPointRelationBean> entitiesToBeans(Iterable<UserDiagnosticPointRelationEntity> entities) {
        List<UserDiagnosticPointRelationBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        UserDiagnosticPointRelationBean bean;
        for (UserDiagnosticPointRelationEntity entity : entities) {
            bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    //===================================================
    //               add
    //===================================================
    @Transactional
    public List<UserDiagnosticPointRelationBean> addUserDiagnosticRelation(long userId, long groupId, List<DiagnosticEnumeration> diagnosticPoints, List<Date> pointTimes, boolean boolHasOperation) {
        logger.info("add diagnostic point to user={}, groupId={}, diagnostic_point={}, point_time={}, hasOperation={}",
                userId, groupId, diagnosticPoints, pointTimes, boolHasOperation);
        if (!userRepository.exists(userId)) {
            logger.error("user not exist");
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        if (VerifyUtil.isListEmpty(diagnosticPoints)) {
            logger.error("diagnostic point is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        UserDiagnosticPointRelationEntity entity;
        List<UserDiagnosticPointRelationBean> relations = new ArrayList<>();
        for (int i = 0, count = diagnosticPoints.size(); i < count; i++) {
            DiagnosticEnumeration diagnosticPoint = diagnosticPoints.get(i);
            Date pointTime = pointTimes.get(i);
            entity = new UserDiagnosticPointRelationEntity();
            entity.setUserId(userId);
            entity.setGroupId(groupId);
            entity.setDiagnosticId(diagnosticPoint.ordinal());
            entity.setDiagnosticTime(pointTime);
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
            entity.setProcessStatus(ProcessStatus.GOING);
            entity.setHasOperation(boolHasOperation ? YesNoEnum.YES : YesNoEnum.NO);
            entity = repository.save(entity);
            relations.add(beanConverter.convert(entity));
        }

        // set other going group diagnostic point to canceled
        List<UserDiagnosticPointRelationEntity> allGoing = repository.findByUserIdStatusAndProcessStatus(userId, CommonStatus.ENABLED, ProcessStatus.GOING, sort);
        for (UserDiagnosticPointRelationEntity tmp : allGoing) {
            if (tmp.getGroupId()==groupId) {
                continue;
            }
            tmp.setProcessStatus(ProcessStatus.CANCELED);
        }
        repository.save(allGoing);

        logger.info("add relations is {}", relations);
        return relations;
    }

    @Transactional
    private UserDiagnosticPointRelationBean addUserDiagnosticRelation(long userId, long groupId, DiagnosticEnumeration diagnosticPoint, Date pointTime, ProcessStatus processStatus, YesNoEnum hasOperation) {
        logger.info("add diagnostic point to user={}, groupId={}, diagnostic_point={}, point_time={}",
                userId, groupId, diagnosticPoint, pointTime);
        if (!userRepository.exists(userId)) {
            logger.error("user not exist");
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        if (null==diagnosticPoint) {
            logger.error("diagnostic point is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null==pointTime) {
            logger.error("diagnostic point time is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null==processStatus) {
            logger.error("process status is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        UserDiagnosticPointRelationEntity entity = null;
        entity = new UserDiagnosticPointRelationEntity();
        entity.setUserId(userId);
        entity.setGroupId(groupId);
        entity.setDiagnosticId(diagnosticPoint.ordinal());
        entity.setDiagnosticTime(pointTime);
        entity.setProcessStatus(processStatus);
        entity.setHasOperation(hasOperation);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        UserDiagnosticPointRelationBean bean = beanConverter.convert(entity);
        logger.info("add relations is {}", bean);
        return bean;
    }

    //===================================================
    //               update
    //===================================================
    @Transactional
    public UserDiagnosticPointRelationBean updateUserDiagnosticRelation(long relationId, boolean checkUser, long userId, Date pointTime, String strStatus) {
        logger.info("user={} update relation={} with pointTime={} and status={}",
                userId, relationId, pointTime, strStatus);
        UserDiagnosticPointRelationEntity entity = repository.findOne(relationId);
        if (checkUser && entity.getUserId()!=userId) {
            logger.info("not user's diagnostic point relation");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        if (null!=pointTime && !pointTime.equals(entity.getDiagnosticTime())) {
            entity.setDiagnosticTime(pointTime);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
            logger.info("after updating is {}", entity);
        }
        UserDiagnosticPointRelationBean bean = beanConverter.convert(entity);
        return bean;
    }

    @Transactional
    public List<UserDiagnosticPointRelationBean> updateUserDiagnosticPointTime(long groupId, long userId, String strDiagnostics, String strPointTime) {
        logger.info("user={} update diagnostic={} with pointTime={}", userId, strDiagnostics, strPointTime);

        List<DiagnosticEnumeration> diagnostics = DiagnosticEnumeration.getDiagnosticByTypes(strDiagnostics);
        List<Date> pointTime = VerifyUtil.parseDates(strPointTime);
        if (VerifyUtil.isListEmpty(diagnostics) || VerifyUtil.isListEmpty(pointTime)) {
            logger.info("relation_id or point_time is empty");
            return new ArrayList<>();
        }
        if (diagnostics.size() != pointTime.size()) {
            logger.info("relation_id size not equals point_time size");
            return new ArrayList<>();
        }

        List<UserDiagnosticPointRelationBean> currentRelations = getUserDiagnosticRelationByGroupId(userId, groupId);
        if (VerifyUtil.isListEmpty(currentRelations)) {
            logger.error("user is not hospitalized.");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        // cache the process status and hasOperation flag
        ProcessStatus processStatus = currentRelations.get(0).getProcessStatus();
        YesNoEnum hasOperation = currentRelations.get(0).getHasOperation();

        List<UserDiagnosticPointRelationBean> relations = new ArrayList<>();
        DiagnosticEnumeration diagnostic;
        Date newTime;
        boolean notExist;
        for (int i = 0; i < diagnostics.size(); i ++) {
            diagnostic = diagnostics.get(i);
            newTime = pointTime.get(i);
            notExist = true;
            // modify the time if exist
            for (UserDiagnosticPointRelationBean current : currentRelations) {
                if (current.getDiagnostic()==diagnostic) {
                    UserDiagnosticPointRelationBean relation = updateUserDiagnosticRelation(current.getId(), true, userId, newTime, null);
                    relations.add(relation);
                    notExist = false;
                    break;
                }
            }
            // new the relation if not exist
            if (notExist) {
                UserDiagnosticPointRelationBean relation = addUserDiagnosticRelation(userId, groupId, diagnostic, newTime, processStatus, hasOperation);
                relations.add(relation);
            }
        }
        logger.info("after updating is {}", relations);
        return relations;
    }

    @Transactional
    public UserDiagnosticPointRelationBean updateUserDiagnosticRelation(long groupId, long diagnosticId, long userId, Date pointTime, String strStatus) {
        logger.info("user={} update diagnostic={} with groupId={} pointTime={} and status={}",
                userId, diagnosticId, groupId, pointTime, strStatus);
        List<UserDiagnosticPointRelationEntity> entities = repository.findByUserIdAndGroupIdAndDiagnosticId(userId, groupId, diagnosticId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.info("not user's diagnostic point relation");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        UserDiagnosticPointRelationEntity entity = entities.get(0);
        boolean changed = false;

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        if (null!=pointTime && !pointTime.equals(entity.getDiagnosticTime())) {
            entity.setDiagnosticTime(pointTime);
            changed = true;
        }

        if (changed) {
            for (UserDiagnosticPointRelationEntity tmp : entities) {
                if (tmp.getId()!=entity.getId()) {
                    tmp.setStatus(CommonStatus.DISABLED);
                }
            }
            entities = repository.save(entities);
            entity = entities.get(0);
            logger.info("after updating is {}", entities);
        }
        UserDiagnosticPointRelationBean bean = beanConverter.convert(entity);
        return bean;
    }

    @Transactional
    public List<UserDiagnosticPointRelationBean> updateProcessStatusByUserAndGroup(long userId, long groupId, ProcessStatus processStatus) {
        logger.info("user={} update diagnostic point date relation with groupId={} and processStatus={}",
                userId, groupId, processStatus);
        if (null==processStatus) {
            logger.info("process status is empty");
            return new ArrayList<>();
        }

        List<UserDiagnosticPointRelationEntity> entities = repository.findByUserIdAndGroupId(userId, groupId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.info("no user's diagnostic point relation");
            return new ArrayList<>();
        }


        for (UserDiagnosticPointRelationEntity entity : entities) {
            entity.setProcessStatus(processStatus);
        }
        List<UserDiagnosticPointRelationBean> beans = entitiesToBeans(repository.save(entities));
        return beans;
    }

    @Transactional
    public List<UserDiagnosticPointRelationBean> updateHasOperationFlagByUserAndGroup(long userId, long groupId, Boolean boolHasOperation) {
        logger.info("user={} update diagnostic point date relation with groupId={} and hasOperation={}",
                userId, groupId, boolHasOperation);
        if (null==boolHasOperation) {
            logger.info("flag has_operation is empty");
            return new ArrayList<>();
        }
        YesNoEnum hasOperation = boolHasOperation ? YesNoEnum.YES : YesNoEnum.NO;

        List<UserDiagnosticPointRelationEntity> entities = repository.findByUserIdAndGroupId(userId, groupId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.info("no user's diagnostic point relation");
            return new ArrayList<>();
        }


        for (UserDiagnosticPointRelationEntity entity : entities) {
            entity.setHasOperation(hasOperation);
        }
        List<UserDiagnosticPointRelationBean> beans = entitiesToBeans(repository.save(entities));
        return beans;
    }
}
