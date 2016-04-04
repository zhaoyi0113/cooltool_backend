package com.cooltoo.admin.services;

import com.cooltoo.admin.beans.AdminUserTokenAccessBean;
import com.cooltoo.admin.converter.AdminUserTokenAccessBeanConverter;
import com.cooltoo.admin.entities.AdminUserEntity;
import com.cooltoo.admin.entities.AdminUserTokenAccessEntity;
import com.cooltoo.admin.repository.AdminUserRepository;
import com.cooltoo.admin.repository.AdminUserTokenAccessRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Service("AdminUserLoginService")
public class AdminUserLoginService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserLoginService.class.getName());

    @Autowired
    private AdminUserTokenAccessRepository tokenAccessRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private AccessTokenGenerator tokenGenerator;

    @Autowired
    private AdminUserTokenAccessBeanConverter beanConverter;


    @Transactional
    public AdminUserTokenAccessBean login(String userName, String password){
        logger.info("login username:"+ userName +", password:"+password);
        AdminUserEntity userEntity = adminUserRepository.findAdminUserByUserName(userName);
        if(null==userEntity) {
            throw new BadRequestException(ErrorCode.AUTHENTICATION_NOT_EXISTED);
        }
        if(userEntity.getPassword()==null || !userEntity.getPassword().equals(password)){
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
        AdminUserTokenAccessEntity entity = new AdminUserTokenAccessEntity();
        entity.setUserId(userEntity.getId());
        entity.setUserType(userEntity.getUserType());
        entity.setTimeCreated(Calendar.getInstance().getTime());
        entity.setStatus(CommonStatus.ENABLED);
        String token = tokenGenerator.generateAccessToken(userName, password);
        entity.setToken(token);
        entity = tokenAccessRepository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public void logout(long userId){
        List<AdminUserTokenAccessEntity> tokenEntities = tokenAccessRepository.findAdminUserTokenAccessByUserId(userId);
        if(tokenEntities.isEmpty()){
            throw new BadRequestException(ErrorCode.NOT_LOGIN);
        }
        tokenEntities.get(0).setStatus(CommonStatus.DISABLED);
        tokenAccessRepository.save(tokenEntities.get(0));
    }

    @Transactional
    public boolean isLogin(long userId){
        List<AdminUserTokenAccessEntity> tokenEntities = tokenAccessRepository.findAdminUserTokenAccessByUserId(userId);
        if (tokenEntities.isEmpty()) {
            return false;
        }
        CommonStatus status = tokenEntities.get(0).getStatus();
        return CommonStatus.ENABLED.equals(status);
    }
}
