package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.GenderType;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.service.UserService;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.util.NumberUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/2.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/user_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/file_storage_data.xml")
})
public class UserServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Autowired private UserService service;
    @Autowired private UserGo2NurseFileStorageService userStorage;

    @Test
    public void testNew() {
        String name = "name_1";
        int gender = 1;
        String mobile = "15811663430";
        String password = "password";
        String smsCode = "aaaa";
        long id = service.registerUser(name, gender, "", mobile, password, smsCode);
        Assert.assertTrue(id>0);
        List<UserBean> all = service.getAllByAuthorityAndFuzzyName("", name, 0, 100);
        Assert.assertTrue(all.size()>0);
    }

    @Test
    public void testUpdateUser(){
        long userId = 1;
        String name = "name 4444444444";
        int gender = 2;
        String birthday = "2013-04-14 14:44:44";
        int authority = 0;

        long lBirthday = NumberUtil.getTime(birthday, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        UserBean bean = service.updateUser(userId, name, gender, birthday, authority);
        Assert.assertNotNull(bean);
        Assert.assertEquals(1, bean.getId());
        Assert.assertEquals(name, bean.getName());
        Assert.assertEquals(lBirthday,  bean.getBirthday().getTime());
        Assert.assertEquals(GenderType.SECRET, bean.getGender());
        Assert.assertNotEquals("4321654312", bean.getMobile());
    }

    @Test
    public void testUpdateProfilePhoto(){
        String filePath = "build/"+System.currentTimeMillis();
        File file = new File(filePath);
        try {
            file.createNewFile();
            InputStream input = new FileInputStream(filePath);
            UserBean user = service.updateProfilePhoto(1, file.getName(), input);

            String profilePhotoPath = userStorage.getFilePath(user.getProfilePhoto());

            Assert.assertTrue(userStorage.fileExist(profilePhotoPath));
            userStorage.deleteFile(profilePhotoPath);
            Assert.assertFalse(userStorage.fileExist(profilePhotoPath));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
    }

    @Test
    public void testUpdateMobilePassword() {
        String mobile = "13423143214";
        String password = "12fd3a45b61c9840e40f4f45b6d1c";
        long userId = 1;

        UserBean bean1 = service.getUserWithoutOtherInfo(userId);
        UserBean bean2 = service.updateMobilePassword(userId, mobile, password);

        Assert.assertEquals(mobile, bean2.getMobile());
        Assert.assertEquals(password, bean2.getPassword());
        Assert.assertEquals(1, bean2.getId());

        Assert.assertNotEquals(bean1.getMobile(), bean2.getMobile());
        Assert.assertNotEquals(bean1.getPassword(), bean2.getPassword());
        Assert.assertEquals(bean1.getId(), bean2.getId());
    }

    @Test
    public void testGetAll(){
        List<UserBean> all = service.getAllByAuthorityAndFuzzyName("", "", 0, 100);
        Assert.assertEquals(17, all.size());
    }

    @Test
    public void testCountUser() {
        long count = service.countByAuthorityAndFuzzyName("", "");
        Assert.assertEquals(17, count);

        count = service.countByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL.name(), "");
        Assert.assertEquals(12, count);
        count = service.countByAuthorityAndFuzzyName(UserAuthority.DENY_ALL.name(), "");
        Assert.assertEquals(5, count);

        count = service.countByAuthorityAndFuzzyName("", "用户1");
        Assert.assertEquals(9, count);
        count = service.countByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL.name(), "用户1");
        Assert.assertEquals(4, count);
        count = service.countByAuthorityAndFuzzyName(UserAuthority.DENY_ALL.name(), "用户1");
        Assert.assertEquals(5, count);
    }

    @Test
    public void testGetUserByAuthorityAndIds() {
        List<Long> userIds = Arrays.asList(new Long[]{9L, 10L, 11L, 12L, 13L});
        UserAuthority authority = null;

        List<UserBean> users = service.getUser(userIds, authority);
        Assert.assertEquals(5, users.size());

        authority = UserAuthority.AGREE_ALL;
        users = service.getUser(userIds, authority);
        Assert.assertEquals(2, users.size());

        authority = UserAuthority.DENY_ALL;
        users = service.getUser(userIds, authority);
        Assert.assertEquals(3, users.size());
    }

    @Test
    public void testGetUserByAuthority() {
        List<UserBean> count = service.getAllByAuthorityAndFuzzyName("", "", 0, 30);
        Assert.assertEquals(17, count.size());

        count = service.getAllByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL.name(), "", 0, 30);
        Assert.assertEquals(12, count.size());
        count = service.getAllByAuthorityAndFuzzyName(UserAuthority.DENY_ALL.name(), "", 0, 30);
        Assert.assertEquals(5, count.size());

        count = service.getAllByAuthorityAndFuzzyName("", "name1", 0, 30);
        Assert.assertEquals(9, count.size());
        count = service.getAllByAuthorityAndFuzzyName(UserAuthority.AGREE_ALL.name(), "name1", 0, 30);
        Assert.assertEquals(4, count.size());
        count = service.getAllByAuthorityAndFuzzyName(UserAuthority.DENY_ALL.name(), "name1", 0, 30);
        Assert.assertEquals(5, count.size());
    }

    @Test
    public void testGetUserWithoutOtherInfoWithMobile() {
        String mobile = "1231432143";
        UserBean exists = service.getUserWithoutOtherInfo(mobile);
        Assert.assertEquals(mobile, exists.getMobile());
        Assert.assertEquals(1, exists.getId());

        mobile = "132143214321432143214321431431";
        exists = service.getUserWithoutOtherInfo(mobile);
        Assert.assertNull(exists);
    }

    @Test
    public void testGetUserWithoutOtherInfoWithId() {
        long userId = 1;
        UserBean exists = service.getUserWithoutOtherInfo(userId);
        Assert.assertEquals(userId, exists.getId());

        userId = 100;
        exists = service.getUserWithoutOtherInfo(userId);
        Assert.assertNull(exists);
    }

    @Test
    public void testExistUser() {
        long userId = 1;
        boolean exists = service.existUser(userId);
        Assert.assertTrue(exists);

        userId = 100;
        exists = service.existUser(userId);
        Assert.assertFalse(exists);
    }

    @Test
    public void testGetAllDenyUserIds() {
        List<Long> ids = service.getAllDenyUserIds();
        Assert.assertTrue(ids.contains(10L));
        Assert.assertTrue(ids.contains(12L));
        Assert.assertTrue(ids.contains(13L));
        Assert.assertTrue(ids.contains(15L));
        Assert.assertTrue(ids.contains(16L));
    }
}
