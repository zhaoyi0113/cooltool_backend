package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import com.cooltoo.go2nurse.repository.UserTokenAccessRepository;
import com.cooltoo.go2nurse.service.UserLoginService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yzzhao on 3/4/16.
 */
@Transactional
public class UserLoginServiceTest extends AbstractCooltooTest {

    @Autowired
    private UserLoginService loginService;

    @Autowired
    private UserTokenAccessRepository accessRepository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/user_data.xml")
    public void userLoginLogoutTest(){
        UserTokenAccessEntity token = loginService.login("1231432143", "aa01");
        Assert.assertNotNull(token);
        UserTokenAccessEntity savedToken = accessRepository.findOne(token.getId());
        Assert.assertNotNull(savedToken);
        Assert.assertEquals(token.getId(), savedToken.getId());
        Assert.assertEquals(CommonStatus.ENABLED, (token.getStatus()));

        loginService.logout(savedToken.getUserId());
        List<UserTokenAccessEntity> entities = accessRepository.findTokenAccessByUserIdAndUserType(savedToken.getUserId(), savedToken.getUserType());
        Assert.assertEquals(CommonStatus.DISABLED, entities.get(0).getStatus());
        Assert.assertFalse(loginService.isLogin(savedToken.getUserId()));
    }


}
