package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseMessageBean;
import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.converter.NurseBeanConverter;
import com.cooltoo.backend.converter.NurseMessageBeanConverter;
import com.cooltoo.backend.converter.social_ability.SpeakAbilityTypeConverter;
import com.cooltoo.backend.entities.*;
import com.cooltoo.backend.repository.*;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhaolisong on 16/5/18.
 */
@Service("MessageService")
public class NurseMessageService {

    private static final Logger logger = LoggerFactory.getLogger(NurseMessageService.class.getName());
    private Map<Integer, SpecificSocialAbility> speakTypeId2Bean = new Hashtable<>();

    @Autowired
    private NurseMessageRepository repository;
    @Autowired
    private NurseMessageBeanConverter beanConverter;
    @Autowired
    private SpeakAbilityTypeConverter speakTypeConverter;
    @Autowired
    private NurseBeanConverter nurseBeanConverter;
    @Autowired
    private NurseRepository nurseRepo;
    @Autowired
    private NurseSpeakRepository speakRepo;
    @Autowired
    private NurseSpeakThumbsUpRepository thumbsUpRepo;
    @Autowired
    private NurseSpeakCommentRepository commentsRepo;
    //@Autowired
    //private NurseAbilityNominationRepository abilityNominateRepo;
    @Autowired
    private UserFileStorageService storageService;

    public long countMessageByStatus(UserType userType, long userId, String strStatuses) {
        logger.info("count message by userType={} userId={} status={}", userType, userId, strStatuses);
        List<SuggestionStatus> statuses = VerifyUtil.parseSuggestionStatuses(strStatuses);
        long count = repository.countByUserTypeAndUserIdAndStatusIn(userType, userId, statuses);
        logger.info("count is {}", count);
        return count;
    }

    @Transactional
    public void addMessage(long userId, UserType userType, SocialAbilityType abilityType, int abilityId, long reasonId) {
        logger.info("add message to userType={} userId={} abilityType={} abilityId={} reasonId={}", userType, userId, abilityType, abilityId, reasonId);
        try {
            boolean nurseExist = nurseRepo.exists(userId);
            if (nurseExist) {
                NurseMessageEntity entity = new NurseMessageEntity();
                entity.setUserId(userId);
                entity.setUserType(userType);
                entity.setAbilityType(abilityType);
                entity.setAbilityId(abilityId);
                entity.setReasonId(reasonId);
                entity.setTime(new Date());
                entity.setStatus(SuggestionStatus.UNREAD);
                repository.save(entity);
            }
            else {
                logger.error("can't find nurse " + userId);
            }
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void setMessageStatus(long messageId, String strStatus) {
        logger.info("set message={} to status={}", messageId, strStatus);
        SuggestionStatus status = SuggestionStatus.parseString(strStatus);
        NurseMessageEntity entity = repository.findOne(messageId);
        if (null!=entity && null!=status) {
            entity.setStatus(status);
            repository.save(entity);
            logger.info("message set", messageId, strStatus);
        }
        else {
            logger.info("no message set");
        }
    }

    public List<NurseMessageBean> getMessages(UserType userType, long userId, String strStatuses, int pageIndex, int size) {
        logger.info("get userType={} user={} message status={} at page={} size={}", userType, userId, strStatuses, pageIndex, size);
        PageRequest page = new PageRequest(pageIndex, size, Sort.Direction.DESC, "time");
        List<SuggestionStatus> statuses = VerifyUtil.parseSuggestionStatuses(strStatuses);
        Page<NurseMessageEntity> entities = repository.findByUserTypeAndUserIdAndStatusIn(userType, userId, statuses, page);
        List<NurseMessageBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("count size={}", beans.size());
        return beans;
    }

    public List<NurseMessageBean> getMessages(UserType userType, long userId, String strStatuses) {
        logger.info("get userType={} user={} message status={}", userType, userId, strStatuses);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "time"));
        List<SuggestionStatus> statuses = VerifyUtil.parseSuggestionStatuses(strStatuses);
        List<NurseMessageEntity> entities = repository.findByUserTypeAndUserIdAndStatusIn(userType, userId, statuses, sort);
        List<NurseMessageBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("count size={}", beans.size());
        return beans;
    }

