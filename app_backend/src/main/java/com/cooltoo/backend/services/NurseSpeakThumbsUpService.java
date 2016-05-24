package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.beans.NurseSpeakThumbsUpBean;
import com.cooltoo.backend.converter.NurseSpeakThumbsUpBeanConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.backend.entities.NurseSpeakThumbsUpEntity;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.NurseSpeakRepository;
import com.cooltoo.backend.repository.NurseSpeakThumbsUpRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.services.file.UserFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hp on 2016/3/18.
 */
@Service("NurseSpeakThumbsUpService")
public class NurseSpeakThumbsUpService {

    private static final Logger logger = LoggerFactory.getLogger(NurseSpeakThumbsUpService.class.getName());

    @Autowired
    private NurseSpeakRepository speakRepository;
    @Autowired
    private NurseSpeakThumbsUpRepository thumbsUpRepository;
    @Autowired
    private NurseRepository              nurseRepository;
    @Autowired
    private NurseFriendsService          friendsService;
    @Autowired
    @Qualifier("UserFileStorageService")
    private UserFileStorageService       userStorage;
    @Autowired
    private NurseSpeakThumbsUpBeanConverter beanConverter;

    //============================================================
    //          get
    //============================================================

    public long countUserThumbsUpOthers(long nurseId) {
        long count = thumbsUpRepository.countUserThumbsUpOthers(nurseId, CommonStatus.ENABLED);
        logger.info("count user thumbs up others speak={}", count);
        return count;
    }

    public long countOthersThumbsUpUser(long nurseId) {
        long count = thumbsUpRepository.countOthersThumbsUpUser(nurseId, CommonStatus.ENABLED);
        logger.info("count others thumbs up user speak={}", count);
        return count;
    }

    public List<NurseSpeakThumbsUpBean> getSpeakThumbsUpByNurseSpeakId(long nurseSpeakId) {
        List<NurseSpeakThumbsUpEntity> thumbsUpEntities = thumbsUpRepository.findByStatusAndNurseSpeakId(CommonStatus.ENABLED, nurseSpeakId);

        List<NurseSpeakThumbsUpBean> thumbsUpBeans = new ArrayList<NurseSpeakThumbsUpBean>();
        for (NurseSpeakThumbsUpEntity thumbsUpEntity : thumbsUpEntities) {
            NurseSpeakThumbsUpBean thumbsUpBean = beanConverter.convert(thumbsUpEntity);
            thumbsUpBeans.add(thumbsUpBean);
        }

        fillOtherProperties(thumbsUpBeans);
        return thumbsUpBeans;
    }

    public List<NurseFriendsBean> getThumbsUpUsers(long currentUserId, long speakId) {
        List<Long> thumbsUpUsers = thumbsUpRepository.findThumbsupUserId(speakId, CommonStatus.ENABLED);
        if (VerifyUtil.isListEmpty(thumbsUpUsers)) {
            return new ArrayList<>();
        }

        return friendsService.getFriendship(currentUserId, thumbsUpUsers);
    }

