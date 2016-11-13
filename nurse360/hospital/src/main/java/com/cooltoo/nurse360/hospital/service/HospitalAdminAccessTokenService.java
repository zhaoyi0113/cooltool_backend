package com.cooltoo.nurse360.hospital.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.HospitalAdminAccessTokenBean;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.converters.HospitalAdminAccessTokenBeanConverter;
import com.cooltoo.nurse360.entities.HospitalAdminAccessTokenEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.repository.HospitalAdminAccessTokenRepository;
import com.cooltoo.util.AccessTokenGenerator;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@Service("HospitalAdminAccessTokenService")
public class HospitalAdminAccessTokenService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalAdminAccessTokenService.class);

    @Autowired private HospitalAdminAccessTokenRepository repository;
    @Autowired private HospitalAdminAccessTokenBeanConverter beanConverter;

    @Autowired private AccessTokenGenerator accessTokenGenerator;
    @Autowired private HospitalAdminService adminService;

    @Transactional
    public HospitalAdminAccessTokenBean addToken(String name, String password) {
        List<HospitalAdminBean> adminUsers = adminService.getAdminUserWithoutInfo(name, password);
        if(VerifyUtil.isListEmpty(adminUsers)) {
            throw new BadRequestException(ErrorCode.NURSE360_USER_NOT_FOUND);
        }

        String token = accessTokenGenerator.generateAccessToken(name, password);
        List<HospitalAdminAccessTokenEntity> tokensExists = repository.findByToken(token);
        while (!VerifyUtil.isListEmpty(tokensExists)) {
            token = accessTokenGenerator.generateAccessToken(name, password);
            tokensExists = repository.findByToken(token);
        }

        HospitalAdminBean admin = adminUsers.get(0);
        HospitalAdminAccessTokenEntity entity = new HospitalAdminAccessTokenEntity();
        entity.setAdminId(admin.getId());
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setToken(token);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public void setTokenDisable(String token){
        List<HospitalAdminAccessTokenEntity> tokenEntities = repository.findByToken(token);
        // user not login
        if (tokenEntities.isEmpty()) {
            return;
        }
        for (HospitalAdminAccessTokenEntity tmp : tokenEntities) {
            tmp.setStatus(CommonStatus.DISABLED);
        }
        repository.save(tokenEntities);
        return;
    }

    public boolean isTokenEnable(String token){
        List<HospitalAdminAccessTokenEntity> tokenEntities = repository.findByToken(token);
        if (tokenEntities.isEmpty()) {
            return false;
        }
        CommonStatus status = tokenEntities.get(0).getStatus();
        return CommonStatus.ENABLED.equals(status);
    }

    public HospitalAdminAccessTokenBean getToken(String token) {
        List<HospitalAdminAccessTokenEntity> tokenEntities = repository.findByToken(token);
        if (tokenEntities.isEmpty()) {
            return null;
        }
        return beanConverter.convert(tokenEntities.get(0));
    }
}
