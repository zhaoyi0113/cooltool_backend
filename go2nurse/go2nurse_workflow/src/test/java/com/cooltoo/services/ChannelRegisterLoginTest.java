package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.AppChannel;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.WeChatUserInfo;
import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import com.cooltoo.go2nurse.entities.UserWeChatTokenAccessEntity;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.repository.UserOpenAppRepository;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.go2nurse.repository.UserWeChatTokenAccessRepository;
import com.cooltoo.go2nurse.service.UserLoginService;
import com.cooltoo.go2nurse.service.UserService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

/**
 * Created by yzzhao on 8/16/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/channel_register_login_data.xml")
})
public class ChannelRegisterLoginTest extends AbstractCooltooTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserOpenAppRepository openAppRepository;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserLoginService loginService;

    @Autowired
    private UserRepository userRepository;
    @Autowired private UserWeChatTokenAccessRepository weChatTokenAccessRepository;

    @Test
    public void test_register_from_existed_wechat_user() {
        UserBean newUser = userService.registerUser("", 0, "", "aaa", "aaa", "aaa", "none", AppChannel.WECHAT.name(), "1", null);
        List<UserOpenAppEntity> channelUsers = openAppRepository.findByUnionidAndStatus("1", CommonStatus.ENABLED);
        Assert.assertNotNull(newUser);
        Assert.assertFalse(channelUsers.isEmpty());
        Assert.assertEquals(newUser.getId(), channelUsers.get(0).getUserId());
    }

    @Test
    public void test_login_from_non_registered_wechat_user() {
        WeChatUserInfo userInfo = new WeChatUserInfo();
        userInfo.setOpenid("100");
        URI uri = weChatService.loginWithWeChatUser(userInfo, null,"1");
        Assert.assertNotNull(uri);
        Assert.assertTrue(uri.toString().contains("register"));
        userService.registerUser("aa", 0, "aa", "aaa", "aaa", "aaa", "none", AppChannel.WECHAT.name(), "100", "100");
        uri = weChatService.loginWithWeChatUser(userInfo, null,"1");
        Assert.assertNotNull(uri);
        Assert.assertTrue(uri.getSchemeSpecificPart().contains("token"));
    }

    @Test
    public void test_login_from_registered_wechat_user() {
        WeChatUserInfo userInfo = new WeChatUserInfo();
        userInfo.setUserId(999);
        userInfo.setOpenid("1");
        URI uri = weChatService.loginWithWeChatUser(userInfo, null,"1");
        Assert.assertNotNull(uri);
        Assert.assertFalse(uri.toString().contains("token"));
        userService.registerUser("aa", 0, "aa", "13523212122", "aaa", "aaa", "none", AppChannel.WECHAT.name(), null, "1");
        uri = weChatService.loginWithWeChatUser(userInfo, null,"1");
        Assert.assertNotNull(uri);
        Assert.assertTrue(uri.toString(), uri.toString().contains("token"));
    }

    @Test
    public void test_login_with_different_channelid_and_user_info() {
        UserTokenAccessEntity userEntity = loginService.login("14321134", "aa02", AppChannel.WECHAT.name(), "1", null);
        Assert.assertEquals(2, userEntity.getUserId());
        List<UserOpenAppEntity> openUsers = openAppRepository.findByUnionidAndStatus("1", CommonStatus.ENABLED);
        Assert.assertEquals(2, openUsers.get(0).getUserId());
    }

    @Test
    public void test_register_with_disabled_channel_user() {
        UserBean userBean = userService.registerUser("bbb", 0, "", "13523212122", "aaa", "aaa", "none", AppChannel.WECHAT.name(), "2", "1");
        Assert.assertEquals("bbb", userBean.getName());
        Assert.assertEquals("aaa", userBean.getPassword());
        List<UserOpenAppEntity> openUsers = openAppRepository.findByUnionidAndStatus("2", CommonStatus.ENABLED);
        Assert.assertFalse(openUsers.isEmpty());
        List<UserEntity> mobiles = userRepository.findByMobile("13523212122");
        Assert.assertEquals(mobiles.get(0).getId(), openUsers.get(0).getUserId());
    }

    @Test
    public void test_login_with_disabled_channelid() {
        BadRequestException ex = null;
        try {
            loginService.login("16811663451", "aa06", "wx", "2", "aaaa");
        } catch (BadRequestException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }

    @Test
    public void test_login_with_channelid_from_different_user() {
        loginService.login("16811663451", "aa06", "wx", "3", "");
        List<UserOpenAppEntity> entities = openAppRepository.findByUnionid("3");
        Assert.assertEquals(1, entities.size());
        UserOpenAppEntity entity = entities.get(0);
        Assert.assertEquals(6, entity.getUserId());
    }

    @Test
    public void test_login_with_openid() {
        UserTokenAccessEntity userEntity = loginService.login("13523212122", "aa03", "wx", null, "1");
        Assert.assertNotNull(userEntity);
        userEntity = loginService.login("13523212122", "aa03", "wx", null, "2");
        Assert.assertNotNull(userEntity);
        userEntity = loginService.login("13523212122", "aa03", "wx", null, "2");
        Assert.assertNotNull(userEntity);
        userEntity = loginService.login("13523212122", "aa03", "wx", "5", "2");
        Assert.assertNotNull(userEntity);

        WeChatUserInfo weChatUserInfo = new WeChatUserInfo();
        weChatUserInfo.setOpenid("1");
        URI uri = weChatService.loginWithWeChatUser(weChatUserInfo, null,"1");
        Assert.assertNotNull(uri);
        Assert.assertTrue(uri.toString().contains("token"));
    }

    @Test
    public void test_failed_login_with_openid() {
        BadRequestException ex = null;
        try {
            UserTokenAccessEntity userEntity = loginService.login("13523212122", "aa03", "wx", null, "xxxxx");
        } catch (BadRequestException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }

    @Test
    public void test_register_with_existed_account() {
        BadRequestException ex = null;
        try {
            userService.registerUser("", 0, "", "1231432143", "aa01", "", "");
        } catch (BadRequestException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }

    @Test
    public void test_register_new_user_with_openid() {
        UserBean userBean = userService.registerUser("name", 0, null, "12345678", "123456", "", "YES", AppChannel.WECHAT.name(), "", "1");
        Assert.assertNotNull(userBean);
        List<UserEntity> user = userRepository.findByMobile("12345678");
        Assert.assertTrue(user.size() > 0);
        Assert.assertEquals("12345678", user.get(0).getMobile());


    }

    @Test
    public void test_register_existed_user_with_openid() {
        userService.registerUser("aaa", 0, null, "1231432143", "aa01", "", "NO", AppChannel.WECHAT.name(), "", "1");
        userService.registerUser("aaa", 0, null, "1231432143", "aa01", "", "NO", AppChannel.WECHAT.name(), "", "2");
        long id = userService.getUser("1231432143").getId();
        List<UserOpenAppEntity> users = openAppRepository.findByUserId(id);
        Assert.assertTrue(users.size() > 0);
    }

    @Test
    public void test_register_duplicate_channelId() {
        BadRequestException ex =  null;
        try{
            userService.registerUser("",0,null,"","","","",AppChannel.WECHAT.name(), "1","");
        }catch(BadRequestException e){
            ex = e;
        }
        Assert.assertNotNull(ex);
    }

    @Test
    public void test_login_save_wechat_token(){
        UserTokenAccessEntity entity = loginService.login("13523212122", "aa03", AppChannel.WECHAT.name(), null, "1");
        UserWeChatTokenAccessEntity wechatToken = weChatTokenAccessRepository.findFirstByTokenAndStatus(entity.getToken(), CommonStatus.ENABLED);
        Assert.assertNotNull(wechatToken);

    }

    @Test
    public void test_find_appid_by_token(){
        String appid = weChatTokenAccessRepository.findAppIdFromToken("token1", CommonStatus.ENABLED);
        Assert.assertNotNull(appid);
        Assert.assertEquals("3", appid);
    }

    @Test
    public void test_wechat_login_duplicate_user(){
        WeChatUserInfo userInfo = new WeChatUserInfo();
        userInfo.setOpenid(System.currentTimeMillis()+"");
        weChatService.loginWithWeChatUser(userInfo, "",null);
        weChatService.loginWithWeChatUser(userInfo, "",null);
        weChatService.loginWithWeChatUser(userInfo, "",null);
        List<UserOpenAppEntity> userOpenEntties = openAppRepository.findByOpenid(userInfo.getOpenid());
        Assert.assertEquals(1, userOpenEntties.size());
    }

    @Test
    public void test_login_user_has_mutiple_openid(){
        WeChatUserInfo userInfo = new WeChatUserInfo();
        userInfo.setOpenid("2");
        URI uri = weChatService.loginWithWeChatUser(userInfo, "1", "2");
        Assert.assertTrue(uri.toString(), uri.toString().contains("token2"));
    }
}
