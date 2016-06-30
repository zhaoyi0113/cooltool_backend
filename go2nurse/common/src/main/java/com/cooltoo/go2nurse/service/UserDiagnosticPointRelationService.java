package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserDiagnosticPointRelationBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.converter.UserDiagnosticPointRelationBeanConverter;
import com.cooltoo.go2nurse.entities.UserDiagnosticPointRelationEntity;
import com.cooltoo.go2nurse.repository.UserDiagnosticPointRelationRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
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
 * Created by hp on 2016/6/15.
 */
@Service("UserDiagnosticPointRelationService")
public class UserDiagnosticPointRelationService {

    private static final Logger logger = LoggerFactory.getLogger(UserDiagnosticPointRelationService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "groupId"),
            new Sort.Order(Sort.Direction.ASC, "time"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private UserRepository userRepository;
    @Autowired private UserDiagnosticPointRelationRepository repository;
    @Autowired private UserDiagnosticPointRelationBeanConverter beanConverter;

    //===================================================
    //               get
    //===================================================
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

    public List<UserDiagnosticPointRelationBean> getRelation(long userId, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get the diagnostic point with user={} status={} at page={} sizePerPage={}",
                userId, strStatus, pageIndex, sizePerPage);
        CommonStatus status = CommonStatus.parseString(strStatus);
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<UserDiagnosticPointRelationEntity> resultSet = repository.findByUserIdAndStatus(userId, status, page);
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
    public List<UserDiagnosticPointRelationBean> addUserDiagnosticRelation(long userId, long groupId, List<DiagnosticEnumeration> diagnosticPoints, List<Date> pointTimes) {
        logger.info("add diagnostic point to user={}, groupId={}, diagnostic_point={}, point_time={}",
                userId, groupId, diagnosticPoints, pointTimes);
        if (!userRepository.exists(userId)) {
            logger.error("user not exist");
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        if (VerifyUtil.isListEmpty(diagnosticPoints)) {
            logger.error("diagnostic point is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        UserDiagnosticPointRelationEntity entity = null;
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
            entity = repository.save(entity);
            relations.add(beanConverter.convert(entity));
        }

        logger.info("add relations is {}", relations);
        return relations;
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
    public UserDiagnosticPointRelationBean updateUserDiagnosticRelation(long groupId, long diagnosticId, long userId, Date pointTime, String strStatus) {
        logger.info("user={} update diagnostic={} with groupId={} pointTime={} and status={}",
                userId, diagnosticId, groupId, pointTime, strStatus);
        List<UserDiagnosticPointRelationEntity> entities = repository.findByUserIdAndDiagnosticIdAndGroupId(userId, diagnosticId, groupId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            logger.info("not user's diagnostic point relation");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        UserDiagnosticPointRelationEntity entity = entities.get(entities.size() - 1);
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
            logger.info("after updating is {}", entities);
        }
        UserDiagnosticPointRelationBean bean = beanConverter.convert(entity);
        return bean;
    }
}
