package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.constants.AppChannel;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.beans.WeChatUserInfo;
import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.repository.UserOpenAppRepository;
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

    @Test
    public void test_register_from_existed_wechat_user() {
        UserBean newUser = userService.registerUser("", 0, "", "aaa", "aaa", "aaa", AppChannel.WECHAT.name(), "1");
        List<UserOpenAppEntity> channelUsers = openAppRepository.findByUnionid("1");
        Assert.assertNotNull(newUser);
        Assert.assertFalse(channelUsers.isEmpty());
        Assert.assertEquals(newUser.getId(), channelUsers.get(0).getUserId());
    }

    @Test
    public void test_login_from_non_registered_wechat_user() {
        WeChatUserInfo userInfo = new WeChatUserInfo();
        String unionid = "100";
        userInfo.setUnionid(unionid);
        URI uri = weChatService.loginWithWeChatUser(userInfo);
        Assert.assertNotNull(uri);
        Assert.assertTrue(uri.toString().contains("register"));
        userService.registerUser("aa", 0, "aa", "aaa", "aaa", "aaa", AppChannel.WECHAT.name(), "100");
        uri = weChatService.loginWithWeChatUser(userInfo);
        Assert.assertNotNull(uri);
        Assert.assertTrue(uri.getSchemeSpecificPart().contains("token"));
    }

    @Test
    public void test_login_from_registered_wechat_user(){
        WeChatUserInfo userInfo = new WeChatUserInfo();
        String unionid = "1";
        userInfo.setUnionid(unionid);
        URI uri = weChatService.loginWithWeChatUser(userInfo);
        Assert.assertNotNull(uri);
        Assert.assertFalse(uri.toString().contains("token"));
        userService.registerUser("aa", 0, "aa", "1231432143", "aaa", "aaa", AppChannel.WECHAT.name(), unionid);
        uri = weChatService.loginWithWeChatUser(userInfo);
        Assert.assertNotNull(uri);
        Assert.assertTrue(uri.toString().contains("token"));
    }

}