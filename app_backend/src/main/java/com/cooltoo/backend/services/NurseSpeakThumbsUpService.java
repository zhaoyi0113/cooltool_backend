package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.converter.NurseSpeakThumbsUpBeanConverter;
import com.cooltoo.backend.entities.NurseSpeakThumbsUpEntity;
import com.cooltoo.backend.repository.NurseSpeakThumbsUpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakThumbsUpService.class.getName());

    @Autowired
    NurseSpeakThumbsUpRepository thumbsUpRepository;

    @Autowired
    NurseSpeakThumbsUpBeanConverter beanConverter;

    public NurseSpeakThumbsUpBean addSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        NurseSpeakThumbsUpEntity thumbsUpEntity = null;
        List<NurseSpeakThumbsUpEntity> thumbsUpEntitys = thumbsUpRepository.findByNurseSpeakIdAndThumbsUpUserId(nurseSpeakId, thumbsUpUserId);
        if (null!=thumbsUpEntitys && !thumbsUpEntitys.isEmpty()) {
            thumbsUpEntity = thumbsUpEntitys.get(0);
            thumbsUpRepository.delete(thumbsUpEntity);
        }
        else {
            thumbsUpEntity = new NurseSpeakThumbsUpEntity();
            thumbsUpEntity.setNurseSpeakId(nurseSpeakId);
            thumbsUpEntity.setThumbsUpUserId(thumbsUpUserId);
            thumbsUpEntity.setTime(new Date());
            thumbsUpEntity = thumbsUpRepository.save(thumbsUpEntity);
        }
        NurseSpeakThumbsUpBean bean = beanConverter.convert(thumbsUpEntity);
        long count = thumbsUpRepository.countByNurseSpeakId(nurseSpeakId);
        bean.setSpeakThumbsUpCount(count);
        return bean;
    }

    public List<NurseSpeakThumbsUpBean> getSpeakThumbsUpByNurseSpeakId(long nurseSpeakId) {
        List<NurseSpeakThumbsUpEntity> thumbsUpEntities = thumbsUpRepository.findUpByNurseSpeakId(nurseSpeakId);
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
        List<NurseSpeakThumbsUpEntity> thumbsUpEntities = thumbsUpRepository.findByNurseSpeakIdIn(nurseSpeakIds, sort);
        List<NurseSpeakThumbsUpBean> thumbsUpBeans = new ArrayList<NurseSpeakThumbsUpBean>();
        for (NurseSpeakThumbsUpEntity thumbsUpEntity : thumbsUpEntities) {
            NurseSpeakThumbsUpBean thumbsUpBean = beanConverter.convert(thumbsUpEntity);
            thumbsUpBeans.add(thumbsUpBean);
        }
        return thumbsUpBeans;
    }

    public NurseSpeakThumbsUpBean findNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        List<NurseSpeakThumbsUpEntity> thumbsUpEntity = thumbsUpRepository.findByNurseSpeakIdAndThumbsUpUserId(nurseSpeakId, thumbsUpUserId);
        if (null==thumbsUpEntity || thumbsUpEntity.isEmpty()) {
            logger.info("the user {} not thumbs up the speak {} content.", thumbsUpUserId, nurseSpeakId);
            return null;
        }
        return beanConverter.convert(thumbsUpEntity.get(0));
    }
}
