package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseDeviceTokensBean;
import com.cooltoo.backend.converter.NurseDeviceTokensBeanConverter;
import com.cooltoo.backend.entities.NurseDeviceTokensEntity;
import com.cooltoo.backend.repository.NurseDeviceTokensRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yzzhao on 4/28/16.
 */
@Component
public class NurseDeviceTokensService {

    private static final Logger logger = LoggerFactory.getLogger(NurseDeviceTokensService.class);
    private static final int ANONYMOUS_USER_ID = -1;

    @Autowired private NurseDeviceTokensRepository deviceTokensRepository;
    @Autowired private NurseDeviceTokensBeanConverter beanConverter;

    @Transactional
    public long registerAnonymousDeviceToken(String token) {
        return saveDeviceToken(ANONYMOUS_USER_ID, token);
    }

    @Transactional
    public long registerUserDeviceToken(long userId, String token) {
        logger.info("register user device token, userId={} deviceToken={}", userId, token);
        return saveDeviceToken(userId, token);
    }

    @Transactional
    public void inactiveUserDeviceToken(long userId, String token) {
        logger.info("inactive user device token, userId={} deviceToken={}", userId, token);
        List<NurseDeviceTokensEntity> tokens = deviceTokensRepository.findByUserIdAndDeviceTokenAndStatus(userId, token, CommonStatus.ENABLED);
        for (NurseDeviceTokensEntity entity : tokens) {
            entity.setUserId(-1);
            deviceTokensRepository.save(entity);
        }
    }

    public List<NurseDeviceTokensBean> getNurseDeviceTokens(long userId) {
        logger.info("get user active device token, userId={}", userId);
        List<NurseDeviceTokensEntity> tokens = deviceTokensRepository.findByUserIdAndStatus(userId, CommonStatus.ENABLED);
        List<NurseDeviceTokensBean> beans = entitiesToBeans(tokens);
        logger.info("get user active device token, count={}", beans.size());
        return beans;
    }

    public List<NurseDeviceTokensBean> getAllActiveDeviceTokens() {
        logger.info("get all active device token");
        List<NurseDeviceTokensEntity> tokens = deviceTokensRepository.findByStatus(CommonStatus.ENABLED);
        List<NurseDeviceTokensBean> beans = entitiesToBeans(tokens);
        logger.info("get all active device token, count={}", beans.size());
        return beans;
    }

    private List<NurseDeviceTokensBean> entitiesToBeans(List<NurseDeviceTokensEntity> entities) {
        List<NurseDeviceTokensBean> beans = new ArrayList<>();
        for (NurseDeviceTokensEntity entity : entities) {
            NurseDeviceTokensBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    @Transactional
    private long saveDeviceToken(long userId, String token) {
        //make sure one device token only one on activity status
        //make sure one device token only belongs to one user
        List<NurseDeviceTokensEntity> anonymousDeviceTokens = new ArrayList<>();
        List<NurseDeviceTokensEntity> nurseDeviceTokens = new ArrayList<>();
        List<NurseDeviceTokensEntity> deviceTokens = deviceTokensRepository.findByDeviceToken(token);
        for (NurseDeviceTokensEntity deviceToken : deviceTokens) {
            if (deviceToken.getUserId()==ANONYMOUS_USER_ID) {
                anonymousDeviceTokens.add(deviceToken);
            }
            if (deviceToken.getUserId()==userId) {
                nurseDeviceTokens.add(deviceToken);
            }
            deviceToken.setStatus(CommonStatus.DISABLED);
        }
        if (!VerifyUtil.isListEmpty(deviceTokens)) {
            deviceTokensRepository.save(deviceTokens);
        }

        // add anonymous device token to user device
        for (NurseDeviceTokensEntity deviceToken : anonymousDeviceTokens) {
            nurseDeviceTokens.add(deviceToken);
        }

        if (!VerifyUtil.isListEmpty(nurseDeviceTokens)) {
            NurseDeviceTokensEntity nurseDeviceToken = nurseDeviceTokens.get(0);
            nurseDeviceTokens.remove(0);
            // if the first device token is marked by anonymous user
            if (nurseDeviceToken.getUserId()!=userId) {
                nurseDeviceToken.setUserId(userId);
            }
            nurseDeviceToken.setStatus(CommonStatus.ENABLED);
            nurseDeviceToken.setTimeCreated(new Date());

            nurseDeviceToken = deviceTokensRepository.save(nurseDeviceToken);
            if (!VerifyUtil.isListEmpty(nurseDeviceTokens)) {
                deviceTokensRepository.delete(nurseDeviceTokens);
            }
            if (userId == ANONYMOUS_USER_ID) {
                return userId;
            }
            else {
                return nurseDeviceToken.getId();
            }
        }

        return registerNewDeviceToken(userId, token);
    }

    @Transactional
    private long registerNewDeviceToken(long userId, String token) {
        List<NurseDeviceTokensEntity> existed = deviceTokensRepository.findByUserIdAndDeviceTokenAndStatus(userId, token, CommonStatus.ENABLED);
        if (existed != null && !existed.isEmpty()){
            return existed.get(0).getUserId();
        }
        NurseDeviceTokensEntity entity = new NurseDeviceTokensEntity();

        entity.setUserId(userId);
        entity.setDeviceToken(token);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTimeCreated(Calendar.getInstance().getTime());

        NurseDeviceTokensEntity saved = deviceTokensRepository.save(entity);
        return saved.getId();
    }


}
