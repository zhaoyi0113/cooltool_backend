package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.admin.beans.AdminUserBean;
import com.cooltoo.admin.converter.AdminUserEntityConverter;
import com.cooltoo.admin.services.AdminUserService;
import com.cooltoo.constants.AdminUserType;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by zhaolisong on 16/3/22.
 */
@Transactional
public class AdminUserServiceTest extends AbstractCooltooTest {

    @Autowired
    AdminUserService adminUserService;
    @Autowired
    AdminUserEntityConverter entityConverter;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/admin_user_data.xml")
    public void testCreateUserByAdmin() {
        AdminUserBean adminUserBean = adminUserService.createUserByAdmin(1, "usernew", "aaaaa", "123456", "1342@163.com");
        Assert.assertTrue(adminUserBean.getId()>0);
        Assert.assertEquals("usernew", adminUserBean.getUserName());
        Assert.assertTrue(AdminUserType.NORMAL.equals(adminUserBean.getUserType()));
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/admin_user_data.xml")
    public void testGetAllUsers() {
        List<AdminUserBean> beans = adminUserService.getAllUsersByAdmin(1);
        Assert.assertEquals(8, beans.size());
        for (AdminUserBean bean : beans) {
            System.out.println(bean);
        }
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/admin_user_data.xml")
    public void testGetUser() {
        AdminUserBean
        bean = adminUserService.getUserByAdmin(1, 1);
        Assert.assertEquals(1, bean.getId());
        Assert.assertEquals("admin", bean.getUserName());
        bean = adminUserService.getUserByAdmin(1, 2);
        Assert.assertEquals(2, bean.getId());
        Assert.assertEquals("user1", bean.getUserName());
    }
    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/admin_user_data.xml")
    public void testUpdateUserByAdmin() {
        AdminUserBean bean = adminUserService.getUserByAdmin(1, 2);
        Assert.assertEquals(2, bean.getId());
        Assert.assertEquals("user1", bean.getUserName());
        bean = adminUserService.updateUserByAdmin(1, 2, "aaaaaa", null, null, null);
        Assert.assertEquals(2, bean.getId());
        Assert.assertEquals("aaaaaa", bean.getUserName());
        bean = adminUserService.getUserByAdmin(1, 2);
        Assert.assertEquals(2, bean.getId());
        Assert.assertEquals("aaaaaa", bean.getUserName());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/admin_user_data.xml")
    public void testDeleteUserByAdmin() {
        AdminUserBean bean = adminUserService.getUserByAdmin(1, 2);
        Assert.assertTrue(bean != null);
        adminUserService.deleteUserByAdmin(1, 2);
        Throwable ex = null;
        try {
            bean = adminUserService.getUserByAdmin(1, 2);
        }
        catch (Exception e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }
    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/admin_user_data.xml")
    public void testUpdateUser() {
        AdminUserBean bean = adminUserService.getUserByAdmin(1, 2);
        Assert.assertEquals(2, bean.getId());
        Assert.assertEquals("user1", bean.getUserName());
        bean = adminUserService.updateUser(2, "aaaaaaaa", "158116", "23432@163.com");
        Assert.assertEquals(2, bean.getId());
        Assert.assertEquals("aaaaaaaa", bean.getPassword());
        Assert.assertEquals("15811223456", bean.getPhoneNumber());
        Assert.assertEquals("23432@163.com", bean.getEmail());
    }
}
