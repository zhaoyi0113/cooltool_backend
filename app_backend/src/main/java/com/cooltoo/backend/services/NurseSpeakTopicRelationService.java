package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakTopicRelationBean;
import com.cooltoo.backend.converter.NurseSpeakTopicRelationBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakTopicRelationEntity;
import com.cooltoo.backend.repository.NurseSpeakTopicRelationRepository;
import com.cooltoo.constants.CommonStatus;
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
 * Created by hp on 2016/6/2.
 */
@Service("NurseSpeakTopicRelationService")
public class NurseSpeakTopicRelationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakTopicRelationService.class);
    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "speakId")
    );

    @Autowired private NurseSpeakTopicRelationRepository repository;
    @Autowired private NurseSpeakTopicRelationBeanConverter beanConverter;

    //=========================================================
    //        get
    //=========================================================
    public long countSpeaksInTopic(long topicId, String strStatus) {
        logger.info("count speak in topic={} status={}", topicId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countSpeakInTopic(topicId, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<Long> getSpeaksIdInTopic(long topicId, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get speak ids in topic={} status={}", topicId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<Long> speakIds = repository.findSpeakIdsInTopic(topicId, status, page);
        List<Long> retVal = new ArrayList<>();
        if (null!=speakIds) {
            for (Long id : speakIds) {
                retVal.add(id);
            }
        }
        return retVal;
    }

    //=========================================================
    //        add
    //=========================================================
    @Transactional
    public NurseSpeakTopicRelationBean addTopicRelation(long topicId, long speakId) {
        logger.info("add speak topic relation topicId={} speakId={}", topicId, speakId);
        if (topicId<=0) {
            logger.info("topicId < 0");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (speakId<=0) {
            logger.info("speakId < 0");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        NurseSpeakTopicRelationEntity entity = new NurseSpeakTopicRelationEntity();
        entity.setTopicId(topicId);
        entity.setSpeakId(speakId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);

        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    //=========================================================
    //        update
    //=========================================================
    @Transactional
    public NurseSpeakTopicRelationBean updateTopicRelationById(long relationId, CommonStatus status) {
        logger.info("update topic relation status to {} by id", status, relationId);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (repository.exists(relationId)) {
            NurseSpeakTopicRelationEntity entity = repository.getOne(relationId);
            if (!status.equals(entity.getStatus())) {
                entity.setStatus(status);
                entity = repository.save(entity);
            }
            return beanConverter.convert(entity);
        }
        throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
    }

    @Transactional
    public long updateTopicRelationBySpeakId(long speakId, List<CommonStatus> originalStatuses, CommonStatus status) {
        logger.info("update topic relation status from {} to {} by speakId={}",
                originalStatuses, status, speakId);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long count = repository.updateStatusBySpeakId(speakId, originalStatuses, status);
        logger.info("modified count is {}", count);
        return count;
    }

    @Transactional
    public long updateTopicRelationByTopicId(long topicId, List<CommonStatus> originalStatuses, CommonStatus status) {
        logger.info("update topic relation status from {} to {} by topicId={}",
                originalStatuses, status, topicId);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long count = repository.updateStatusByTopicId(topicId, originalStatuses, status);
        logger.info("modified count is {}", count);
        return count;
    }
}
