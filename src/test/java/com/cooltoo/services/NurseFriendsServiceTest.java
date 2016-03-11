package com.cooltoo.services;

import com.cooltoo.beans.NurseFriendsBean;
import com.cooltoo.serivces.NurseFriendsService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public class NurseFriendsServiceTest extends AbstractCooltooTest{

    @Autowired
    private NurseFriendsService friendsService;

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_friends_data.xml")
    public void testAddFriend(){
        List<NurseFriendsBean> friendList = friendsService.getFriendList(4);
        Assert.assertTrue(friendList.isEmpty());
        friendsService.addFriend(4,1);
        friendList = friendsService.getFriendList(4);
        Assert.assertEquals(1, friendList.size());
        Assert.assertEquals(1, friendList.get(0).getFriendId());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_friends_data.xml")
    public void testDeleteFriend(){
        List<NurseFriendsBean> friendList = friendsService.getFriendList(1);
        Assert.assertEquals(2, friendList.size());
        friendsService.removeFriend(1, 2);
        friendList = friendsService.getFriendList(1);
        Assert.assertEquals(1, friendList.size());
        friendsService.removeFriend(1, 3);
        friendList = friendsService.getFriendList(1);
        Assert.assertEquals(0, friendList.size());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_friends_data.xml")
    public void testSearch(){
        List<NurseFriendsBean> friends = friendsService.searchFriends(1, "name");
        Assert.assertEquals(2, friends.size());
        friends = friendsService.searchFriends(1, "e");
        Assert.assertEquals(2, friends.size());
        friendsService.addFriend(1, 4);
        friends = friendsService.searchFriends(1, "name");
        Assert.assertEquals(3, friends.size());
//        friends = friendsService.searchFriends(1, "name3");
//        Assert.assertEquals(2, friends.size());
    }
}
