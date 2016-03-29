package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.services.NurseFriendsService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
@Transactional
public class NurseFriendsServiceTest extends AbstractCooltooTest {

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

        friendList = friendsService.getFriendList(1);
        Assert.assertEquals(3, friendList.size());

        friendsService.addFriend(4,1);
        friendList = friendsService.getFriendList(4);
        Assert.assertEquals(1, friendList.size());
        friendList = friendsService.getFriendList(1);
        Assert.assertEquals(3, friendList.size());
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
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_friends_page_data.xml")
    public void testGetFriends(){
        List<NurseFriendsBean> friends = friendsService.getFriends(1, 1, 0, 3);
        Assert.assertEquals(3, friends.size());
        Assert.assertEquals(1, friends.get(0).getFriendId());

        friends = friendsService.getFriends(1, 1, 1, 5);
        Assert.assertEquals(5, friends.size());
        Assert.assertEquals(6, friends.get(0).getFriendId());


        friends = friendsService.getFriends(1, 2, 0, 5);
        Assert.assertEquals(1, friends.size());
        Assert.assertTrue(friends.get(0).getIsFriend());

        friends = friendsService.getFriends(1, 3, 0, 5);
        Assert.assertEquals(1, friends.size());
        Assert.assertFalse(friends.get(0).getIsFriend());
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_friends_page_data.xml")
    public void testSearchFriends(){
        List<NurseFriendsBean> friends = null;

        friends = friendsService.searchFriends(1, "4");
        System.out.println(friends);
        Assert.assertEquals(2, friends.size());

        friends = friendsService.searchFriends(1, "护士");
        System.out.println(friends);
        Assert.assertEquals(14, friends.size());

    }
}
