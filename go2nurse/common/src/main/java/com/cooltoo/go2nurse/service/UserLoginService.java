package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import com.cooltoo.go2nurse.repository.UserOpenAppRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.go2nurse.repository.UserTokenAccessRepository;
import com.cooltoo.util.AccessTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * Created by yzzhao on 3/2/16.
 */
@Service("UserLoginService")
public class UserLoginService {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginService.class);

    @Autowired
    private UserTokenAccessRepository tokenAccessRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccessTokenGenerator tokenGenerator;
    @Autowired
    private UserOpenAppRepository openAppRepository;

    @Transactional
    public UserTokenAccessEntity login(String mobile, String password) {
        logger.info("login by mobile={} password={}", mobile, password);
        List<UserEntity> users = userRepository.findByMobile(mobile);
        if (users == null || users.isEmpty()) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        UserEntity userEntity = users.get(0);
        if (userEntity.getPassword() != null && !userEntity.getPassword().equals(password)) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
        if (UserAuthority.DENY_ALL.equals(userEntity.getAuthority())) {
            throw new BadRequestException(ErrorCode.USER_AUTHORITY_DENY_ALL);
        }
        UserTokenAccessEntity entity = new UserTokenAccessEntity();
        entity.setUserId(userEntity.getId());
        entity.setUserType(UserType.NORMAL_USER);
        entity.setTime(Calendar.getInstance().getTime());
        entity.setStatus(CommonStatus.ENABLED);
        String token = tokenGenerator.generateAccessToken(mobile, password);
        entity.setToken(token);
        return tokenAccessRepository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserTokenAccessEntity login(String mobile, String password, String channel, String channelid){
        if(channel != null && channelid != null){
            List<UserOpenAppEntity> channelUsers = openAppRepository.findByUnionidAndStatus(channelid, CommonStatus.ENABLED);
            if(channelUsers.isEmpty()){
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
            UserOpenAppEntity channelUser = channelUsers.get(0);
            UserTokenAccessEntity userEntity = login(mobile, password);
            if(channelUser.getUserId() != userEntity.getUserId()){
                //update existed user mapping
                channelUser.setUserId(userEntity.getUserId());
                openAppRepository.save(channelUser);
            }
            return userEntity;
        } else {
            return login(mobile, password);
        }
    }


    @Transactional
    public void logout(long userId) {
        logger.info("logout userId={}", userId);
        List<UserTokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByUserIdAndUserType(userId, UserType.NORMAL_USER);
        if (tokenEntities.isEmpty()) {
            throw new BadRequestException(ErrorCode.NOT_LOGIN);
        }
        tokenEntities.get(0).setStatus(CommonStatus.DISABLED);
        tokenAccessRepository.save(tokenEntities.get(0));
    }

    public boolean isLogin(long userId) {
        logger.info("is  userId={} login?", userId);
        List<UserTokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByUserIdAndUserType(userId, UserType.NORMAL_USER);
        if (tokenEntities.isEmpty()) {
            return false;
        }
        CommonStatus status = tokenEntities.get(0).getStatus();
        logger.info("userId={} login={}", userId, CommonStatus.ENABLED.equals(status));
        return CommonStatus.ENABLED.equals(status);
    }
}
