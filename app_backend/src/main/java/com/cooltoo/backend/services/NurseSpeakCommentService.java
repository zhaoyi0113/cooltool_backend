package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.converter.NurseSpeakCommentBeanConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseSpeakCommentEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSpeakCommentRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Lob;

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
    @Qualifier("StorageService")
    private StorageService storageService;
    @Autowired
    private NurseSpeakCommentBeanConverter beanConverter;

    //==================================================================
    //              GET
    //==================================================================
    public List<NurseSpeakCommentBean> getCommentByIds(String ids) {
        if (!VerifyUtil.isIds(ids)) {
            return new ArrayList<>();
        }
        String[]   strArray   = ids.split(",");
        List<Long> commentIds = new ArrayList<>();
        for (String tmp : strArray) {
            long id = Long.parseLong(tmp);
            commentIds.add(id);
        }
        return getCommentByIds(commentIds);
    }

    public List<NurseSpeakCommentBean> getCommentByIds(List<Long> commentIds) {
        if (null==commentIds || commentIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakCommentEntity> resultSet = commentRepository.findByIdIn(commentIds);

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
        List<NurseSpeakCommentEntity> comments = commentRepository.findByNurseSpeakId(nurseSpeakId, sort);
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
        List<NurseSpeakCommentEntity> comments = commentRepository.findByNurseSpeakIdIn(nurseSpeakIds, sort);
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

        List<Long> ids = new ArrayList<>();
        for (NurseSpeakCommentBean tmp : comments) {
            ids.add(tmp.getCommentMakerId());
            ids.add(tmp.getCommentReceiverId());
        }

        List<NurseEntity>      nurses   = nurseRepository.findByIdIn(ids);
        Map<Long, NurseEntity> id2Nurse = new HashMap<>();
        ids.clear();
        for (NurseEntity tmp : nurses) {
            id2Nurse.put(tmp.getId(), tmp);
            ids.add(tmp.getProfilePhotoId());
        }

        Map<Long, String>      id2Path  = storageService.getFilePath(ids);


        NurseEntity maker        = null;
        NurseEntity receiver     = null;
        String      makerName    = null;
        String      receiverName = null;
        String      makerHead    = null;
        String      receiverHead = null;
        for (NurseSpeakCommentBean tmp : comments) {
            maker        = id2Nurse.get(tmp.getCommentMakerId());
            receiver     = id2Nurse.get(tmp.getCommentReceiverId());
            makerName    = (null==maker)    ? null : maker.getName();
            receiverName = (null==receiver) ? null : receiver.getName();
            makerHead    = (null==maker)    ? null : id2Path.get(maker.getProfilePhotoId());
            receiverHead = (null==receiver) ? null : id2Path.get(receiver.getProfilePhotoId());
            tmp.setMakerName(makerName);
            tmp.setMakerHeadImageUrl(makerHead);
            tmp.setReceiverName(receiverName);
            tmp.setReceiverHeadImageUrl(receiverHead);
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

        List<NurseSpeakCommentEntity> comments = commentRepository.findByNurseSpeakIdIn(speakIds);
        if (null==comments || comments.isEmpty()) {
            logger.info("delete nothing");
        }

        commentRepository.delete(comments);

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

        List<Long> ids       = new ArrayList<>();
        String[]   strArrIds = strCommentIds.split(",");
        for (String tmp : strArrIds) {
            Long id = Long.parseLong(tmp);
            ids.add(id);
        }

        return deleteByIds(ids);
    }

    @Transactional
    public List<NurseSpeakCommentBean> deleteByIds(List<Long> commentIds) {
        logger.info("delete nurse speak comment by comment ids {}.", commentIds);
        if (null==commentIds || commentIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakCommentEntity> comments = commentRepository.findByIdIn(commentIds);
        if (null==comments || comments.isEmpty()) {
            logger.info("delete nothing");
        }

        commentRepository.delete(comments);


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
        entity = commentRepository.save(entity);
        return beanConverter.convert(entity);
    }
}
