package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.beans.Nurse360DeviceTokensBean;
import com.cooltoo.go2nurse.converter.Nurse360DeviceTokensBeanConverter;
import com.cooltoo.go2nurse.entities.Nurse360DeviceTokensEntity;
import com.cooltoo.go2nurse.repository.Nurse360DeviceTokensRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@Service("Nurse360DeviceTokensService")
public class Nurse360DeviceTokensService {

    private static final Logger logger = LoggerFactory.getLogger(Nurse360DeviceTokensService.class);
    private static final int ANONYMOUS_USER_ID = -1;

    @Autowired private Nurse360DeviceTokensRepository repository;
    @Autowired private Nurse360DeviceTokensBeanConverter beanConverter;

    @Transactional
    public long registerUserDeviceToken(long userId, DeviceType type, String token) {
        logger.info("register user device token, userId={} deviceType={} deviceToken={}", userId, type, token);
        return saveDeviceToken(userId, type, token);
    }

    @Transactional
    public void inactiveUserDeviceToken(long userId, DeviceType type, String token) {
        logger.info("inactive user device token, userId={} deviceToken={}", userId, token);
        List<Nurse360DeviceTokensEntity> tokens = repository.findByUserIdAndDeviceTokenAndStatus(userId, token, CommonStatus.ENABLED);
        repository.delete(tokens);
    }

    public List<Nurse360DeviceTokensBean> getUserDeviceToknes(List<Long> userIds) {
        long size = VerifyUtil.isListEmpty(userIds) ? 0 : userIds.size();
        logger.info("get user active device token, userId={}", size>20 ? size : userIds);
        List<Nurse360DeviceTokensEntity> tokens = null;
        if (size > 0) {
            tokens = repository.findByUserIdInAndStatus(userIds, CommonStatus.ENABLED);
        }
        List<Nurse360DeviceTokensBean> beans = entitiesToBeans(tokens);
        logger.info("get user active device token, count={}", beans.size());
        return beans;
    }

    public List<Nurse360DeviceTokensBean> getDeviceToken(DeviceType type, String token) {
        List<Nurse360DeviceTokensEntity> deviceTokens = repository.findByDeviceTypeAndDeviceToken(type, token);
        List<Nurse360DeviceTokensBean> beans = entitiesToBeans(deviceTokens);
        return beans;
    }

    private List<Nurse360DeviceTokensBean> entitiesToBeans(Iterable<Nurse360DeviceTokensEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<Nurse360DeviceTokensBean> beans = new ArrayList<>();
        for (Nurse360DeviceTokensEntity entity : entities) {
            Nurse360DeviceTokensBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    @Transactional
    private long saveDeviceToken(long userId, DeviceType type, String token) {
        //make sure one device token only one on activity status
        //make sure one device token only belongs to one user
        List<Nurse360DeviceTokensEntity> userDeviceTokens = new ArrayList<>();
        List<Nurse360DeviceTokensEntity> deviceTokens = repository.findByDeviceTypeAndDeviceToken(type, token);
        for (Nurse360DeviceTokensEntity deviceToken : deviceTokens) {
            if (deviceToken.getUserId()==userId) {
                userDeviceTokens.add(deviceToken);
            }
            deviceToken.setStatus(CommonStatus.DISABLED);
        }
        if (!VerifyUtil.isListEmpty(deviceTokens)) {
            repository.save(deviceTokens);
        }

        if (!VerifyUtil.isListEmpty(userDeviceTokens)) {
            Nurse360DeviceTokensEntity nurseDeviceToken = userDeviceTokens.get(0);
            userDeviceTokens.remove(0);
            // if the first device token is marked by anonymous user
            if (nurseDeviceToken.getUserId()!=userId) {
                nurseDeviceToken.setUserId(userId);
            }
            nurseDeviceToken.setStatus(CommonStatus.ENABLED);
            nurseDeviceToken.setTime(new Date());

            nurseDeviceToken = repository.save(nurseDeviceToken);
            if (!VerifyUtil.isListEmpty(userDeviceTokens)) {
                repository.delete(userDeviceTokens);
            }
            if (userId == ANONYMOUS_USER_ID) {
                return userId;
            }
            else {
                return nurseDeviceToken.getId();
            }
        }

        return registerNewDeviceToken(userId, type, token);
    }

    @Transactional
    private long registerNewDeviceToken(long userId, DeviceType type, String token) {
        List<Nurse360DeviceTokensEntity> existed = repository.findByUserIdAndDeviceTypeAndDeviceTokenAndStatus(userId, type, token, CommonStatus.ENABLED);
        if (existed != null && !existed.isEmpty()){
            return existed.get(0).getId();
        }

        Nurse360DeviceTokensEntity entity = new Nurse360DeviceTokensEntity();
        entity.setUserId(userId);
        entity.setDeviceType(type);
        entity.setDeviceToken(token);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        Nurse360DeviceTokensEntity saved = repository.save(entity);
        return saved.getId();
    }

    private List<Nurse360DeviceTokensBean> entitiesToBeans(List<Nurse360DeviceTokensEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<Nurse360DeviceTokensBean> ret = new ArrayList<>();
        for (Nurse360DeviceTokensEntity tmp : entities) {
            Nurse360DeviceTokensBean bean = beanConverter.convert(tmp);
            ret.add(bean);
        }
        return ret;
    }
}
