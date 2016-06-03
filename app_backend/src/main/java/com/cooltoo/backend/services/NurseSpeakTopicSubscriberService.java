package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakTopicSubscriberBean;
import com.cooltoo.backend.converter.NurseSpeakTopicSubscriberBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakTopicSubscriberEntity;
import com.cooltoo.backend.repository.NurseSpeakTopicSubscriberRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
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
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "userId")
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

    public List<NurseSpeakTopicSubscriberBean> getSubscriberInTopic(long topicId, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get subscriber in topic={} status={} at page={} sizePerPage={}",
                topicId, strStatus, pageIndex, sizePerPage);
        CommonStatus status = CommonStatus.parseString(strStatus);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseSpeakTopicSubscriberEntity> resultSet = repository.findByTopicIdAndStatus(topicId, status, page);
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
    public NurseSpeakTopicSubscriberBean setTopicSubscriber(long topicId, long userId, String strUserType, String strStatus) {
        logger.info("set topic subscriber topicId={} userId={} userType={} status={}",
                topicId, userId, strUserType, strStatus);
        UserType userType = UserType.parseString(strUserType);
        CommonStatus status  = CommonStatus.parseString(strStatus);
        if (null==userType || null==status){
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseSpeakTopicSubscriberEntity entity = repository.findByTopicIdAndUserIdAndUserType(topicId, userId, userType);
        if (null==entity) {
            entity = new NurseSpeakTopicSubscriberEntity();
            entity.setTopicId(topicId);
            entity.setUserId(userId);
            entity.setUserType(userType);
        }
        entity.setStatus(status);
        entity.setTime(new Date());
        entity = repository.save(entity);

        NurseSpeakTopicSubscriberBean bean = beanConverter.convert(entity);
        logger.info("topic subscriber is {}", bean);
        return bean;
    }

    //======================================================================
    //             update
    //======================================================================
    @Transactional
    public NurseSpeakTopicSubscriberBean updateTopicSubscriber(long subscriberId, String strStatus) {
        logger.info("set topic subscriber subscriberId={} status={}", subscriberId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseSpeakTopicSubscriberEntity entity = repository.findOne(subscriberId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        entity.setStatus(status);
        entity.setTime(new Date());
        entity = repository.save(entity);

        NurseSpeakTopicSubscriberBean bean = beanConverter.convert(entity);
        logger.info("topic subscriber is {}", bean);
        return bean;
    }

}