    private List<NurseMessageBean> entitiesToBeans(Iterable<NurseMessageEntity> entities) {
        List<NurseMessageBean> beans = new ArrayList<>();
        if(null!=entities) {
            for (NurseMessageEntity entity : entities) {
                NurseMessageBean bean = beanConverter.convert(entity);
                beans.add(bean);
            }
        }
        return beans;
    }

    private void fillOtherProperties(List<NurseMessageBean> all) {
        if (VerifyUtil.isListEmpty(all)) {
            return;
        }

        // 获取 点赞 和 评论
        List<Long> thumbsUpIds = new ArrayList<>();
        List<Long> commentIds = new ArrayList<>();
        for (NurseMessageBean message : all) {
            if (SocialAbilityType.COMMENT.equals(message.getAbilityType())) {
                if (!commentIds.contains(message.getReasonId())) {
                    commentIds.add(message.getReasonId());
                }
            }
            else if (SocialAbilityType.THUMBS_UP.equals(message.getAbilityType())) {
                if (!thumbsUpIds.contains(message.getReasonId())) {
                    thumbsUpIds.add(message.getReasonId());
                }
            }
        }

        List<NurseSpeakThumbsUpEntity> thumbsUps = thumbsUpRepo.findAll(thumbsUpIds);
        List<NurseSpeakCommentEntity> comments = commentsRepo.findAll(commentIds);

        //获取 speak 和 用户
        List<Long> speakIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        if (null!=thumbsUps) {
            for (NurseSpeakThumbsUpEntity entity : thumbsUps) {
                if (!speakIds.contains(entity.getNurseSpeakId())) {
                    speakIds.add(entity.getNurseSpeakId());
                }
                if (!userIds.contains(entity.getThumbsUpUserId())) {
                    userIds.add(entity.getThumbsUpUserId());
                }
            }
        }
        if (null!=comments) {
            for (NurseSpeakCommentEntity entity : comments) {
                if (!speakIds.contains(entity.getNurseSpeakId())) {
                    speakIds.add(entity.getNurseSpeakId());
                }
                if (!userIds.contains(entity.getCommentMakerId())) {
                    userIds.add(entity.getCommentMakerId());
                }
            }
        }
        List<NurseSpeakEntity> speaks = speakRepo.findAll(speakIds);
        List<NurseEntity> users = nurseRepo.findAll(userIds);

        // 获取 Id --> entity/bean 映射
        Map<Long, NurseBean> userId2Bean = userIdToEntity(users);
        Map<Long, NurseSpeakEntity> speakId2Entity = speakIdToEntity(speaks);
        Map<Long, NurseSpeakThumbsUpEntity> thumbsUpId2Entity = thumbsUpIdToEntity(thumbsUps);
        Map<Long, NurseSpeakCommentEntity> commentId2Entity = commentIdToEntity(comments);

        // 设置 message 其他信息----- message发起者的名称和头像
        for (NurseMessageBean message : all) {
            if (SocialAbilityType.COMMENT.equals(message.getAbilityType())) {
                NurseSpeakCommentEntity comment = commentId2Entity.get(message.getReasonId());
                long userId = comment.getCommentMakerId();
                long speakId = comment.getNurseSpeakId();
                NurseBean user = userId2Bean.get(userId);
                NurseSpeakEntity speak = speakId2Entity.get(speakId);
                message.setReasonId(speakId); // 替换为 speak 表的 ID
                message.setAbilityId(speak.getSpeakType());  // 替换为 speak type 的 ID
                message.setAbilityType(SocialAbilityType.COMMUNITY);  // 替换为 speak 的 social ability type
                message.setContent(comment.getComment()); // 评论内容
                message.setUserId(userId);
                message.setUserName(user.getName());
                message.setProfileImageUrl(user.getProfilePhotoUrl());
                message.setType(MessageType.Comment);
            }
            else if (SocialAbilityType.THUMBS_UP.equals(message.getAbilityType())) {
                NurseSpeakThumbsUpEntity thumbsUp = thumbsUpId2Entity.get(message.getReasonId());
                long userId = thumbsUp.getThumbsUpUserId();
                long speakId = thumbsUp.getNurseSpeakId();
                NurseBean user = userId2Bean.get(userId);
                NurseSpeakEntity speak = speakId2Entity.get(speakId);
                message.setReasonId(speakId); // 替换为 speak 表的 ID
                message.setAbilityId(speak.getSpeakType());  // 替换为 speak type 的 ID
                message.setAbilityType(SocialAbilityType.COMMUNITY);  // 替换为 speak 的 social ability type
                message.setContent(speak.getContent()); // 发言内容
                message.setUserId(userId);
                message.setUserName(user.getName());
                message.setProfileImageUrl(user.getProfilePhotoUrl());
                message.setType(MessageType.ThumbsUp);
                message.setAbilityApproved(getSpeakTypeAbility(new Integer(speak.getSpeakType()))); //获取被赞的技能
                message.setAbilityName(null==message.getAbilityApproved() ? "" : message.getAbilityApproved().getAbilityName());
            }
        }
    }

