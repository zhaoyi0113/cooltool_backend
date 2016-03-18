package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.converter.NurseSpeakCommentBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakCommentEntity;
import com.cooltoo.backend.repository.NurseSpeakCommentRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Test111 on 2016/3/18.
 */
@Service("NurseSpeakCommentService")
public class NurseSpeakCommentService {
    private static final Logger logger = Logger.getLogger(NurseSpeakCommentService.class.getName());

    @Autowired
    private NurseSpeakCommentRepository commentRepository;

    @Autowired
    private NurseSpeakCommentBeanConverter beanConverter;

    public NurseSpeakCommentBean addSpeakComment(long nurseSpeakId, long commentMakerId, long commentReceiverId, String comment) {
        if (nurseSpeakId <= 0) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_NOT_EXIST);
        }
        if (commentMakerId <= 0) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        if (commentReceiverId < 0) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
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

    public List<NurseSpeakCommentBean> getSpeakCommentsByNurseSpeakId(long nurseSpeakId) {
        if (nurseSpeakId < 0) {
            throw new BadRequestException(ErrorCode.SPEAK_CONTENT_NOT_EXIST);
        }
        Sort sort = new Sort(Sort.Direction.ASC, "time");
        List<NurseSpeakCommentEntity> comments = commentRepository.findNurseSpeakCommentByNurseSpeakId(nurseSpeakId, sort);
        List<NurseSpeakCommentBean> retValue = new ArrayList<NurseSpeakCommentBean>();

        int count = null==comments ? 0 : comments.size();
        for (int i = 0; i < count; i ++) {
            NurseSpeakCommentEntity comment = comments.get(i);
            if (null==comment) {
                continue;
            }
            retValue.add(beanConverter.convert(comment));
        }
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
        List<NurseSpeakCommentEntity> comments = commentRepository.findNurseSpeakCommentByNurseSpeakIdIn(nurseSpeakIds, sort);
        List<NurseSpeakCommentBean> retValue = new ArrayList<NurseSpeakCommentBean>();

        int count = null==comments ? 0 : comments.size();
        for (int i = 0; i < count; i ++) {
            NurseSpeakCommentEntity comment = comments.get(i);
            if (null==comment) {
                continue;
            }
            retValue.add(beanConverter.convert(comment));
        }
        return retValue;
    }
}
