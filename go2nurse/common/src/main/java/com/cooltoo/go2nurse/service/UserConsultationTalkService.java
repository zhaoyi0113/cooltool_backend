package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserConsultationTalkBean;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.converter.UserConsultationTalkBeanConverter;
import com.cooltoo.go2nurse.entities.UserConsultationTalkEntity;
import com.cooltoo.go2nurse.repository.UserConsultationRepository;
import com.cooltoo.go2nurse.repository.UserConsultationTalkRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by hp on 2016/8/28.
 */
@Service("UserConsultationTalkService")
public class UserConsultationTalkService {

    private static final Logger logger = LoggerFactory.getLogger(UserConsultationTalkService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "time"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private UserConsultationTalkRepository repository;
    @Autowired private UserConsultationTalkBeanConverter beanConverter;

    @Autowired private UserConsultationRepository consultationRepository;


    //==================================================================
    //              GET
    //==================================================================
    public boolean existTalk(long talkId) {
        return repository.exists(talkId);
    }

    public UserConsultationTalkBean getTalkWithoutInfoById(long talkId) {
        UserConsultationTalkEntity talk = repository.findOne(talkId);
        if (null==talk) {
            return null;
        }
        return beanConverter.convert(talk);
    }

    public List<UserConsultationTalkBean> getTalkByConsultationId(long consultationId) {
        logger.info("get consultation talk by consultationId={}.", consultationId);
        List<Long> consultationIds = new ArrayList<>();
        consultationIds.add(consultationId);
        List<UserConsultationTalkEntity> comments = repository.findByStatusNotAndConsultationIdIn(CommonStatus.DELETED, consultationIds, sort);
        List<UserConsultationTalkBean> retValue = entitiesToBeans(comments);
        logger.info("count is {}", retValue.size());
        return retValue;
    }

    public Map<Long, UserConsultationTalkBean> getBestTalkByConsultationIds(List<Long> consultationIds) {
        logger.info("get consultationId-->one talk by consultationIds={}.",
                VerifyUtil.isListEmpty(consultationIds) ? 0 : consultationIds.size());
        if (VerifyUtil.isListEmpty(consultationIds)) {
            return new HashMap<>();
        }
        List<UserConsultationTalkEntity> talks = repository.findByStatusNotAndConsultationIdIn(CommonStatus.DELETED, consultationIds, sort);
        List<UserConsultationTalkBean> retValue = entitiesToBeans(talks);
        Map<Long, UserConsultationTalkBean> consultationIdToTalk = new HashMap<>();
        for (UserConsultationTalkBean talk : retValue) {
            if (consultationIdToTalk.containsKey(talk.getConsultationId())) {
                if (!talk.isBest()) {
                    continue;
                }
                else {
                    UserConsultationTalkBean tmpTalk = consultationIdToTalk.get(talk.getConsultationId());
                    if (tmpTalk.isBest()) {
                        continue;
                    }
                }
            }
            ConsultationTalkStatus talkStatus = talk.getTalkStatus();
            if (ConsultationTalkStatus.NURSE_SPEAK.equals(talkStatus) || ConsultationTalkStatus.ADMIN_SPEAK.equals(talkStatus)) {
                consultationIdToTalk.put(talk.getConsultationId(), talk);
            }
        }
        logger.info("count is {}", consultationIdToTalk.size());
        return consultationIdToTalk;
    }

    public Map<Long, Long> getUnreadTalkSizeByConsultationIds(List<Long> consultationIds, ConsultationTalkStatus talkStatusNotMatch) {
        logger.info("get consultationId-->one talk by consultationIds={}.",
                VerifyUtil.isListEmpty(consultationIds) ? 0 : consultationIds.size());
        if (VerifyUtil.isListEmpty(consultationIds)) {
            return new HashMap<>();
        }
        List<UserConsultationTalkEntity> talks = repository.findByStatusNotAndReadingStatusAndConsultationIdIn(CommonStatus.DELETED, ReadingStatus.UNREAD, consultationIds, sort);
        Map<Long, Long> consultationIdToUnreadTalkSize = new HashMap<>();
        for (UserConsultationTalkEntity talk : talks) {
            if (null==talkStatusNotMatch) {
                consultationIdToUnreadTalkSize.put(talk.getConsultationId(), 0L);
                continue;
            }
            if (talkStatusNotMatch.equals(talk.getTalkStatus())) {
                continue;
            }
            if (consultationIdToUnreadTalkSize.containsKey(talk.getConsultationId())) {
                Long tmpTalkSize = consultationIdToUnreadTalkSize.get(talk.getConsultationId());
                consultationIdToUnreadTalkSize.put(talk.getConsultationId(), tmpTalkSize++);
            }
            else {
                consultationIdToUnreadTalkSize.put(talk.getConsultationId(), 1L);
            }
        }
        logger.info("count is {}", consultationIdToUnreadTalkSize.size());
        return consultationIdToUnreadTalkSize;
    }

    private List<UserConsultationTalkBean> entitiesToBeans(Iterable<UserConsultationTalkEntity> entities) {
        List<UserConsultationTalkBean> retVal = new ArrayList<>();
        if (null==entities) {
            return retVal;
        }

        for (UserConsultationTalkEntity tmp : entities) {
            UserConsultationTalkBean bean = beanConverter.convert(tmp);
            retVal.add(bean);
        }

        return retVal;
    }

    //==================================================================
    //              delete
    //==================================================================
    @Transactional
    public List<Long> deleteByIds(List<Long> talkIds) {
        logger.info("delete consultation talk by talkIds {}.", talkIds);
        List<Long> retValue = new ArrayList<>();
        if (null==talkIds || talkIds.isEmpty()) {
            return retValue;
        }

        List<UserConsultationTalkEntity> comments = repository.findByStatusNotAndIdIn(CommonStatus.DELETED, talkIds);
        if (!VerifyUtil.isListEmpty(comments)) {
            for (UserConsultationTalkEntity comment : comments) {
                comment.setStatus(CommonStatus.DELETED);
                retValue.add(comment.getId());
            }
            repository.save(comments);
        }
        logger.info("count is {}", retValue.size());
        return retValue;
    }

    //==================================================================
    //                           adding
    //==================================================================
    @Transactional
    public long addConsultationTalk(long consultationId, long nurseId, ConsultationTalkStatus talkStatus, String talkContent) {
        logger.info("add consultation talk, consultationId={} nurseId={} talkStatus={} talkContent={}."
                , consultationId, nurseId, talkStatus, talkContent);
        if (!consultationRepository.exists(consultationId)) {
            logger.error("consultation not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        nurseId = nurseId<0 ? 0 : nurseId;
        if (null==talkStatus) {
            logger.error("talk status is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(talkContent)) {
            logger.error("talk content is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        UserConsultationTalkEntity entity = new UserConsultationTalkEntity();
        entity.setConsultationId(consultationId);
        entity.setNurseId(nurseId);
        entity.setTalkStatus(talkStatus);
        entity.setTalkContent(talkContent.trim());
        entity.setIsBest(YesNoEnum.NO);
        entity.setReadingStatus(ReadingStatus.UNREAD);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        return entity.getId();
    }

    //==================================================================
    //                           updating
    //==================================================================
    @Transactional
    public long updateConsultationTalk(long talkId, YesNoEnum isBest) {
        logger.info("update consultation talkId={} isBest={}.", talkId, isBest);
        UserConsultationTalkEntity talkEntity = repository.findOne(talkId);
        if (null==talkEntity) {
            logger.error("talk not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (ConsultationTalkStatus.USER_SPEAK.equals(talkEntity.getTalkStatus())
                || ConsultationTalkStatus.NONE.equals(talkEntity.getTalkStatus())) {
            logger.error("user talk can not set isBest");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        boolean changed = false;
        if (null!=isBest && !isBest.equals(talkEntity.getIsBest())) {
            talkEntity.setIsBest(isBest);
            changed = true;
        }
        if (changed) {
            repository.save(talkEntity);
        }

        return talkId;
    }

    @Transactional
    public long updateConsultationUnreadTalkStatusToRead(long consultationId, ConsultationTalkStatus talkStatusNotMatch) {
        logger.info("update consultation(Id={}) talkStatusNotMatch={} talks' readingStatus={}.",
                consultationId, talkStatusNotMatch, ReadingStatus.READ);
        List<UserConsultationTalkEntity> unreadTalks = repository.findByReadingStatusAndTalkStatusNotAndConsultationId(ReadingStatus.UNREAD, talkStatusNotMatch, consultationId);
        if (VerifyUtil.isListEmpty(unreadTalks)) {
            logger.error("talks not exists");
            return consultationId;
        }
        boolean changed = false;
        if (null != talkStatusNotMatch) {
            for (UserConsultationTalkEntity talkEntity : unreadTalks) {
                if (!talkStatusNotMatch.equals(talkEntity.getTalkStatus())) {
                    talkEntity.setReadingStatus(ReadingStatus.READ);
                    changed = true;
                }
            }
        }
        if (changed) {
            repository.save(unreadTalks);
        }

        return consultationId;
    }
}
