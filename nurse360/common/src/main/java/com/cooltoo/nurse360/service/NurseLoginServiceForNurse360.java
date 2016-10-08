package com.cooltoo.nurse360.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.UserType;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.entities.NurseTokenAccessEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.repository.NurseTokenAccessRepository;
import com.cooltoo.util.AccessTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * Created by yzzhao on 3/2/16.
 */
@Service("NurseLoginServiceForNurse360")
public class NurseLoginServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NurseLoginServiceForNurse360.class.getName());

    @Autowired private NurseTokenAccessRepository tokenAccessRepository;
    @Autowired private NurseRepository nurseRepository;
    @Autowired private AccessTokenGenerator tokenGenerator;

    @Transactional
    public NurseTokenAccessEntity login(String mobile, String password){
        logger.info("login "+ mobile +", password:"+password);
        List<NurseEntity> nurses = nurseRepository.findByMobile(mobile);
        if(nurses == null || nurses.isEmpty()){
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        NurseEntity nurseEntity = nurses.get(0);
        if(nurseEntity.getPassword()!=null && !nurseEntity.getPassword().equals(password)){
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
        if(UserAuthority.DENY_ALL.equals(nurseEntity.getAuthority())) {
            throw new BadRequestException(ErrorCode.USER_AUTHORITY_DENY_ALL);
        }
        NurseTokenAccessEntity entity = new NurseTokenAccessEntity();
        entity.setUserId(nurseEntity.getId());
        entity.setType(UserType.NURSE);
        entity.setTimeCreated(Calendar.getInstance().getTime());
        entity.setStatus(CommonStatus.ENABLED);
        String token = tokenGenerator.generateAccessToken(mobile, password);
        entity.setToken(token);
        return tokenAccessRepository.save(entity);
    }

    @Transactional
    public void logout(long nurseId){
        List<NurseTokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByUserId(nurseId);
        if(tokenEntities.isEmpty()){
            throw new BadRequestException(ErrorCode.NOT_LOGIN);
        }
        long id = 0;
        NurseTokenAccessEntity entity = null;
        for (NurseTokenAccessEntity tmp : tokenEntities) {
            if (id < tmp.getId()) {
                id = tmp.getId();
                entity = tmp;
            }
        }

        entity.setStatus(CommonStatus.DISABLED);
        tokenAccessRepository.save(entity);
    }

    public boolean isLogin(long userId){
        List<NurseTokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByUserId(userId);
        if (tokenEntities.isEmpty()) {
            return false;
        }

        long id = 0;
        CommonStatus status = null;
        for (NurseTokenAccessEntity tmp : tokenEntities) {
            if (id < tmp.getId()) {
                id = tmp.getId();
                status = tmp.getStatus();
            }
        }

        return CommonStatus.ENABLED.equals(status);
    }


}
