package com.cooltoo.services;

import com.cooltoo.beans.NurseTokenAccessBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.converter.NurseTokenAccessBeanConverter;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.entities.NurseTokenAccessEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseTokenAccessRepository;
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
@Service("NurseAccessTokenService")
public class NurseTokenAccessService {

    private static final Logger logger = LoggerFactory.getLogger(NurseTokenAccessService.class);

    @Autowired private NurseTokenAccessRepository repository;
    @Autowired private NurseTokenAccessBeanConverter beanConverter;

    @Autowired private AccessTokenGenerator accessTokenGenerator;
    @Autowired private CommonNurseService nurseService;

    @Transactional
    public NurseTokenAccessBean addToken(String mobile, String password) {
        logger.info("add token for mobile={}", mobile);
        NurseEntity nurse = nurseService.getNurseByMobileAndPassword(mobile, password);
        if(null==nurse) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        long nurseId = nurse.getId();

        String token = accessTokenGenerator.generateAccessToken(mobile, password);
        List<NurseTokenAccessEntity> tokensExists = repository.findTokenAccessByToken(token);
        while (!VerifyUtil.isListEmpty(tokensExists)) {
            token = accessTokenGenerator.generateAccessToken(mobile, password);
            tokensExists = repository.findTokenAccessByToken(token);
        }

        NurseTokenAccessEntity entity = new NurseTokenAccessEntity();
        entity.setUserId(nurseId);
        entity.setTimeCreated(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setToken(token);
        entity.setType(UserType.NURSE);
        entity = repository.save(entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public void setTokenDisable(String token){
        List<NurseTokenAccessEntity> tokenEntities = repository.findTokenAccessByToken(token);
        // user not login
        if (tokenEntities.isEmpty()) {
            return;
        }
        for (NurseTokenAccessEntity tmp : tokenEntities) {
            tmp.setStatus(CommonStatus.DISABLED);
        }
        repository.save(tokenEntities);
        return;
    }

    public boolean isTokenEnable(String token){
        List<NurseTokenAccessEntity> tokenEntities = repository.findTokenAccessByToken(token);
        if (tokenEntities.isEmpty()) {
            return false;
        }
        CommonStatus status = tokenEntities.get(0).getStatus();
        return CommonStatus.ENABLED.equals(status);
    }

    public NurseTokenAccessBean getToken(String token) {
        List<NurseTokenAccessEntity> tokenEntities = repository.findTokenAccessByToken(token);
        if (tokenEntities.isEmpty()) {
            return null;
        }
        return beanConverter.convert(tokenEntities.get(0));
    }
}
