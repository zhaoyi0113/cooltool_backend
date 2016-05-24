package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.converter.NurseSpeakCommentBeanConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseSpeakCommentEntity;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSpeakCommentRepository;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Test111 on 2016/3/18.
 */
@Service("NurseSpeakCommentService")
public class NurseSpeakCommentService {
    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakCommentService.class.getName());

    @Autowired
    private NurseSpeakCommentRepository commentRepository;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private NurseSpeakRepository speakRepository;
    @Autowired
    @Qualifier("UserFileStorageService")
    private UserFileStorageService userStorage;
    @Autowired
    private NurseSpeakCommentBeanConverter beanConverter;

    //==================================================================
    //              GET
    //==================================================================
    public List<Long> findSpeakWithCommentUserMake(long nurseId) {
        List<Long> speaks = commentRepository.findSpeakWithCommentUserMake(nurseId, CommonStatus.ENABLED);
        if (VerifyUtil.isListEmpty(speaks)) {
            logger.info("speak with comment user={} made is empty", nurseId);
            return new ArrayList<>();
        }
        logger.info("speak with comment user={} made, size={}", nurseId, speaks.size());
        return speaks;
    }

    public List<NurseSpeakCommentBean> getCommentByIds(String ids) {
        if (!VerifyUtil.isIds(ids)) {
            return new ArrayList<>();
        }
        List<Long> commentIds = VerifyUtil.parseLongIds(ids);
        return getCommentByIds(commentIds);
    }

    public List<NurseSpeakCommentBean> getCommentByIds(List<Long> commentIds) {
        if (null==commentIds || commentIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakCommentEntity> resultSet = commentRepository.findByStatusAndIdIn(CommonStatus.ENABLED, commentIds);

        List<NurseSpeakCommentBean>   comments  = new ArrayList<>();
        for (NurseSpeakCommentEntity tmp : resultSet) {
            NurseSpeakCommentBean bean = beanConverter.convert(tmp);
            comments.add(bean);
        }

        return comments;
    }

    public List<NurseSpeakCommentBean> getSpeakCommentsByNurseSpeakId(long nurseSpeakId) {
        if (nurseSpeakId < 0) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_NOT_EXIST);
        }
        Sort sort = new Sort(Sort.Direction.ASC, "time");
        List<NurseSpeakCommentEntity> comments = commentRepository.findByStatusAndNurseSpeakId(CommonStatus.ENABLED, nurseSpeakId, sort);
        List<NurseSpeakCommentBean> retValue = new ArrayList<NurseSpeakCommentBean>();

        int count = null==comments ? 0 : comments.size();
        for (int i = 0; i < count; i ++) {
            NurseSpeakCommentEntity comment = comments.get(i);
            if (null==comment) {
                continue;
            }
            retValue.add(beanConverter.convert(comment));
        }

        fillOtherProperties(retValue);
        return retValue;
    }

    public List<NurseSpeakCommentBean> getSpeakCommentsByNurseSpeakIds(List<Long> nurseSpeakIds) {
        if (null==nurseSpeakIds || nurseSpeakIds.isEmpty()) {
            return new ArrayList<NurseSpeakCommentBean>();
        }
        for (Long nurseSpeakId : nurseSpeakIds) {
            if (nurseSpeakId < 0) {
                throw new BadRequestException(ErrorCode.SPEAK_CONTENT_NOT_EXIST);
            }
        }
        Sort.Order speakIdOrder = new Sort.Order(Sort.Direction.DESC, "nurseSpeakId");
        Sort.Order timeOrder = new Sort.Order(Sort.Direction.ASC, "time");
        Sort sort = new Sort(speakIdOrder, timeOrder);
        List<NurseSpeakCommentEntity> comments = commentRepository.findByStatusAndNurseSpeakIdIn(CommonStatus.ENABLED, nurseSpeakIds, sort);
        List<NurseSpeakCommentBean> retValue = new ArrayList<NurseSpeakCommentBean>();

        int count = null==comments ? 0 : comments.size();
        for (int i = 0; i < count; i ++) {
            NurseSpeakCommentEntity comment = comments.get(i);
            if (null==comment) {
                continue;
            }
            retValue.add(beanConverter.convert(comment));
        }

        fillOtherProperties(retValue);
        return retValue;
    }

    private void fillOtherProperties(List<NurseSpeakCommentBean> comments) {
        if (null==comments || comments.isEmpty()) {
            return;
        }

        List<Long> speakIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (NurseSpeakCommentBean tmp : comments) {
            userIds.add(tmp.getCommentMakerId());
            userIds.add(tmp.getCommentReceiverId());
            speakIds.add(tmp.getNurseSpeakId());
        }

        Map<Long, NurseSpeakEntity> id2Speak = new HashMap<>();
        List<NurseSpeakEntity> speaks = speakRepository.findAll(speakIds);
        for (NurseSpeakEntity tmp : speaks) {
            id2Speak.put(tmp.getId(), tmp);
        }

        List<NurseEntity>      nurses   = nurseRepository.findByIdIn(userIds);
        Map<Long, NurseEntity> id2Nurse = new HashMap<>();
        List<Long> imageIds = new ArrayList<>();
        for (NurseEntity tmp : nurses) {
            id2Nurse.put(tmp.getId(), tmp);
            imageIds.add(tmp.getProfilePhotoId());
        }

        Map<Long, String>      id2Path  = userStorage.getFilePath(imageIds);

        NurseSpeakEntity speak   = null;
        NurseEntity maker        = null;
        NurseEntity receiver     = null;
        String      makerName    = null;
        String      receiverName = null;
        String      makerHead    = null;
        String      receiverHead = null;
        for (NurseSpeakCommentBean tmp : comments) {
            maker        = id2Nurse.get(tmp.getCommentMakerId());
            receiver     = id2Nurse.get(tmp.getCommentReceiverId());
            speak        = id2Speak.get(tmp.getNurseSpeakId());
            makerName    = (null==maker)    ? null : maker.getName();
            receiverName = (null==receiver) ? null : receiver.getName();
            makerHead    = (null==maker)    ? null : id2Path.get(maker.getProfilePhotoId());
            receiverHead = (null==receiver) ? null : id2Path.get(receiver.getProfilePhotoId());
            tmp.setMakerName(makerName);
            tmp.setMakerHeadImageUrl(makerHead);
            tmp.setReceiverName(receiverName);
            tmp.setReceiverHeadImageUrl(receiverHead);
            tmp.setSpeakMakerId(speak.getUserId());
            tmp.setNurseSpeakTypeId(speak.getSpeakType());
        }
    }

    //==================================================================
    //              delete
    //==================================================================

    @Transactional
    public List<NurseSpeakCommentBean> deleteBySpeakIds(List<Long> speakIds) {
        logger.info("delete nurse speak comment by speak ids {}.", speakIds);
        if (null==speakIds || speakIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakCommentEntity> comments = commentRepository.findByStatusAndNurseSpeakIdIn(CommonStatus.ENABLED, speakIds);
        if (null==comments || comments.isEmpty()) {
            logger.info("delete nothing");
        }
        else {
            for (NurseSpeakCommentEntity comment : comments) {
                comment.setStatus(CommonStatus.DELETED);
            }
            commentRepository.save(comments);
        }


        List<NurseSpeakCommentBean> retValue = new ArrayList<>();
        for (NurseSpeakCommentEntity tmp : comments) {
            NurseSpeakCommentBean comment = beanConverter.convert(tmp);
            retValue.add(comment);
        }
        return retValue;
    }

    @Transactional
    public List<NurseSpeakCommentBean> deleteByIds(String strCommentIds) {
        logger.info("delete nurse speak comment by comment ids {}.", strCommentIds);
        if (!VerifyUtil.isIds(strCommentIds)) {
            logger.warn("comment ids are invalid");
            return new ArrayList<>();
        }

        List<Long> ids = VerifyUtil.parseLongIds(strCommentIds);
        return deleteByIds(ids);
    }

    @Transactional
    public List<NurseSpeakCommentBean> deleteByIds(List<Long> commentIds) {
        logger.info("delete nurse speak comment by comment ids {}.", commentIds);
        if (null==commentIds || commentIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakCommentEntity> comments = commentRepository.findByStatusAndIdIn(CommonStatus.ENABLED, commentIds);
        if (null==comments || comments.isEmpty()) {
            logger.info("delete nothing");
        }
        else {
            for (NurseSpeakCommentEntity comment : comments) {
                comment.setStatus(CommonStatus.DELETED);
            }
            commentRepository.save(comments);
        }

        List<NurseSpeakCommentBean> retValue = new ArrayList<>();
        for (NurseSpeakCommentEntity tmp : comments) {
            NurseSpeakCommentBean comment = beanConverter.convert(tmp);
            retValue.add(comment);
        }
        return retValue;
    }

    //==================================================================
    //              add
    //==================================================================
    @Transactional
    public NurseSpeakCommentBean addSpeakComment(long nurseSpeakId, long commentMakerId, long commentReceiverId, String comment) {
        if (nurseSpeakId <= 0) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_NOT_EXIST);
        }
        if (commentMakerId <= 0) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        if (commentReceiverId < 0) {
            commentReceiverId = 0;
        }
        if (null==comment || "".equals(comment)) {
            throw new BadRequestException(ErrorCode.SPEAK_COMMENT_NOT_EXIST);
        }
        NurseSpeakCommentEntity entity = new NurseSpeakCommentEntity();
        entity.setNurseSpeakId(nurseSpeakId);
        entity.setCommentMakerId(commentMakerId);
        entity.setCommentReceiverId(commentReceiverId);
        entity.setComment(comment);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = commentRepository.save(entity);

        NurseSpeakCommentBean commentBean = beanConverter.convert(entity);
        NurseSpeakEntity speak = speakRepository.getOne(nurseSpeakId);
        commentBean.setSpeakMakerId(speak.getUserId());
        commentBean.setNurseSpeakTypeId(speak.getSpeakType());
        return commentBean;
    }
}
