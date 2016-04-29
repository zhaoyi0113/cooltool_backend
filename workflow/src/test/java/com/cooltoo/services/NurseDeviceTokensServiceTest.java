package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseDeviceTokensBean;
import com.cooltoo.backend.entities.NurseDeviceTokensEntity;
import com.cooltoo.backend.repository.NurseDeviceTokensRepository;
import com.cooltoo.backend.services.NurseDeviceTokensService;
import com.cooltoo.exception.BadRequestException;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yzzhao on 4/28/16.
 */
@Transactional
public class NurseDeviceTokensServiceTest extends AbstractCooltooTest {

    @Autowired
    private NurseDeviceTokensService deviceTokensService;

    @Autowired
    private NurseDeviceTokensRepository repository;

    @Test
    public void testRegisterAnonymousDeviceToken() {
        String token = String.valueOf(System.currentTimeMillis());
        long id = deviceTokensService.registerAnonymousDeviceToken(token);
        NurseDeviceTokensEntity entity = repository.findOne(id);
        Assert.assertNotNull(entity);
        Assert.assertEquals(token, entity.getDeviceToken());
        Assert.assertEquals(-1, entity.getUserId());
    }

    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    @Test
    public void testRegisterUserDeviceToken() {
        String token = String.valueOf(System.currentTimeMillis());
        long id = deviceTokensService.registerUserDeviceToken(1, token);
        NurseDeviceTokensEntity entity = repository.findOne(id);
        Assert.assertNotNull(entity);
        Assert.assertEquals(token, entity.getDeviceToken());
        Assert.assertEquals(1, entity.getUserId());
    }

    @Test
    public void testRegisterNonExistedUser(){
        String token = String.valueOf(System.currentTimeMillis());
        long userId = System.currentTimeMillis();
        Exception exception = null;
        try {
            deviceTokensService.registerUserDeviceToken(userId, token);
        }catch(BadRequestException ex){
            exception = ex;
        }
        Assert.assertNotNull(exception);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    public void testRegisterExistedUserToken(){
        long id = deviceTokensService.registerUserDeviceToken(1, "aaa");
        Assert.assertEquals(1000, id);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    public void testRegisterTokenForDifferentUser(){
        long id = deviceTokensService.registerUserDeviceToken(2, "aaa");
        Assert.assertNotEquals(1000, id);
        List<NurseDeviceTokensBean> tokens = deviceTokensService.getNurseDeviceTokens(1);
        for(NurseDeviceTokensBean bean : tokens){
            if(bean.getDeviceToken().equals("aaa")){
                Assert.fail();
            }
        }
    }
}
