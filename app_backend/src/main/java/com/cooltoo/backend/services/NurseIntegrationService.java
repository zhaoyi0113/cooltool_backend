package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.*;
import com.cooltoo.backend.converter.NurseIntegrationBeanConverter;
import com.cooltoo.backend.converter.social_ability.CommentAbilityTypeConverter;
import com.cooltoo.backend.converter.social_ability.SpeakAbilityTypeConverter;
import com.cooltoo.backend.converter.social_ability.ThumbsUpAbilityTypeConverter;
import com.cooltoo.backend.entities.NurseIntegrationEntity;
import com.cooltoo.backend.repository.NurseIntegrationRepository;
import com.cooltoo.backend.repository.NurseSpeakCommentRepository;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.backend.repository.NurseSpeakThumbsUpRepository;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.constants.UserType;
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
 * Created by yzzhao on 5/21/16.
 */
@Service
public class NurseIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseIntegrationService.class);
    private Map<SpeakType, SpecificSocialAbility> type2SpeakTypePoints = new Hashtable<>();
    private Map<Integer, SpecificSocialAbility> id2SpeakTypePoints = new Hashtable<>();

    @Autowired
    private NurseService nurseService;
    @Autowired
    private NurseSpeakThumbsUpRepository thumbsUpRepository;
    @Autowired
    private NurseSpeakCommentRepository commentRepository;
    @Autowired
    private NurseIntegrationRepository repository;
    @Autowired
    private NurseIntegrationBeanConverter beanConverter;
    @Autowired private SpeakAbilityTypeConverter speakTypeConverter;
    @Autowired private ThumbsUpAbilityTypeConverter thumbsUpTypeConverter;
    @Autowired private CommentAbilityTypeConverter commentTypeConverter;

    //============================================================================
    //                    add
    //============================================================================
    @Transactional
    public void addNurseSpeakIntegration(long userId, SpeakType speakType, NurseSpeakBean nurseSpeakBean) {
        try {
            if (!nurseService.existNurse(userId)) {
                logger.error("can't find nurse {}", userId);
                throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
            }
            SpecificSocialAbility speakAbility = getSpeakTypePoint(speakType);
            addIntegration(userId, UserType.NURSE, speakAbility.getAbilityType(), speakAbility.getAbilityId(), nurseSpeakBean.getId(), speakAbility.getFactor());
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void addNurseThumbsUpIntegration(NurseSpeakThumbsUpBean nurseThumbsUpBean) {
        long thumbsUpMaker = nurseThumbsUpBean.getThumbsUpUserId();
        long speakMaker    = nurseThumbsUpBean.getUserIdBeenThumbsUp();
        if (!nurseService.existNurse(thumbsUpMaker)) {
            logger.error("can't find thumbs up maker {}", nurseThumbsUpBean);
            return;
        }
        if (!nurseService.existNurse(speakMaker)) {
            logger.error("can't find speak maker {}", nurseThumbsUpBean);
            return;
        }
        // 自己点赞不获得积分
        if (nurseThumbsUpBean.getThumbsUpUserId()==nurseThumbsUpBean.getUserIdBeenThumbsUp()) {
            logger.error("can't make integration by self {}", nurseThumbsUpBean);
            return;
        }
        // 获取点赞人针对该发言的所有点赞
        List<Long> thumbsUpIds = thumbsUpRepository.findThumbsUpIdBySpeakIdAndMakerId(nurseThumbsUpBean.getNurseSpeakId(), nurseThumbsUpBean.getThumbsUpUserId());
        if (VerifyUtil.isListEmpty(thumbsUpIds)) {
            logger.error("thumbs up maker doesn't make thumbs up {}", nurseThumbsUpBean);
            return;
        }
        // 获取被点赞的人是否已获得点赞人针对该发言的点赞积分
        // 已经获取了，就不在计分
        long thumbsUpIntegrations = repository.countByStatusAndUserTypeAndUserIdAndAbilityTypeAndReasonIdIn(CommonStatus.ENABLED, UserType.NURSE, speakMaker, SocialAbilityType.THUMBS_UP, thumbsUpIds);
        if (thumbsUpIntegrations>0) {
            return;
        }
        try {
            SpecificSocialAbility beenThumbsUpAbility = thumbsUpTypeConverter.getItem(ThumbsUpAbilityTypeConverter.BEEN_THUMBS_UP);
            addIntegration(speakMaker, UserType.NURSE, beenThumbsUpAbility.getAbilityType(), beenThumbsUpAbility.getAbilityId(), nurseThumbsUpBean.getId(), beenThumbsUpAbility.getFactor());
            SpecificSocialAbility thumbsUpAbility = thumbsUpTypeConverter.getItem(ThumbsUpAbilityTypeConverter.THUMBS_UP_OTHERS);
            addIntegration(thumbsUpMaker, UserType.NURSE, thumbsUpAbility.getAbilityType(), thumbsUpAbility.getAbilityId(), nurseThumbsUpBean.getId(), thumbsUpAbility.getFactor());
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void addNurseCommentIntegration(NurseSpeakCommentBean nurseCommentBean) {
        if (!nurseService.existNurse(nurseCommentBean.getCommentMakerId())) {
            logger.error("can't find comment maker {}", nurseCommentBean);
            return;
        }
        // 自己评论不获得积分
        if (nurseCommentBean.getCommentMakerId() == nurseCommentBean.getSpeakMakerId()) {
            logger.error("can't make integration by self {}", nurseCommentBean);
            return;
        }
        SpecificSocialAbility speakAbility = getSpeakTypePoint(nurseCommentBean.getNurseSpeakTypeId());
        SpeakType speakType = (SpeakType) speakAbility.getProperty(SpecificSocialAbility.Speak_Type);
        SpecificSocialAbility commentAbility;
        if (SpeakType.ASK_QUESTION.equals(speakType)) {
            commentAbility = commentTypeConverter.getItem(CommentAbilityTypeConverter.ANSWER);
        }
        else {
            commentAbility = commentTypeConverter.getItem(CommentAbilityTypeConverter.COMMENT);
        }
        // 获取评论人针对该发言的所有评论
        List<Long> commentIds = commentRepository.findCommentIdBySpeakIdAndMakerId(nurseCommentBean.getNurseSpeakId(), nurseCommentBean.getCommentMakerId());
        if (VerifyUtil.isListEmpty(commentIds)) {
            logger.error("comment maker doesn't make comment {}", nurseCommentBean);
            return;
        }
        // 获取评论人是否已获得针对该发言的评论积分
        // 已经获取了，就不在计分
        long commentMaker = nurseCommentBean.getCommentMakerId();
        long commentIntegrations = repository.countByStatusAndUserTypeAndUserIdAndAbilityTypeAndReasonIdIn(CommonStatus.ENABLED, UserType.NURSE, commentMaker, SocialAbilityType.COMMENT, commentIds);
        if (commentIntegrations>0) {
            return;
        }
        try {
            addIntegration(nurseCommentBean.getCommentMakerId(), UserType.NURSE, commentAbility.getAbilityType(), commentAbility.getAbilityId(), nurseCommentBean.getId(), commentAbility.getFactor());
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    private void addIntegration(long userId, UserType userType, SocialAbilityType abilityType, int abilityId, long reasonId, long point) {
        try {
            NurseBean nurse = nurseService.getNurseWithoutOtherInfo(userId);
            if (nurse!=null) {
                NurseIntegrationEntity integrationE = repository.findByUserIdAndUserTypeAndAbilityTypeAndAbilityIdAndReasonId(userId, userType, abilityType, abilityId, reasonId);
                if (null != integrationE) {
                    integrationE.setStatus(CommonStatus.ENABLED);
                    repository.save(integrationE);
                } else {
                    NurseIntegrationEntity entity = new NurseIntegrationEntity();
                    entity.setUserId(userId);
                    entity.setUserType(userType);
                    entity.setAbilityType(abilityType);
                    entity.setAbilityId(abilityId);
                    entity.setReasonId(reasonId);
                    entity.setPoint(point);
                    entity.setTime(new Date());
                    entity.setStatus(CommonStatus.ENABLED);
                    repository.save(entity);
                }
            }
            else {
                logger.error("can't find nurse " + userId);
            }
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
        }
    }

    //============================================================================
    //                    get
    //============================================================================
    public int getNurseCommunityIntegration(long userId, SpeakType speakType) {
        SpecificSocialAbility ability = getSpeakTypePoint(speakType);
        if (ability == null) {
            return 0;
        }
        return getIntegration(userId, ability.getAbilityType(), ability.getAbilityId());
    }

    public int getNurseCommentIntegration(long userId) {
        SpecificSocialAbility ability = commentTypeConverter.getItem(CommentAbilityTypeConverter.COMMENT);
        return getIntegration(userId, ability.getAbilityType(), ability.getAbilityId());
    }

    public int getNurseAnswerIntegration(long userId) {
        SpecificSocialAbility ability = commentTypeConverter.getItem(CommentAbilityTypeConverter.ANSWER);
        return getIntegration(userId, ability.getAbilityType(), ability.getAbilityId());
    }

    public int getNurseBeenThumbsUpIntegration(long userId) {
        SpecificSocialAbility ability = thumbsUpTypeConverter.getItem(ThumbsUpAbilityTypeConverter.BEEN_THUMBS_UP);
        return getIntegration(userId, ability.getAbilityType(), ability.getAbilityId());
    }

    public int getNurseThumbsUpIntegration(long userId) {
        SpecificSocialAbility ability = thumbsUpTypeConverter.getItem(ThumbsUpAbilityTypeConverter.THUMBS_UP_OTHERS);
        return getIntegration(userId, ability.getAbilityType(), ability.getAbilityId());
    }

    private int getIntegration(long userId, SocialAbilityType abilityType, int abilityId) {
        List<NurseIntegrationEntity> entities = repository.findByUserIdAndAbilityTypeAndAbilityIdAndStatus(userId, abilityType, abilityId, CommonStatus.ENABLED);
        int integration = 0;
        if (entities != null) {
            for (NurseIntegrationEntity entity : entities) {
                integration += entity.getPoint();
            }
        }
        return integration;
    }

    public List<NurseIntegrationBean> getIntegrationSorted(long userId, UserType userType, CommonStatus status) {
        Sort sort = new Sort(
                new Sort.Order(Sort.Direction.ASC, "abilityType"),
                new Sort.Order(Sort.Direction.ASC, "abilityId"),
                new Sort.Order(Sort.Direction.ASC, "time")
        );
        List<NurseIntegrationEntity> entities = repository.findByStatusAndUserTypeAndUserId(status, userType, userId, sort);
        List<NurseIntegrationBean> beans = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(entities)) {
            NurseIntegrationBean bean;
            for (NurseIntegrationEntity entity : entities) {
                bean = beanConverter.convert(entity);
                beans.add(bean);
            }
        }
        return beans;
    }

    public int getIntegration(List<NurseIntegrationBean> integrationSorted, UserType userType, long userId, SocialAbilityType abilityType, int abilityId) {
        int integration = 0;
        if (integrationSorted != null) {
            for (NurseIntegrationBean bean : integrationSorted) {
                if (bean.getUserType().equals(userType) && bean.getUserId()==userId && bean.getAbilityType().equals(abilityType) && bean.getAbilityId()==abilityId) {
                    integration += bean.getPoint();
                }
            }
        }
        return integration;
    }

    /**
     * @param integrationSorted this must sort by userType ASC, userId ASC, abilityType ASC, abilityId ASC, time ASC
     * @param userType user type
     * @param userId user ID
     * @param abilityType {@see SocialAbilityType}
     * @param abilityId
     * @param pointToFetch  point to fetch
     */
    public Date firstTimeFetchPoint(List<NurseIntegrationBean> integrationSorted, UserType userType, long userId, SocialAbilityType abilityType, int abilityId,  long pointToFetch) {
        if (!VerifyUtil.isListEmpty(integrationSorted)) {
            long sum = 0;
            int size = integrationSorted.size();
            NurseIntegrationBean bean;
            for (int i=0; i < size; i ++) {
                bean = integrationSorted.get(i);
                if (bean.getUserType().equals(userType) && bean.getUserId()==userId && bean.getAbilityType().equals(abilityType) && bean.getAbilityId()==abilityId) {
                    sum += bean.getPoint();
                }
                if (sum >= pointToFetch) {
                    return bean.getTime();
                }
            }
        }
        return new Date();
    }

    public SpecificSocialAbility getSpeakTypePoint(SpeakType type) {
        if (!type2SpeakTypePoints.containsKey(type)) {
            SpecificSocialAbility speakType = speakTypeConverter.getItem(type);
            if (speakType != null) {
                type2SpeakTypePoints.put(type, speakType);
                id2SpeakTypePoints.put(speakType.getAbilityId(), speakType);
            }
        }
        return type2SpeakTypePoints.get(type);
    }

    public SpecificSocialAbility getSpeakTypePoint(int speakTypeId) {
        if (!id2SpeakTypePoints.containsKey(speakTypeId)) {
            SpecificSocialAbility speakType = speakTypeConverter.getItem(speakTypeId);
            if (speakType != null) {
                SpeakType type = (SpeakType) speakType.getProperty(SpecificSocialAbility.Speak_Type);
                type2SpeakTypePoints.put(type, speakType);
                id2SpeakTypePoints.put(speakType.getAbilityId(), speakType);
            }
        }
        return id2SpeakTypePoints.get(speakTypeId);
    }
}
