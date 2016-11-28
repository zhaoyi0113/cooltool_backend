package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.beans.AdminUserTokenAccessBean;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhaolisong on 16/3/22.
 */
@Transactional
public class AdminUserTokenAccessServiceTest extends AbstractCooltooTest {

    @Autowired
    private AdminUserLoginService loginService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/admin_user_token_access_data.xml")
    public void testLogin() {
        long userId = 7;
        String userName = "user6";
        String password = "user6";
        boolean isLogin = loginService.isLogin(userId);
        Assert.assertFalse(isLogin);
        AdminUserTokenAccessBean bean = loginService.login(userName, password);
        isLogin = loginService.isLogin(userId);
        Assert.assertTrue(isLogin);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/admin_user_token_access_data.xml")
    public void testLogout() {
        long userId = 7;
        String userName = "user6";
        String password = "user6";
        AdminUserTokenAccessBean bean = loginService.login(userName, password);
        boolean isLogin = loginService.isLogin(userId);
        Assert.assertTrue(isLogin);
        loginService.logout(userId);
        isLogin = loginService.isLogin(userId);
        Assert.assertFalse(isLogin);
    }
}