    private Map<Long, NurseBean> userIdToEntity(List<NurseEntity> entities) {
        if (VerifyUtil.isListEmpty(entities)) {
            return new HashMap<>();
        }
        List<NurseBean> nurseBeans = new ArrayList<>();
        List<Long> imageIds = new ArrayList<>();
        for (NurseEntity entity : entities) {
            NurseBean bean = nurseBeanConverter.convert(entity);
            if (!imageIds.contains(bean.getProfilePhotoId())) {
                imageIds.add(bean.getProfilePhotoId());
            }
            nurseBeans.add(bean);
        }

        Map<Long, String> imageId2Url = storageService.getFilePath(imageIds);
        Map<Long, NurseBean> nurseId2Bean = new HashMap<>();
        for (NurseBean bean : nurseBeans) {
            String imageUrl = imageId2Url.get(bean.getProfilePhotoId());
            bean.setProfilePhotoUrl(imageUrl);
            nurseId2Bean.put(bean.getId(), bean);
        }

        return nurseId2Bean;
    }

    private Map<Long, NurseSpeakEntity> speakIdToEntity(List<NurseSpeakEntity> entities) {
        if (VerifyUtil.isListEmpty(entities)) {
            return new HashMap<>();
        }
        Map<Long, NurseSpeakEntity> speakId2Entity = new HashMap<>();
        for (NurseSpeakEntity bean : entities) {
            speakId2Entity.put(bean.getId(), bean);
        }

        return speakId2Entity;
    }

    private Map<Long, NurseSpeakThumbsUpEntity> thumbsUpIdToEntity(List<NurseSpeakThumbsUpEntity> entities) {
        if (VerifyUtil.isListEmpty(entities)) {
            return new HashMap<>();
        }
        Map<Long, NurseSpeakThumbsUpEntity> thumbsUpId2Entity = new HashMap<>();
        for (NurseSpeakThumbsUpEntity bean : entities) {
            thumbsUpId2Entity.put(bean.getId(), bean);
        }

        return thumbsUpId2Entity;
    }

    private Map<Long, NurseSpeakCommentEntity> commentIdToEntity(List<NurseSpeakCommentEntity> entities) {
        if (VerifyUtil.isListEmpty(entities)) {
            return new HashMap<>();
        }
        Map<Long, NurseSpeakCommentEntity> commentId2Entity = new HashMap<>();
        for (NurseSpeakCommentEntity bean : entities) {
            commentId2Entity.put(bean.getId(), bean);
        }

        return commentId2Entity;
    }

    public SpecificSocialAbility getSpeakTypeAbility(Integer speakTypeId) {
        if (!speakTypeId2Bean.containsKey(speakTypeId)) {
            SpecificSocialAbility speakType = speakTypeConverter.getItem(speakTypeId);
            if (speakType != null) {
                speakTypeId2Bean.put(speakTypeId, speakType);
            }
        }
        return speakTypeId2Bean.get(speakTypeId);
    }
}
