package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseDeviceTokensBean;
import com.cooltoo.backend.converter.NurseDeviceTokensBeanConverter;
import com.cooltoo.backend.entities.NurseDeviceTokensEntity;
import com.cooltoo.backend.repository.NurseDeviceTokensRepository;
import com.cooltoo.constants.CommonStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yzzhao on 4/28/16.
 */
@Component
public class NurseDeviceTokensService {

    @Autowired
    private NurseDeviceTokensRepository deviceTokensRepository;

    @Autowired
    private NurseService nurseService;

    private static final int ANONYMOUS_USER_ID = -1;

    @Autowired
    private NurseDeviceTokensBeanConverter beanConverter;

    @Transactional
    public long registerAnonymousDeviceToken(String token) {
        return saveDeviceToken(ANONYMOUS_USER_ID, token);
    }

    @Transactional
    public long registerUserDeviceToken(long userId, String token) {
        return saveDeviceToken(userId, token);
    }

    @Transactional
    public void inactiveUserDeviceToken(long userId, String token) {
        List<NurseDeviceTokensEntity> tokens = deviceTokensRepository.findByUserIdAndDeviceTokenAndStatus(userId, token, CommonStatus.ENABLED);
        for (NurseDeviceTokensEntity entity : tokens) {
            entity.setStatus(CommonStatus.DISABLED);
            deviceTokensRepository.save(entity);
        }
    }

    public List<NurseDeviceTokensBean> getNurseDeviceTokens(long userId) {
        List<NurseDeviceTokensEntity> tokens = deviceTokensRepository.findByUserIdAndStatus(userId, CommonStatus.ENABLED);
        return getNurseDeviceTokensBeans(tokens);
    }

    private List<NurseDeviceTokensBean> getNurseDeviceTokensBeans(List<NurseDeviceTokensEntity> tokens) {
        List<NurseDeviceTokensBean> beans = new ArrayList<>();
        for (NurseDeviceTokensEntity entity : tokens) {
            NurseDeviceTokensBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    public List<NurseDeviceTokensBean> getAllActiveDeviceTokens(){
        List<NurseDeviceTokensEntity> tokens = deviceTokensRepository.findByStatus(CommonStatus.ENABLED);
        return getNurseDeviceTokensBeans(tokens);
    }

    private long saveDeviceToken(long userId, String token) {

        if (userId != ANONYMOUS_USER_ID) {

            //make sure one device token only belones to one user
            List<NurseDeviceTokensEntity> deviceTokens = deviceTokensRepository.findByDeviceToken(token);
            for (NurseDeviceTokensEntity entity : deviceTokens) {
                if (entity.getUserId() == ANONYMOUS_USER_ID) {
                    entity.setUserId(userId);
                    deviceTokensRepository.save(entity);
                } else if (entity.getUserId() != userId) {
                    entity.setStatus(CommonStatus.DISABLED);
                    deviceTokensRepository.save(entity);
                } else if (entity.getUserId() == userId && !entity.getStatus().equals(CommonStatus.ENABLED)){
                    entity.setStatus(CommonStatus.ENABLED);
                    deviceTokensRepository.save(entity);
                }
            }
            List<NurseDeviceTokensEntity> tokens = deviceTokensRepository.findByUserIdAndDeviceTokenAndStatus(userId, token, CommonStatus.ENABLED);
            if (!tokens.isEmpty()) {
                return tokens.get(0).getId();
            }

        }

        return registerNewDeviceToken(userId, token);

    }

    private long registerNewDeviceToken(long userId, String token) {
        NurseDeviceTokensEntity entity = new NurseDeviceTokensEntity();

        entity.setUserId(userId);
        entity.setDeviceToken(token);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTimeCreated(Calendar.getInstance().getTime());

        NurseDeviceTokensEntity saved = deviceTokensRepository.save(entity);
        return saved.getId();
    }
}
