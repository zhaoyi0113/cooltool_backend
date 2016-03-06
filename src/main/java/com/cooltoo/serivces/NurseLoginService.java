package com.cooltoo.serivces;

import com.cooltoo.constants.UserType;
import com.cooltoo.converter.NurseBeanConverter;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.entities.TokenAccessEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.repository.TokenAccessRepository;
import com.cooltoo.util.AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * Created by yzzhao on 3/2/16.
 */
@Service("NurseLoginService")
public class NurseLoginService {

    @Autowired
    private TokenAccessRepository tokenAccessRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private NurseBeanConverter beanConverter;

    @Autowired
    private AccessTokenGenerator tokenGenerator;

    @Transactional
    public String login(String mobile, String password){
        List<NurseEntity> nurses = nurseRepository.findNurseByMobile(mobile);
        if(nurses == null || nurses.isEmpty()){
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        //TODO: verity user password

        NurseEntity nurseEntity = nurses.get(0);
        TokenAccessEntity entity = new TokenAccessEntity();
        entity.setUserId(nurseEntity.getId());
        entity.setType(UserType.NURSE);
        entity.setTimeCreated(Calendar.getInstance().getTime());
        String token = tokenGenerator.generateAccessToken(mobile, password);
        entity.setToken(token);
        TokenAccessEntity saved = tokenAccessRepository.save(entity);
        return saved.getToken();
    }
}
