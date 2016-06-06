package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakTopicSubscriberBean;
import com.cooltoo.backend.converter.NurseSpeakTopicSubscriberBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakTopicSubscriberEntity;
import com.cooltoo.backend.repository.NurseSpeakTopicSubscriberRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
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
 * Created by hp on 2016/6/3.
 */
@Service("NurseSpeakTopicSubscriberService")
public class NurseSpeakTopicSubscriberService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakTopicSubscriberService.class);
    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "time"),
            new Sort.Order(Sort.Direction.ASC, "userId"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private NurseSpeakTopicSubscriberRepository repository;
    @Autowired private NurseSpeakTopicSubscriberBeanConverter beanConverter;

    //======================================================================
    //             get
    //======================================================================
    public long countSubscriberInTopic(long topicId, String strStatus) {
        logger.info("count subscriber number in topic={} status={}", topicId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countByTopicIdAndStatus(topicId, status);
        logger.info("size is {}", count);
        return count;
    }

    public long countSubscriberByUser(long userId, String strUserType, String strStatus) {
        logger.info("count subscriber number by userId={} userType={} status={}", userId, strUserType, strStatus);
        UserType userType = UserType.parseString(strUserType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countByUserIdAndUserTypeAndStatus(userId, userType, status);
        logger.info("size is {}", count);
        return count;
    }

    public List<NurseSpeakTopicSubscriberBean> getSubscriberInTopic(long topicId, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get subscriber in topic={} status={} at page={} sizePerPage={}",
                topicId, strStatus, pageIndex, sizePerPage);
        CommonStatus status = CommonStatus.parseString(strStatus);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseSpeakTopicSubscriberEntity> resultSet = repository.findByTopicIdAndStatus(topicId, status, page);
        List<NurseSpeakTopicSubscriberBean> beans = entitiesToBean(resultSet);

        return beans;
    }

    public List<NurseSpeakTopicSubscriberBean> getSubscriberByUser(long userId, String strUserType, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get subscriber by userId={} userType={] status={} at page={} sizePerPage={}",
                userId, strUserType, strStatus, pageIndex, sizePerPage);
        CommonStatus status = CommonStatus.parseString(strStatus);
        UserType userType = UserType.parseString(strUserType);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseSpeakTopicSubscriberEntity> resultSet = repository.findByUserIdAndUserTypeAndStatus(userId, userType, status, page);
        List<NurseSpeakTopicSubscriberBean> beans = entitiesToBean(resultSet);

        return beans;
    }

    private List<NurseSpeakTopicSubscriberBean> entitiesToBean(Iterable<NurseSpeakTopicSubscriberEntity> entities) {
        List<NurseSpeakTopicSubscriberBean> list = new ArrayList<>();
        if (null==entities) {
            return list;
        }

        NurseSpeakTopicSubscriberBean bean;
        for (NurseSpeakTopicSubscriberEntity entity : entities) {
            bean = beanConverter.convert(entity);
            list.add(bean);
        }
        return list;
    }

    //======================================================================
    //             set
    //======================================================================
    @Transactional
    public NurseSpeakTopicSubscriberBean setTopicSubscriber(long topicId, long userId, String strUserType) {
        logger.info("set topic subscriber topicId={} userId={} userType={} status={}",
                topicId, userId, strUserType);
        UserType userType = UserType.parseString(strUserType);
        if (null==userType){
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseSpeakTopicSubscriberEntity entity;
        List<NurseSpeakTopicSubscriberEntity> entities = repository.findByTopicIdAndUserIdAndUserTypeAndStatus(topicId, userId, userType, CommonStatus.ENABLED);
        if (null!=entities && !entities.isEmpty()) {
            entity = entities.get(0);
            for (NurseSpeakTopicSubscriberEntity subscriber : entities) {
                subscriber.setStatus(CommonStatus.DELETED);
            }
            repository.save(entities);
        }
        else {
            entity = new NurseSpeakTopicSubscriberEntity();
            entity.setTopicId(topicId);
            entity.setUserId(userId);
            entity.setUserType(userType);
            entity.setStatus(CommonStatus.ENABLED);
            entity.setTime(new Date());
            entity = repository.save(entity);
        }

        NurseSpeakTopicSubscriberBean bean = beanConverter.convert(entity);
        logger.info("topic subscriber is {}", bean);
        return bean;
    }

    //======================================================================
    //             update
    //======================================================================
    @Transactional
    public int updateStatusByUser(long userId, String strUserType, String strOrgStatus, String strStatus) {
        logger.info("set topic subscriber status by userId={} userType={} status={} to status={}", userId, strUserType, strOrgStatus, strStatus);
        CommonStatus orgStatus = CommonStatus.parseString(strOrgStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        UserType userType = UserType.parseString(strUserType);
        if (null==orgStatus || null==status || null==userType) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        int count;
        List<NurseSpeakTopicSubscriberEntity> entities = repository.findByUserIdAndUserTypeAndStatus(userId, userType, orgStatus);
        if (VerifyUtil.isListEmpty(entities)) {
            count = 0;
        }
        else {
            for (NurseSpeakTopicSubscriberEntity entity : entities) {
                entity.setStatus(status);
                entity.setTime(new Date());
            }
            repository.save(entities);
            count = entities.size();
        }

        logger.info("update count is {}", count);
        return count;
    }

    @Transactional
    public int updateStatusInTopic(long topicId, String strOrgStatus, String strStatus) {
        logger.info("set topic subscriber status by topicId={} status={} to status={}", topicId, strOrgStatus, strStatus);
        CommonStatus orgStatus = CommonStatus.parseString(strOrgStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        int count;
        List<NurseSpeakTopicSubscriberEntity> entities = repository.findByTopicIdAndStatus(topicId, orgStatus);
        if (VerifyUtil.isListEmpty(entities)) {
            count = 0;
        }
        else {
            for (NurseSpeakTopicSubscriberEntity entity : entities) {
                entity.setStatus(status);
                entity.setTime(new Date());
            }
            repository.save(entities);
            count = entities.size();
        }

        logger.info("update count is {}", count);
        return count;
    }
}
