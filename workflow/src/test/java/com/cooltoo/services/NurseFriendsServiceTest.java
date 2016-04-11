package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.services.NurseFriendsService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 3/10/16.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_data.xml"),
        @DatabaseSetup(value = "classpath:/com/cooltoo/services/nurse_friends_page_data.xml")
})
public class NurseFriendsServiceTest extends AbstractCooltooTest {

    public static final Logger logger = LoggerFactory.getLogger(NurseFriendsServiceTest.class.getName());

    @Autowired
    private NurseFriendsService friendsService;

    @Test
    public void testAddFriend(){
        long nurseId = 15;
        long friendId= 1;
        List<NurseFriendsBean> friendList = friendsService.getFriend(nurseId);
        Assert.assertTrue(friendList.isEmpty());
        friendsService.addFriend(nurseId,1);
        friendList = friendsService.getFriend(nurseId);
        Assert.assertEquals(1, friendList.size());
        Assert.assertEquals(friendId, friendList.get(0).getFriendId());

        friendList = friendsService.getFriend(1);
        Assert.assertEquals(15, friendList.size());

        nurseId = 15;
        friendsService.addFriend(nurseId,1);
        friendList = friendsService.getFriend(nurseId);
        Assert.assertEquals(1, friendList.size());
        friendList = friendsService.getFriend(1);
        Assert.assertEquals(15, friendList.size());
    }

    @Test
    public void testDeleteFriend(){
        List<NurseFriendsBean> friendList = friendsService.getFriend(1);
        Assert.assertEquals(14, friendList.size());
        friendsService.removeFriend(1, 2);
        friendList = friendsService.getFriend(1);
        Assert.assertEquals(13, friendList.size());
        friendsService.removeFriend(1, 3);
        friendList = friendsService.getFriend(1);
        Assert.assertEquals(12, friendList.size());
    }

    @Test
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
    public void testSearchFriends(){
        List<NurseFriendsBean> friends = null;

        friends = friendsService.searchFriends(1, "4");
        logger.info(friends.toString());
        Assert.assertEquals(2, friends.size());

        friends = friendsService.searchFriends(1, "护士");
       logger.info(friends.toString());
        Assert.assertEquals(14, friends.size());

    }
}
