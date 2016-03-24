package com.cooltoo.backend.services;

import com.cooltoo.backend.converter.NurseBeanConverter;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.backend.repository.TokenAccessRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.TokenAccessEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 3/2/16.
 */
@Service("NurseLoginService")
public class NurseLoginService {
    private static final Logger logger = Logger.getLogger(NurseLoginService.class.getName());

    @Autowired
    private TokenAccessRepository tokenAccessRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private NurseBeanConverter beanConverter;

    @Autowired
    private AccessTokenGenerator tokenGenerator;

    @Transactional
    public TokenAccessEntity login(String mobile, String password){
        logger.info("login "+ mobile +", password:"+password);
        List<NurseEntity> nurses = nurseRepository.findNurseByMobile(mobile);
        if(nurses == null || nurses.isEmpty()){
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        NurseEntity nurseEntity = nurses.get(0);
        if(nurseEntity.getPassword()!=null && !nurseEntity.getPassword().equals(password)){
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
        TokenAccessEntity entity = new TokenAccessEntity();
        entity.setUserId(nurseEntity.getId());
        entity.setType(UserType.NURSE);
        entity.setTimeCreated(Calendar.getInstance().getTime());
        entity.setStatus(CommonStatus.ENABLED);
        String token = tokenGenerator.generateAccessToken(mobile, password);
        entity.setToken(token);
        return tokenAccessRepository.save(entity);
    }

    @Transactional
    public void logout(long userId){
        List<TokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByUserId(userId);
        if(tokenEntities.isEmpty()){
            throw new BadRequestException(ErrorCode.NOT_LOGIN);
        }
        tokenEntities.get(0).setStatus(CommonStatus.DISABLED);
        tokenAccessRepository.save(tokenEntities.get(0));
    }

    public boolean isLogin(long userId){
        List<TokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByUserId(userId);
        if (tokenEntities.isEmpty()) {
            return false;
        }
        CommonStatus status = tokenEntities.get(0).getStatus();
        return CommonStatus.ENABLED.equals(status);
    }
}
