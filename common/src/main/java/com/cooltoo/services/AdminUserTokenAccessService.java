package com.cooltoo.services;

import com.cooltoo.beans.AdminUserBean;
import com.cooltoo.beans.AdminUserTokenAccessBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.converter.AdminUserTokenAccessBeanConverter;
import com.cooltoo.entities.AdminUserTokenAccessEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.AdminUserTokenAccessRepository;
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
@Service("AdminUserAccessTokenService")
public class AdminUserTokenAccessService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserTokenAccessService.class);

    @Autowired private AdminUserTokenAccessRepository repository;
    @Autowired private AdminUserTokenAccessBeanConverter beanConverter;

    @Autowired private AccessTokenGenerator accessTokenGenerator;
    @Autowired private AdminUserService adminService;

    @Transactional
    public AdminUserTokenAccessBean addToken(String name, String password) {
        logger.info("add token for user={}", name);
        AdminUserBean admin = adminService.getUser(name, password);
        if(null==admin) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }

        String token = accessTokenGenerator.generateAccessToken(name, password);
        List<AdminUserTokenAccessEntity> tokensExists = repository.findAdminUserTokenAccessByToken(token);
        while (!VerifyUtil.isListEmpty(tokensExists)) {
            token = accessTokenGenerator.generateAccessToken(name, password);
            tokensExists = repository.findAdminUserTokenAccessByToken(token);
        }

        AdminUserTokenAccessEntity entity = new AdminUserTokenAccessEntity();
        entity.setUserId(admin.getId());
        entity.setUserType(admin.getUserType());
        entity.setTimeCreated(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity.setToken(token);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public void setTokenDisable(String token){
        List<AdminUserTokenAccessEntity> tokenEntities = repository.findAdminUserTokenAccessByToken(token);
        // user not login
        if (tokenEntities.isEmpty()) {
            return;
        }
        for (AdminUserTokenAccessEntity tmp : tokenEntities) {
            tmp.setStatus(CommonStatus.DISABLED);
        }
        repository.save(tokenEntities);
        return;
    }

    public boolean isTokenEnable(String token){
        List<AdminUserTokenAccessEntity> tokenEntities = repository.findAdminUserTokenAccessByToken(token);
        if (tokenEntities.isEmpty()) {
            return false;
        }
        CommonStatus status = tokenEntities.get(0).getStatus();
        return CommonStatus.ENABLED.equals(status);
    }

    public AdminUserTokenAccessBean getToken(String token) {
        List<AdminUserTokenAccessEntity> tokenEntities = repository.findAdminUserTokenAccessByToken(token);
        if (tokenEntities.isEmpty()) {
            return null;
        }
        return beanConverter.convert(tokenEntities.get(0));
    }
}
