package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakTopicRelationBean;
import com.cooltoo.backend.converter.NurseSpeakTopicRelationBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakTopicRelationEntity;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.backend.repository.NurseSpeakTopicRelationRepository;
import com.cooltoo.backend.repository.NurseSpeakTopicRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hp on 2016/6/2.
 */
@Service("NurseSpeakTopicRelationService")
public class NurseSpeakTopicRelationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakTopicRelationService.class);
    private static final Sort sortDesc = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "speakId"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );
    private static final Sort sortASC = new Sort(
            new Sort.Order(Sort.Direction.ASC, "time"),
            new Sort.Order(Sort.Direction.ASC, "speakId"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private NurseSpeakTopicRelationRepository repository;
    @Autowired private NurseSpeakTopicRelationBeanConverter beanConverter;
    @Autowired private NurseRepository nurseRepository;
    @Autowired private NurseSpeakRepository speakRepository;
    @Autowired private NurseSpeakTopicRepository topicRepository;

    //=========================================================
    //        get
    //=========================================================
    public long countUserTakePartIn(long topicId, String strUserAuthority) {
        logger.info("count user take part in the topic {} by userAuthority={}",
                topicId, strUserAuthority);
        UserAuthority userAuthority = UserAuthority.parseString(strUserAuthority);

        long count = 0;
        List<Long> userIds = repository.getUserIdInTopic(topicId, sortASC);
        if (!VerifyUtil.isListEmpty(userIds)) {
            count = nurseRepository.countByAuthorityAndIdIn(userAuthority, userIds);
        }

        logger.info("count is = {}", count);
        return count;
    }

    public List<Long> getUserTakePartIn(long topicId, String strUserAuthority, int page, int size) {
        logger.info("count user take part in the topic {} by userAuthority={} at page={} size={}",
                topicId, strUserAuthority, page, size);
        UserAuthority userAuthority = UserAuthority.parseString(strUserAuthority);

        List<Long> userIds = repository.getUserIdInTopic(topicId, sortASC);
        List<Long> userIdsEnabled = nurseRepository.findByAuthorityAndIdIn(userAuthority, userIds);
        List<Long> validUserIds = new ArrayList<>();
        for (Long userId : userIds) {
            if (userIdsEnabled.contains(userId)) {
                validUserIds.add(userId);
            }
        }

        userIdsEnabled  = new ArrayList<>();
        int iIndex   = Math.abs(page);
        int iNumber  = Math.abs(size);
        int startIdx = iIndex*iNumber;
        int endIdx   = startIdx + size;
        if (startIdx>=validUserIds.size()) {
            return userIdsEnabled;
        }

        for (int i=startIdx, count=validUserIds.size(); i<endIdx && i<count; i++) {
            Long tmp = validUserIds.get(i);
            userIdsEnabled.add(tmp);
        }
        logger.info("count is {}", userIdsEnabled.size());
        return userIdsEnabled;
    }

    public long countSpeaksInTopic(long topicId, String strSpeakStatus) {
        logger.info("count speak in topic={} speakStatus={}", topicId, strSpeakStatus);
        CommonStatus speakStatus = CommonStatus.parseString(strSpeakStatus);

        long count = 0;
        List<Long> speakIds = repository.findSpeakIdsInTopic(topicId, sortDesc);
        List<Long> speakIdsEnable = speakRepository.findByStatusAndIdIn(speakStatus, speakIds);
        if (!VerifyUtil.isListEmpty(speakIdsEnable)) {
            count = speakIdsEnable.size();
        }
        logger.info("count is {}", count);
        return count;
    }

    public List<Long> getSpeaksIdInTopic(long topicId, String strSpeakStatus, int pageIndex, int sizePerPage) {
        logger.info("get speak ids in topic={} speakStatus={}", topicId, strSpeakStatus);
        CommonStatus speakStatus = CommonStatus.parseString(strSpeakStatus);

        List<Long> speakIds = repository.findSpeakIdsInTopic(topicId, sortDesc);
        List<Long> speakIdsEnable = speakRepository.findByStatusAndIdIn(speakStatus, speakIds);
        List<Long> validSpeakIds = new ArrayList<>();
        for (Long speakId : speakIds) {
            if (speakIdsEnable.contains(speakId)) {
                validSpeakIds.add(speakId);
            }
        }

        speakIdsEnable  = new ArrayList<>();
        int iIndex   = Math.abs(pageIndex);
        int iNumber  = Math.abs(sizePerPage);
        int startIdx = iIndex*iNumber;
        int endIdx   = startIdx + sizePerPage;
        if (startIdx>=validSpeakIds.size()) {
            return speakIdsEnable;
        }

        for (int i=startIdx, count=validSpeakIds.size(); i<endIdx && i<count; i++) {
            Long tmp = validSpeakIds.get(i);
            speakIdsEnable.add(tmp);
        }
        logger.info("count is {}", speakIdsEnable.size());
        return speakIdsEnable;
    }

    public List<Long> getTopicIdsBySpeakId(long speakId, String strTopicStatus) {
        logger.info("get topic ids which speak {} contains and topicStatus={}", speakId, strTopicStatus);
        CommonStatus status = CommonStatus.parseString(strTopicStatus);

        List<Long> topicIds = repository.findTopicIdsBySpeakId(speakId, sortDesc);
        List<Long> topicIdsEnable = topicRepository.findByStatusAndIdIn(status, topicIds);
        List<Long> validTopicIds = new ArrayList<>();
        for (Long topicId : topicIds) {
            if (topicIdsEnable.contains(topicId)) {
                validTopicIds.add(topicId);
            }
        }
        logger.info("count is {}", validTopicIds.size());
        return validTopicIds;
    }

    public Map<Long, List<Long>> getTopicIdsBySpeakIds(List<Long> speakIds, String strTopicStatus) {
        logger.info("get topic ids which status={} speakids={}", strTopicStatus, speakIds);
        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.DESC, "speakId"),
                new Sort.Order(Sort.Direction.DESC, "time"),
                new Sort.Order(Sort.Direction.DESC, "id")
        );
        CommonStatus status = CommonStatus.parseString(strTopicStatus);

        List<Object[]> speakId2TopicId = repository.findTopicIdsBySpeakIds(speakIds, sort);
        if (!VerifyUtil.isListEmpty(speakId2TopicId)) {
            List<Long> topicIds = new ArrayList<>();
            for (Object[] st : speakId2TopicId) {
                Long topicId = (Long) st[1];
                if (null!=topicId && !topicIds.contains(topicId)) {
                    topicIds.add(topicId);
                }
            }
            List<Long> topicIdsEnable = topicRepository.findByStatusAndIdIn(status, topicIds);
            List<Long> validTopicIds = new ArrayList<>();
            for (Long topicId : topicIds) {
                if (topicIdsEnable.contains(topicId)) {
                    validTopicIds.add(topicId);
                }
            }

            Map<Long, List<Long>> retVal = new HashMap<>();
            for (Object[] st : speakId2TopicId) {
                Long speakId = (Long) st[0];
                Long topicId = (Long) st[1];
                List<Long> topicInSpeak = retVal.get(speakId);
                if (null==topicInSpeak) {
                    topicInSpeak = new ArrayList<>();
                    retVal.put(speakId, topicInSpeak);
                }
                if (null!=topicId && !topicInSpeak.contains(topicId) && validTopicIds.contains(topicId)) {
                    topicInSpeak.add(topicId);
                }
            }
            return retVal;
        }
        return new HashMap<>();
    }

    //=========================================================
    //        add
    //=========================================================
    @Transactional
    public List<NurseSpeakTopicRelationBean> addTopicRelation(List<Long> topicsId, long speakId, long userId) {
        logger.info("add speak topic relation topicsId={} speakId={} userId={}", topicsId, speakId, userId);
        if (VerifyUtil.isListEmpty(topicsId)) {
            logger.info("topicsId is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (speakId<=0) {
            logger.info("speakId < 0");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (userId<=0) {
            if (userId==-1) {
                // official speak
            }
            else {
                logger.info("user id invalid");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }

        List<NurseSpeakTopicRelationBean> retVal = new ArrayList<>();
        NurseSpeakTopicRelationEntity entity;
        for (Long topicId : topicsId) {
            if (topicId<=0) {
                continue;
            }
            entity = new NurseSpeakTopicRelationEntity();
            entity.setTopicId(topicId);
            entity.setSpeakId(speakId);
            entity.setUserId(userId);
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
            entity = repository.save(entity);
            retVal.add(beanConverter.convert(entity));
        }
        return retVal;
    }

    //=========================================================
    //        update
    //=========================================================
//    @Transactional
//    public int updateTopicRelationBySpeakId(long speakId, List<CommonStatus> originalStatuses, CommonStatus status) {
//        logger.info("update topic relation status from {} to {} by speakId={}",
//                originalStatuses, status, speakId);
//        if (null==status) {
//            throw new BadRequestException(ErrorCode.DATA_ERROR);
//        }
//
//        int count = repository.updateStatusBySpeakId(speakId, originalStatuses, status);
//        logger.info("modified count is {}", count);
//        return count;
//    }
//
//    @Transactional
//    public int updateTopicRelationByTopicId(long topicId, List<CommonStatus> originalStatuses, CommonStatus status) {
//        logger.info("update topic relation status from {} to {} by topicId={}",
//                originalStatuses, status, topicId);
//        if (null==status) {
//            throw new BadRequestException(ErrorCode.DATA_ERROR);
//        }
//
//        int count = repository.updateStatusByTopicId(topicId, originalStatuses, status);
//        logger.info("modified count is {}", count);
//        return count;
//    }
}
