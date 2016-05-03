package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.entities.NurseFriendsEntity;
import com.cooltoo.backend.repository.NurseFriendsRepository;
import com.cooltoo.backend.services.NurseFriendsService;
import com.cooltoo.constants.AgreeType;
import com.cooltoo.util.VerifyUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    @Autowired
    private NurseFriendsRepository friendsRepository;

    @Test
    public void testGetAgreedAndGetWaitingAndAddFriendAndModifyFriendAgreed(){
        long nurseId = 1;
        long friendId = 15;
        List<NurseFriendsBean> friendAgreed  = friendsService.getFriendshipAgreed(nurseId);
        List<NurseFriendsBean> friendWaiting = friendsService.getFriendshipWaitingAgreed(nurseId);
        Assert.assertEquals(11, friendAgreed.size());
        Assert.assertEquals(1, friendWaiting.size());

        friendsService.addFriendship(friendId, nurseId);

        friendAgreed  = friendsService.getFriendshipAgreed(nurseId);
        friendWaiting = friendsService.getFriendshipWaitingAgreed(nurseId);
        Assert.assertEquals(11, friendAgreed.size());
        Assert.assertEquals(2, friendWaiting.size());

        friendAgreed  = friendsService.getFriendshipAgreed(friendId);
        friendWaiting = friendsService.getFriendshipWaitingAgreed(friendId);
        Assert.assertEquals(1, friendAgreed.size());
        Assert.assertEquals(0, friendWaiting.size());

        friendsService.modifyFriendAgreed(nurseId, friendId, AgreeType.AGREED);

        friendAgreed  = friendsService.getFriendshipAgreed(nurseId);
        friendWaiting = friendsService.getFriendshipWaitingAgreed(nurseId);
        Assert.assertEquals(12, friendAgreed.size());
        Assert.assertEquals(1, friendWaiting.size());

        friendAgreed  = friendsService.getFriendshipAgreed(friendId);
        friendWaiting = friendsService.getFriendshipWaitingAgreed(friendId);
        Assert.assertEquals(2, friendAgreed.size());
        Assert.assertEquals(0, friendWaiting.size());
    }

    @Test
    public void testGetFriendshipByUserIdAndUserSearchedId(){
        long userId = 3L;
        long userSearchedId = 1L;
        List<NurseFriendsBean> friendship = friendsService.getFriendship(userId, userSearchedId, 0, 15);
        Assert.assertEquals(11, friendship.size());

        friendship = friendsService.getFriendship(userId, userSearchedId, 2, 5);
        Assert.assertEquals(1, friendship.size());
        Assert.assertEquals(12, friendship.get(0).getFriendId());
    }

    @Test
    public void testGetFriendshipByOthersIds(){
        long userId = 1L;
        String otherIds = "3,13,14,15";
        List<NurseFriendsBean> friends = friendsService.getFriendship(userId, otherIds);
        Assert.assertEquals(4, friends.size());
        Assert.assertEquals(3, friends.get(0).getFriendId());
        Assert.assertEquals(13, friends.get(1).getFriendId());
        Assert.assertEquals(14, friends.get(2).getFriendId());
        Assert.assertEquals(15, friends.get(3).getFriendId());

        Assert.assertEquals(true, friends.get(0).getIsFriend());
        Assert.assertEquals(AgreeType.AGREED, friends.get(0).getIsAgreed());

        Assert.assertEquals(false, friends.get(1).getIsFriend());
        Assert.assertEquals(AgreeType.WAITING, friends.get(1).getIsAgreed());
        Assert.assertEquals(AgreeType.WAIT_FOR_MY_AGREE, friends.get(1).getWaitFor());
        Assert.assertEquals(true, friends.get(1).isWaitMoreThan1Day());

        Assert.assertEquals(false, friends.get(2).getIsFriend());
        Assert.assertEquals(AgreeType.WAITING, friends.get(2).getIsAgreed());
        Assert.assertEquals(AgreeType.WAIT_FOR_FRIEND_AGREE, friends.get(2).getWaitFor());
        Assert.assertEquals(true, friends.get(2).isWaitMoreThan1Day());

        Assert.assertEquals(false, friends.get(3).getIsFriend());
    }

    @Test
    public void testSearchFriends(){
        List<NurseFriendsBean> friends = null;

        friends = friendsService.getFriendship(1, 1, 3, 3);
        Assert.assertEquals(2, friends.size());
        Assert.assertEquals(11,friends.get(0).getFriendId());
        Assert.assertEquals(12,friends.get(1).getFriendId());

        Assert.assertEquals(true, friends.get(0).getIsFriend());
        Assert.assertEquals(AgreeType.AGREED, friends.get(0).getIsAgreed());

        Assert.assertEquals(true, friends.get(1).getIsFriend());
        Assert.assertEquals(AgreeType.AGREED, friends.get(1).getIsAgreed());
    }

    //=======================================================================
    //           test repository
