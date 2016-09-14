package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.beans.UserDeviceTokensBean;
import com.cooltoo.go2nurse.converter.UserDeviceTokensBeanConverter;
import com.cooltoo.go2nurse.entities.UserDeviceTokensEntity;
import com.cooltoo.go2nurse.repository.UserDeviceTokensRepository;
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
 * Created by hp on 2016/9/13.
 */
@Service("UserDeviceTokensService")
public class UserDeviceTokensService {

    private static final Logger logger = LoggerFactory.getLogger(UserDeviceTokensService.class);
    private static final int ANONYMOUS_USER_ID = -1;

    @Autowired private UserDeviceTokensRepository repository;
    @Autowired private UserDeviceTokensBeanConverter beanConverter;

    @Transactional
    public long registerUserDeviceToken(long userId, DeviceType type, String token) {
        logger.info("register user device token, userId={} deviceType={} deviceToken={}", userId, type, token);
        return saveDeviceToken(userId, type, token);
    }

    @Transactional
    public void inactiveUserDeviceToken(long userId, DeviceType type, String token) {
        logger.info("inactive user device token, userId={} deviceToken={}", userId, token);
        List<UserDeviceTokensEntity> tokens = repository.findByUserIdAndDeviceTokenAndStatus(userId, token, CommonStatus.ENABLED);
        repository.delete(tokens);
    }

    public List<UserDeviceTokensBean> getUserDeviceTokens(long userId) {
        logger.info("get user active device token, userId={}", userId);
        List<UserDeviceTokensEntity> tokens = repository.findByUserIdAndStatus(userId, CommonStatus.ENABLED);
        List<UserDeviceTokensBean> beans = entitiesToBeans(tokens);
        logger.info("get user active device token, count={}", beans.size());
        return beans;
    }

    public List<UserDeviceTokensBean> getDeviceToken(DeviceType type, String token) {
        List<UserDeviceTokensEntity> deviceTokens = repository.findByDeviceTypeAndDeviceToken(type, token);
        List<UserDeviceTokensBean> beans = entitiesToBeans(deviceTokens);
        return beans;
    }

    private List<UserDeviceTokensBean> entitiesToBeans(Iterable<UserDeviceTokensEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<UserDeviceTokensBean> beans = new ArrayList<>();
        for (UserDeviceTokensEntity entity : entities) {
            UserDeviceTokensBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    @Transactional
    private long saveDeviceToken(long userId, DeviceType type, String token) {
        //make sure one device token only one on activity status
        //make sure one device token only belongs to one user
        List<UserDeviceTokensEntity> userDeviceTokens = new ArrayList<>();
        List<UserDeviceTokensEntity> deviceTokens = repository.findByDeviceTypeAndDeviceToken(type, token);
        for (UserDeviceTokensEntity deviceToken : deviceTokens) {
            if (deviceToken.getUserId()==userId) {
                userDeviceTokens.add(deviceToken);
            }
            deviceToken.setStatus(CommonStatus.DISABLED);
        }
        if (!VerifyUtil.isListEmpty(deviceTokens)) {
            repository.save(deviceTokens);
        }

        if (!VerifyUtil.isListEmpty(userDeviceTokens)) {
            UserDeviceTokensEntity nurseDeviceToken = userDeviceTokens.get(0);
            userDeviceTokens.remove(0);
            // if the first device token is marked by anonymous user
            if (nurseDeviceToken.getUserId()!=userId) {
                nurseDeviceToken.setUserId(userId);
            }
            nurseDeviceToken.setStatus(CommonStatus.ENABLED);
            nurseDeviceToken.setTimeCreated(new Date());

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
        List<UserDeviceTokensEntity> existed = repository.findByUserIdAndDeviceTypeAndDeviceTokenAndStatus(userId, type, token, CommonStatus.ENABLED);
        if (existed != null && !existed.isEmpty()){
            return existed.get(0).getId();
        }
        UserDeviceTokensEntity entity = new UserDeviceTokensEntity();

        entity.setUserId(userId);
        entity.setDeviceType(type);
        entity.setDeviceToken(token);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTimeCreated(new Date());

        UserDeviceTokensEntity saved = repository.save(entity);
        return saved.getId();
    }

    private List<UserDeviceTokensBean> entitiesToBeans(List<UserDeviceTokensEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<UserDeviceTokensBean> ret = new ArrayList<>();
        for (UserDeviceTokensEntity tmp : entities) {
            UserDeviceTokensBean bean = beanConverter.convert(tmp);
            ret.add(bean);
        }
        return ret;
    }
}