    public List<NurseSpeakThumbsUpBean> getSpeakThumbsUpByNurseSpeakIds(List<Long> nurseSpeakIds) {
        if (null==nurseSpeakIds) {
            return new ArrayList<NurseSpeakThumbsUpBean>();
        }

        // get speak thumbsup
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "time"));
        List<NurseSpeakThumbsUpEntity> thumbsUpEntities = thumbsUpRepository.findByStatusAndNurseSpeakIdIn(CommonStatus.ENABLED, nurseSpeakIds, sort);

        List<NurseSpeakThumbsUpBean> thumbsUpBeans = new ArrayList<NurseSpeakThumbsUpBean>();
        for (NurseSpeakThumbsUpEntity thumbsUpEntity : thumbsUpEntities) {
            NurseSpeakThumbsUpBean thumbsUpBean = beanConverter.convert(thumbsUpEntity);
            thumbsUpBeans.add(thumbsUpBean);
        }

        fillOtherProperties(thumbsUpBeans);
        return thumbsUpBeans;
    }

    public NurseSpeakThumbsUpBean findNurseSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        List<NurseSpeakThumbsUpEntity> thumbsUpEntities = thumbsUpRepository.findByStatusAndNurseSpeakIdAndThumbsUpUserId(CommonStatus.ENABLED, nurseSpeakId, thumbsUpUserId);
        if (null==thumbsUpEntities || thumbsUpEntities.isEmpty()) {
            logger.info("the user {} not thumbs up the speak {} content.", thumbsUpUserId, nurseSpeakId);
            return null;
        }
        List<NurseSpeakThumbsUpBean>  thumbsUpBeans = new ArrayList<NurseSpeakThumbsUpBean>();
        for (NurseSpeakThumbsUpEntity thumbsUpEntity : thumbsUpEntities) {
            NurseSpeakThumbsUpBean thumbsUpBean = beanConverter.convert(thumbsUpEntity);
            thumbsUpBeans.add(thumbsUpBean);
        }

        fillOtherProperties(thumbsUpBeans);
        return thumbsUpBeans.get(0);
    }

    private void fillOtherProperties(List<NurseSpeakThumbsUpBean> thumbsUps) {
        if (null==thumbsUps || thumbsUps.isEmpty()) {
            return;
        }
        List<Long> speakIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (NurseSpeakThumbsUpBean tmp : thumbsUps) {
            userIds.add(tmp.getThumbsUpUserId());
            if (!speakIds.contains(tmp.getNurseSpeakId())) {
                speakIds.add(tmp.getNurseSpeakId());
            }
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

        NurseEntity maker        = null;
        NurseSpeakEntity speak   = null;
        String      makerName    = null;
        String      makerHead    = null;
        for (NurseSpeakThumbsUpBean tmp : thumbsUps) {
            speak     = id2Speak.get(tmp.getNurseSpeakId());
            maker     = id2Nurse.get(tmp.getThumbsUpUserId());
            makerName = (null==maker)    ? null : maker.getName();
            makerHead = (null==maker)    ? null : id2Path.get(maker.getProfilePhotoId());
            tmp.setThumbsUpUserName(makerName);
            tmp.setThumbsUpUserHeadImageUrl(makerHead);
            if (speak!=null) {
                tmp.setUserIdBeenThumbsUp(speak.getUserId());
            }
            tmp.setThumbsUpAddOrDelete("");
        }
    }

    //============================================================
    //          add / delete
    //============================================================

    @Transactional
    public NurseSpeakThumbsUpBean setSpeakThumbsUp(long nurseSpeakId, long thumbsUpUserId) {
        NurseSpeakThumbsUpEntity thumbsUpEntity = null;
        NurseSpeakEntity speak = speakRepository.getOne(nurseSpeakId);
        String addOrDelete = null;
        List<NurseSpeakThumbsUpEntity> thumbsUpEntitys = thumbsUpRepository.findByStatusAndNurseSpeakIdAndThumbsUpUserId(CommonStatus.ENABLED, nurseSpeakId, thumbsUpUserId);
        if (null!=thumbsUpEntitys && !thumbsUpEntitys.isEmpty()) {
            thumbsUpEntity = thumbsUpEntitys.get(0);
            for (NurseSpeakThumbsUpEntity thumbsUp : thumbsUpEntitys) {
                thumbsUp.setStatus(CommonStatus.DELETED);
            }
            thumbsUpRepository.save(thumbsUpEntitys);
            addOrDelete = NurseSpeakThumbsUpBean.THUMBS_UP_DELETE;
        }
        else {
            thumbsUpEntity = new NurseSpeakThumbsUpEntity();
            thumbsUpEntity.setNurseSpeakId(nurseSpeakId);
            thumbsUpEntity.setThumbsUpUserId(thumbsUpUserId);
            thumbsUpEntity.setTime(new Date());
            thumbsUpEntity.setStatus(CommonStatus.ENABLED);
            thumbsUpEntity = thumbsUpRepository.save(thumbsUpEntity);
            addOrDelete = NurseSpeakThumbsUpBean.THUMBS_UP_ADD;
        }
        NurseSpeakThumbsUpBean bean = beanConverter.convert(thumbsUpEntity);
        bean.setUserIdBeenThumbsUp(null!=speak ? speak.getUserId() : 0);
        bean.setThumbsUpAddOrDelete(addOrDelete);
        long count = thumbsUpRepository.countByNurseSpeakId(nurseSpeakId);
        bean.setSpeakThumbsUpCount(count);
        return bean;
    }

    //============================================================
    //           delete
    //============================================================


    @Transactional
    public List<NurseSpeakThumbsUpBean> deleteBySpeakIds(List<Long> speakIds) {
        logger.info("delete nurse speak thumbs up by speak ids {}.", speakIds);
        if (null==speakIds || speakIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseSpeakThumbsUpEntity> thumbsUps = thumbsUpRepository.findByStatusAndNurseSpeakIdIn(CommonStatus.ENABLED, speakIds);
        if (null==thumbsUps || thumbsUps.isEmpty()) {
            logger.info("delete nothing");
        }
        else {
            for (NurseSpeakThumbsUpEntity thumbsUp : thumbsUps) {
                thumbsUp.setStatus(CommonStatus.DELETED);
            }
            thumbsUpRepository.save(thumbsUps);
        }

        List<NurseSpeakThumbsUpBean> retValue = new ArrayList<>();
        for (NurseSpeakThumbsUpEntity tmp : thumbsUps) {
            NurseSpeakThumbsUpBean comment = beanConverter.convert(tmp);
            retValue.add(comment);
        }
        return retValue;
    }
}