//    List<NurseFriendsEntity> findByUserIdAndFriendId(long userId, long friendId);
//    long countByUserIdAndFriendId(long userId, long friendId);
//    long countFriendship(long userId);
//    List<NurseFriendsEntity> findFriendshipByPage(long userId, Pageable pageable);
//    List<NurseFriendsEntity> findFriendshipAgreed(long userId);
//    List<NurseFriendsEntity> findFriendshipWaitAgree(long userId);
//    List<Long> findFriendshipAgreedIds(long userId, Pageable pageable);
//    List<NurseFriendsEntity> findAgreedAndWaitingIds(long userId, List<Long> others);
//    List<Long> findUserWaitingAgreeIds(long userId, List<Long> others);
//    List<NurseFriendsEntity> findFriendsByName(@Param("userId") long userId, @Param("name") String nameLike);
//    void deleteFriendship(long userId, long friendId);
    //=======================================================================

    @Test
    public void testFindFriendship () {
        List<NurseFriendsEntity> friendships = friendsRepository.findFriendshipAgreed(1L);
        Assert.assertEquals(11, friendships.size());
    }

    @Test
    public void testFindByUserIdAndFriendId() {
        List<NurseFriendsEntity> friendships = friendsRepository.findByUserIdAndFriendId(1L, 2L);
        Assert.assertEquals(1, friendships.size());
    }

    @Test
    public void testCountByUserIdAndFriendId() {
        long count = friendsRepository.countByUserIdAndFriendId(1L, 2L);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testCountFriendship() {
        long count = friendsRepository.countFriendship(1L);
        Assert.assertEquals(11, count);
    }

    @Test
    public void testFindFriendshipByPage() {
        PageRequest pageRequest = new PageRequest(0, 8, Sort.Direction.DESC, "dateTime");
        List<NurseFriendsEntity> page = friendsRepository.findFriendshipByPage(1L, pageRequest);
        Assert.assertEquals(8, page.size());

        pageRequest = new PageRequest(1, 8, Sort.Direction.DESC, "dateTime");
        page = friendsRepository.findFriendshipByPage(1L, pageRequest);
        Assert.assertEquals(3, page.size());
    }

    @Test
    public void testFindFriendshipAgreed() {
        List<NurseFriendsEntity> friendships = friendsRepository.findFriendshipAgreed(1L);
        Assert.assertEquals(11, friendships.size());
    }

    @Test
    public void testFindFriendWaitAgree() {
        List<NurseFriendsEntity> friendships = friendsRepository.findFriendshipWaitAgree(1L);
        Assert.assertEquals(1, friendships.size());
        Assert.assertEquals(13, friendships.get(0).getFriendId());
    }

    @Test
    public void testFindFriendshipAgreedIds() {
        PageRequest pageRequest = new PageRequest(0, 8, Sort.Direction.DESC, "dateTime");
        List<Long> page = friendsRepository.findFriendshipAgreedIds(1L, pageRequest);
        Assert.assertEquals(8, page.size());

        pageRequest = new PageRequest(1, 8, Sort.Direction.DESC, "dateTime");
        page = friendsRepository.findFriendshipAgreedIds(1L, pageRequest);
        Assert.assertEquals(3, page.size());
    }

    @Test
    public void testFindAgreedAndWaitingIds() {
        List<Long> ids = VerifyUtil.parseLongIds("2,3,4,13,14");
        List<NurseFriendsEntity> page = friendsRepository.findAgreedAndWaitingIds(1L, ids);
        Assert.assertEquals(5, page.size());
    }

    @Test
    public void testFindUserWaitingAgreeIds() {
        List<Long> ids = VerifyUtil.parseLongIds("2,3,4,13,14");
        List<Long> page = friendsRepository.findUserWaitingAgreeIds(1L, ids);
        Assert.assertEquals(1, page.size());
        Assert.assertEquals(14L, page.get(0).longValue());
    }

    @Test
    public void testDeleteFriendship() {
        long count = friendsRepository.countByUserIdAndFriendId(1L, 2L);
        Assert.assertEquals(1, count);

        friendsRepository.deleteFriendship(1L, 2L);

        count = friendsRepository.countByUserIdAndFriendId(1L, 2L);
        Assert.assertEquals(0, count);
    }

    @Test
    public void testFindByName() {
        List<NurseFriendsEntity> friends = friendsRepository.findFriendsByName(1L, "%name1%");
        Assert.assertEquals(3, friends.size());

        friends = friendsRepository.findFriendsByName(1L, "%name1% or 1=1");
        System.out.println(friends);
    }
}
