package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.backend.entities.TokenAccessEntity;
import com.cooltoo.backend.repository.TokenAccessRepository;
import com.cooltoo.backend.services.NurseLoginService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by yzzhao on 3/4/16.
 */
public class NurseLoginServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseLoginService loginService;

    @Autowired
    private TokenAccessRepository accessRepository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_login_data.xml")
    public void nurseLoginTest(){
        TokenAccessEntity token = loginService.login("135232121222", "");
        Assert.assertNotNull(token);
        TokenAccessEntity savedToken = accessRepository.findOne(token.getId());
        Assert.assertNotNull(savedToken);
        Assert.assertEquals(token.getId(), savedToken.getId());
        Assert.assertEquals(CommonStatus.ENABLED, (token.getStatus()));

        loginService.logout(savedToken.getUserId());
        List<TokenAccessEntity> entities = accessRepository.findTokenAccessByUserId(savedToken.getUserId());
        Assert.assertEquals(CommonStatus.DISABLED, entities.get(0).getStatus());
        Assert.assertFalse(loginService.isLogin(savedToken.getUserId()));
    }


}
