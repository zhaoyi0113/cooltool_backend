package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.NurseDeviceTokensBean;
import com.cooltoo.entities.NurseDeviceTokensEntity;
import com.cooltoo.repository.NurseDeviceTokensRepository;
import com.cooltoo.constants.CommonStatus;
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
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    public void testRegisterAnonymousDeviceToken() {
        String token = String.valueOf(System.currentTimeMillis());
        long id = deviceTokensService.registerAnonymousDeviceToken(token);
        NurseDeviceTokensEntity entity = repository.findOne(id);
        Assert.assertNotNull(entity);
        Assert.assertEquals(token, entity.getDeviceToken());
        Assert.assertEquals(-1, entity.getUserId());

        deviceTokensService.registerUserDeviceToken(1, token);
        entity = repository.findOne(id);
        Assert.assertNotNull(entity);
        Assert.assertEquals(token, entity.getDeviceToken());
        Assert.assertEquals(1, entity.getUserId());
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
    public void testRegisterNonExistedUser() {
        String token = String.valueOf(System.currentTimeMillis());
        long userId = System.currentTimeMillis();
        Exception exception = null;
        try {
            deviceTokensService.registerUserDeviceToken(userId, token);
        } catch (BadRequestException ex) {
            exception = ex;
        }
        Assert.assertNotNull(exception);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    public void testRegisterExistedUserToken() {
        long id = deviceTokensService.registerUserDeviceToken(1, "aaa");
        Assert.assertEquals(1000, id);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    public void testRegisterTokenForDifferentUser() {
        long id = deviceTokensService.registerUserDeviceToken(2, "aaa");
        Assert.assertNotEquals(1000, id);
        List<NurseDeviceTokensBean> tokens = deviceTokensService.getNurseDeviceTokens(1);
        for (NurseDeviceTokensBean bean : tokens) {
            if (bean.getDeviceToken().equals("aaa")) {
                Assert.fail();
            }
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    public void testInactiveUserDeviceToken() {
        deviceTokensService.inactiveUserDeviceToken(1, "aaa");
        List<NurseDeviceTokensBean> beans = deviceTokensService.getNurseDeviceTokens(1);
        for (NurseDeviceTokensBean bean : beans) {
            Assert.assertEquals(-1, bean.getUserId());
        }
        deviceTokensService.registerUserDeviceToken(1, "aaa");
        beans = deviceTokensService.getNurseDeviceTokens(1);
        Assert.assertTrue(beans.size() > 0);
        Assert.assertEquals(CommonStatus.ENABLED, beans.get(0).getStatus());
    }


    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    public void testActiveLoggedOutUserToken() {

        List<NurseDeviceTokensEntity> tokens = repository.findByUserId(2);
        Assert.assertEquals(1, tokens.size());
        Assert.assertEquals(CommonStatus.DISABLED, tokens.get(0).getStatus());

        deviceTokensService.registerUserDeviceToken(2, "bbb");
        tokens = repository.findByUserId(2);
        Assert.assertEquals(1, tokens.size());
        Assert.assertEquals(CommonStatus.ENABLED, tokens.get(0).getStatus());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_device_token_data.xml")
    public void testInactiveNoneExistedToken(){
        deviceTokensService.inactiveUserDeviceToken(3, "aaa");
    }
}
