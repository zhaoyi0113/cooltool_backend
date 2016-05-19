package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.MessageBean;
import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.entities.*;
import com.cooltoo.backend.repository.*;
import com.cooltoo.constants.MessageType;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhaolisong on 16/5/18.
 */
@Service("MessageService")
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class.getName());

    @Autowired
    private NurseRepository nurseRepo;
    @Autowired
    private SpeakTypeService speakTypeSrv;
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

    public List<MessageBean> getMessages(long userId, int page, int size) {
        logger.info("get user={} message at page={} size={}", userId, page, size);
        int fetchPage = 0;
        int fetchSize = page*size + size;

        Map<Long, NurseSpeakEntity>    id2Entity  = getSpeaks(userId);
        PageRequest condition = new PageRequest(fetchPage, fetchSize, Sort.Direction.DESC, "time", "thumbsUpUserId");
        Page<NurseSpeakThumbsUpEntity> thumbsUps  = thumbsUpRepo.findByNurseSpeakIdIn(id2Entity.keySet(), condition);
        condition = new PageRequest(fetchPage, fetchSize, Sort.Direction.DESC, "time", "commentMakerId");
        Page<NurseSpeakCommentEntity>  comments   = commentsRepo.findByReceiverIdAndSpeakIdIn(userId, id2Entity.keySet(), condition);

        List<MessageBean> allMsg = new ArrayList<>();
        thumbsUpToMsgBean(id2Entity, thumbsUps, allMsg);
        commentToMsgBean(id2Entity, comments, allMsg);

        int fetchFromIdx = page*size;
        int fetchToIdx = fetchSize;
        int count = allMsg.size();
        if (count <= fetchFromIdx) {
            return new ArrayList<>();
        }
        List<MessageBean> retMsg = new ArrayList<>();
        for (int i=fetchFromIdx; i < count; i ++) {
            if (i < fetchToIdx) {
                retMsg.add(allMsg.get(i));
            }
        }
        fillOtherProperties(retMsg);
        logger.info("count size={}", retMsg.size());
        return retMsg;
    }

    private Map<Long, NurseSpeakEntity> getSpeaks(long userId) {
        List<NurseSpeakEntity> speaks = speakRepo.findByUserId(userId);
        if (null==speaks) {
            return new HashMap<>();
        }
        Map<Long, NurseSpeakEntity> id2Speak = new HashMap<>();
        for (NurseSpeakEntity entity : speaks) {
            id2Speak.put(entity.getId(), entity);
        }
        return id2Speak;
    }

    private List<MessageBean> thumbsUpToMsgBean(Map<Long, NurseSpeakEntity> id2Entity,
                                                Iterable<NurseSpeakThumbsUpEntity> thumbsUps,
                                                List<MessageBean> allMsg) {
        if (null==allMsg) {
            allMsg = new ArrayList<>();
        }
        if (null==thumbsUps) {
            return allMsg;
        }
        for (NurseSpeakThumbsUpEntity thumbsUp : thumbsUps) {
            NurseSpeakEntity speak = id2Entity.get(thumbsUp.getNurseSpeakId());
            MessageBean bean = new MessageBean();
            bean.setId(thumbsUp.getNurseSpeakId());
            bean.setContent(speak.getContent());
            bean.setType(MessageType.ThumbsUp);
            bean.setTime(thumbsUp.getTime());
            bean.setUserId(thumbsUp.getThumbsUpUserId());
            //bean.setUserName();
            //bean.setProfileImageUrl();
            bean.setAbilityId(speak.getSpeakType());
            bean.setAbilityType(SocialAbilityType.COMMUNITY);
            //bean.setAbilityName();
            allMsg.add(bean);
        }
        return allMsg;
    }

    private List<MessageBean> commentToMsgBean(Map<Long, NurseSpeakEntity> id2Entity,
                                               Iterable<NurseSpeakCommentEntity> comments,
                                               List<MessageBean> allMsg) {
        if (null==allMsg) {
            allMsg = new ArrayList<>();
        }
        if (null==comments) {
            return allMsg;
        }
        for (NurseSpeakCommentEntity comment : comments) {
            NurseSpeakEntity speak = id2Entity.get(comment.getNurseSpeakId());
            MessageBean bean = new MessageBean();
            bean.setId(comment.getNurseSpeakId());
            bean.setContent(speak.getContent());
            bean.setType(MessageType.Comment);
            bean.setTime(comment.getTime());
            bean.setUserId(comment.getCommentMakerId());
            //bean.setUserName();
            //bean.setProfileImageUrl();
            bean.setAbilityId(speak.getSpeakType());
            bean.setAbilityType(SocialAbilityType.COMMUNITY);
            //bean.setAbilityName();
            allMsg.add(bean);
        }
        return allMsg;
    }

    private void fillOtherProperties(List<MessageBean> all) {
        if (VerifyUtil.isListEmpty(all)) {
            return;
        }
        all.sort(new Comparator<MessageBean>(){
            @Override
            public int compare(MessageBean o1, MessageBean o2) {
                int delta = o1.getTime().compareTo(o2.getTime());
                return -delta;
            }
        });

        // 设置 user 名称头像
        List<Long> userIds  = new ArrayList<>();
        for (MessageBean msg : all) {
            long nurseId = msg.getUserId();
            if (!userIds.contains(nurseId)) {
                userIds.add(nurseId);
            }
        }
        List<NurseEntity> nurses = nurseRepo.findByIdIn(userIds);

        Map<Long, NurseEntity> userId2Entity = new HashMap<>();
        List<Long> imageIds = new ArrayList<>();
        for (NurseEntity entity :nurses) {
            long imageId = entity.getProfilePhotoId();
            userId2Entity.put(entity.getId(), entity);
            if (!imageIds.contains(imageId)) {
                imageIds.add(imageId);
            }
        }

        Map<Long, String> imageId2Path = storageService.getFilePath(imageIds);
        for (MessageBean msg : all) {
            NurseEntity nurse    = userId2Entity.get(msg.getUserId());
            String      imageUrl = imageId2Path.get(nurse.getProfilePhotoId());
            msg.setUserName(nurse.getName());
            msg.setProfileImageUrl(imageUrl);
        }

        // 设置speak类型
        List<SpeakTypeBean> speakTypes = speakTypeSrv.getAllSpeakType();
        for (MessageBean msg : all) {
            if (msg.getAbilityType()==SocialAbilityType.COMMUNITY) {
                for (SpeakTypeBean type : speakTypes) {
                    if (msg.getAbilityId() == type.getId()) {
                        msg.setAbilityName(type.getName());
                    }
                }
            }
        }

        // 返回所有的Message
    }
}
