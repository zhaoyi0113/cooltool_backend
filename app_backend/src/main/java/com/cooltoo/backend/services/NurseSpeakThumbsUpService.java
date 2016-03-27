package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.converter.NurseSpeakThumbsUpBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakThumbsUpEntity;
import com.cooltoo.backend.repository.NurseSpeakThumbsUpRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2016/3/18.
 */
@Service("NurseSpeakThumbsUpService")
public class NurseSpeakThumbsUpService {

    @Autowired
    NurseSpeakThumbsUpRepository thumbsUpRepository;

    @Autowired
    NurseSpeakThumbsUpBeanConverter beanConverter;

    public NurseSpeakThumbsUpBean addSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        NurseSpeakThumbsUpEntity thumbsUpEntity = thumbsUpRepository.findNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(nurseSpeakId, thumbsUpUserId);
        if (null!=thumbsUpEntity) {
            throw new BadRequestException(ErrorCode.SPEAK_THUMBS_UP_EXIST);
        }
        thumbsUpEntity = new NurseSpeakThumbsUpEntity();
        thumbsUpEntity.setNurseSpeakId(nurseSpeakId);
        thumbsUpEntity.setThumbsUpUserId(thumbsUpUserId);
        thumbsUpEntity.setTime(new Date());
        thumbsUpEntity = thumbsUpRepository.save(thumbsUpEntity);
        return beanConverter.convert(thumbsUpEntity);
    }

    public List<NurseSpeakThumbsUpBean> getSpeakThumbsUpByNurseSpeakId(long nurseSpeakId) {
        List<NurseSpeakThumbsUpEntity> thumbsUpEntities = thumbsUpRepository.findNurseSpeakThumbsUpByNurseSpeakId(nurseSpeakId);
        List<NurseSpeakThumbsUpBean> thumbsUpBeans = new ArrayList<NurseSpeakThumbsUpBean>();
        for (NurseSpeakThumbsUpEntity thumbsUpEntity : thumbsUpEntities) {
            NurseSpeakThumbsUpBean thumbsUpBean = beanConverter.convert(thumbsUpEntity);
            thumbsUpBeans.add(thumbsUpBean);
        }
        return thumbsUpBeans;
    }

    public List<NurseSpeakThumbsUpBean> getSpeakThumbsUpByNurseSpeakIds(List<Long> nurseSpeakIds) {
        if (null==nurseSpeakIds) {
            return new ArrayList<NurseSpeakThumbsUpBean>();
        }

        // get speak thumbsup
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "time"));
        List<NurseSpeakThumbsUpEntity> thumbsUpEntities = thumbsUpRepository.findThumbsUpByNurseSpeakIdIn(nurseSpeakIds, sort);
        List<NurseSpeakThumbsUpBean> thumbsUpBeans = new ArrayList<NurseSpeakThumbsUpBean>();
        for (NurseSpeakThumbsUpEntity thumbsUpEntity : thumbsUpEntities) {
            NurseSpeakThumbsUpBean thumbsUpBean = beanConverter.convert(thumbsUpEntity);
            thumbsUpBeans.add(thumbsUpBean);
        }
        return thumbsUpBeans;
    }

    public NurseSpeakThumbsUpBean findNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        NurseSpeakThumbsUpEntity thumbsUpEntity = thumbsUpRepository.findNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(nurseSpeakId, thumbsUpUserId);
        if (null==thumbsUpEntity) {
            throw new BadRequestException(ErrorCode.SPEAK_THUMBS_UP_NOT_EXIST);
        }
        return beanConverter.convert(thumbsUpEntity);
    }

    public void deleteNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        //Notice: this method will throw Exception when Http testing(But JUnit Testing is passed):
        //      org.springframework.dao.InvalidDataAccessApiUsageException: No EntityManager with actual transaction available for current thread - cannot reliably process 'remove' call;
//        thumbsUpRepository.deleteNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(nurseSpeakId, thumbsUpUserId);
        NurseSpeakThumbsUpEntity thumbsUpEntity = thumbsUpRepository.findNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(nurseSpeakId, thumbsUpUserId);
        if (null!=thumbsUpEntity) {
            thumbsUpRepository.delete(thumbsUpEntity);
        }
    }
}
