package com.cooltoo.services;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.entities.TokenAccessEntity;
import com.cooltoo.repository.TokenAccessRepository;
import com.cooltoo.serivces.NurseLoginService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by yzzhao on 3/4/16.
 */
public class NurseLoginServiceTest extends AbstractCooltooTest{

    @Autowired
    private NurseLoginService loginService;

    @Autowired
    private TokenAccessRepository accessRepository;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_login_data.xml")
    public void nurseLoginTest(){
        NurseBean nurse = loginService.login("135232121222", "");
        Assert.assertNotNull(nurse);
        List<TokenAccessEntity> tokens = accessRepository.findTokenAccessByUserId(nurse.getId());
        Assert.assertTrue(tokens.size()>0);
        Assert.assertEquals(nurse.getId(), tokens.get(0).getUserId());
    }
}
