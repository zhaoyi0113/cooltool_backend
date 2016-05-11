package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.converter.NurseFriendBeanConverter;
import com.cooltoo.backend.entities.NurseFriendsEntity;
import com.cooltoo.constants.AgreeType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.backend.repository.NurseFriendsRepository;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 3/10/16.
 */
@Service("NurseFriendsService")
public class NurseFriendsService {

    private static final Logger logger = LoggerFactory.getLogger(NurseFriendsService.class.getName());

    @Autowired
    private NurseFriendsRepository friendsRepository;
    @Autowired
    private NurseService nurseService;
    @Autowired
    private NurseFriendBeanConverter beanConverter;

    //======================================================
    //            add friend relation
    //======================================================
    @Transactional
    public boolean addFriendship(long userId, long friendId){
        if(userId == friendId){
            logger.error("user can't be himself friend.");
        }
        return addFriendshipToDB(userId, friendId);
    }

    private boolean addFriendshipToDB(long userId, long friendId){
        validateUserId(userId, friendId);

        List<NurseFriendsEntity> friendships = friendsRepository.findFriendshipByUserIdAndFriendId(userId, friendId);
        if (!VerifyUtil.isListEmpty(friendships)) {
            logger.info("user {} and other {} has friendship already", userId, friendId);
            friendsRepository.delete(friendships);
        }
        // the friendship data is invalid or none
        Date datetime = new Date();
        NurseFriendsEntity entity = new NurseFriendsEntity();
        entity.setUserId(userId);
        entity.setFriendId(friendId);
        entity.setIsAgreed(AgreeType.AGREED);
        entity.setDateTime(datetime);
        friendsRepository.save(entity);

        entity = new NurseFriendsEntity();
        entity.setUserId(friendId);
        entity.setFriendId(userId);
        entity.setIsAgreed(AgreeType.WAITING);
        entity.setDateTime(datetime);
        friendsRepository.save(entity);
        logger.info("user {} add friendship to other {}", userId, friendId);
        return true;
    }

    private void validateUserId(long userId, long friendId) {
        logger.info("validate user {} and {}", userId, friendId);
        if (!nurseService.existNurse(userId) || !nurseService.existNurse(friendId)) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
    }

    //======================================================
    //            modify friend relation
    //======================================================
    @Transactional
    public AgreeType modifyFriendAgreed(long userId, long friend, AgreeType agreeType) {
        List<NurseFriendsEntity> entities = null;
        NurseFriendsEntity entity = null;
        if (null == agreeType) {
            logger.info("AgreeType is null");
            return null;
        } else if (AgreeType.WAITING == agreeType) {
            logger.info("Cannot set to WAITING again");
            return null;
        } else if (AgreeType.AGREED == agreeType) {
            entities = friendsRepository.findByUserIdAndFriendId(userId, friend);
        } else {// AgreeType.ACCESS_ZONE_DENY, AgreeType.BLACKLIST;
            entities = friendsRepository.findByUserIdAndFriendId(friend, userId);
        }

        if (null == entities || entities.isEmpty()) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        entity = entities.get(0);
        // toggle setting (AgreeType.ACCESS_ZONE_DENY, AgreeType.BLACKLIST;) to AgreeType.AGREED
        boolean isLimit1    = (AgreeType.ACCESS_ZONE_DENY==entity.getIsAgreed()
                             ||AgreeType.BLACKLIST       ==entity.getIsAgreed());
        boolean isLimit2    = (AgreeType.ACCESS_ZONE_DENY==agreeType
                             ||AgreeType.BLACKLIST       ==agreeType);
        if (isLimit1 && isLimit2) {
            agreeType = AgreeType.AGREED;
        }
        entity.setIsAgreed(agreeType);

        entity = friendsRepository.save(entity);
        NurseFriendsBean bean = beanConverter.convert(entity);
        return agreeType;
    }

    //======================================================
    //            get friend relation
    //======================================================

    public long countFriendship(long userId) {
        long count = friendsRepository.countFriendship(userId);
        logger.info("get user {} friend count {}", userId, count);
        return count;
    }

    public List<NurseFriendsBean> getFriendshipAgreed(long userId) {
        logger.info("user {} get friendship agreed", userId);
        List<NurseFriendsEntity> friendsE = getFriendship(userId);
        List<NurseFriendsBean>   friendsAgreedB = entities2beans(friendsE);
        fillOtherProperties(friendsAgreedB);
        logger.info("count {}", friendsAgreedB.size());
        return friendsAgreedB;
    }

    public List<NurseFriendsBean> getFriendshipWaitingAgreed(long userId) {
        logger.info("user {} get friendship waiting agreed", userId);
        List<NurseFriendsEntity> friendsE         = getFriendshipWaitingAgree(userId);
        List<NurseFriendsBean>   friendsNotAgreeB = entities2beans(friendsE);
        fillOtherProperties(friendsNotAgreeB);
        logger.info("count {}", friendsNotAgreeB.size());
        return friendsNotAgreeB;
    }

    public List<NurseFriendsBean> getFriendship(long currentUserId, long userSearchedId, int pageIndex, int number) {
        logger.info("user {} get others {} 's friendship at page={}, {}/page", currentUserId, userSearchedId, pageIndex, number);
        if (currentUserId == userSearchedId) {
            return getFriendship(currentUserId, pageIndex, number);
        }

        PageRequest page = new PageRequest(pageIndex, number, Sort.Direction.DESC, "dateTime");
        List<Long> userSearchedFriendshipIds = getFriendshipIds(userSearchedId, page);
        if (VerifyUtil.isListEmpty(userSearchedFriendshipIds)) {
            return new ArrayList<>();
        }

        List<NurseFriendsBean> currentUsersFriendship2SearchedFriends = getFriendshipWithOthers(currentUserId, userSearchedFriendshipIds);
        fillOtherProperties(currentUsersFriendship2SearchedFriends);
        logger.info("count {}", currentUsersFriendship2SearchedFriends.size());
        return currentUsersFriendship2SearchedFriends;
    }

    public List<NurseFriendsBean> getFriendship(long userId, String strOtherIds) {
        logger.info("get user {} 's friendship with others={}", userId, strOtherIds);
        List<Long> otherIds = VerifyUtil.parseLongIds(strOtherIds);
        List<NurseFriendsBean> currentUsersFriendship2SearchedFriends = getFriendshipWithOthers(userId, otherIds);
        fillOtherProperties(currentUsersFriendship2SearchedFriends);
        return currentUsersFriendship2SearchedFriends;
    }

    public List<NurseFriendsBean> getFriendshipByFuzzyName(long userId, String fuzzyName, int pageIndex, int sizePerPage) {
        logger.info("get user {} 's friendship with fuzzy name={}", fuzzyName);
        List<Long> otherIds = nurseService.getNurseIdsByName(fuzzyName, pageIndex, sizePerPage);
        if (otherIds.contains(userId)) {
            otherIds.remove(userId);
        }
        List<NurseFriendsBean> currentUsersFriendship2SearchedFriends = getFriendshipWithOthers(userId, otherIds);
        fillOtherProperties(currentUsersFriendship2SearchedFriends);
        return currentUsersFriendship2SearchedFriends;
    }

    public List<NurseFriendsBean> getFriendship(long userId, List<Long> otherIds) {
        logger.info("get user {} 's friendship with others={}", userId, otherIds);
        List<NurseFriendsBean> currentUsersFriendship2SearchedFriends = getFriendshipWithOthers(userId, otherIds);
        fillOtherProperties(currentUsersFriendship2SearchedFriends);
        return currentUsersFriendship2SearchedFriends;

    }

    private List<NurseFriendsBean> getFriendship(long userId, int pageIndex, int number) {
        PageRequest page = new PageRequest(pageIndex, number, Sort.Direction.DESC, "dateTime");
        List<NurseFriendsEntity> friends  = getFriendshipByPage(userId, page);
        List<NurseFriendsBean>   friendsB = entities2beans(friends);
        for (NurseFriendsBean friend : friendsB) {
            friend.setIsFriend(true);
        }
        fillOtherProperties(friendsB);
        return friendsB;
    }

    private List<NurseFriendsBean> getFriendshipWithOthers(long userId, List<Long> othersIds) {
        logger.info("get friendship of user {} and others {}", userId, othersIds);
        if (VerifyUtil.isListEmpty(othersIds)) {
            logger.info("others ids is empty");
            return new ArrayList<>();
        }
        List<NurseFriendsEntity> userAgreedAndWaiting  = findFriendshipAgreedAndWaiting(userId, othersIds);
        if (VerifyUtil.isListEmpty(userAgreedAndWaiting)) {
            userAgreedAndWaiting = new ArrayList<>();
        }
        List<Long> userWaitingAgreedIds = findFriendshipUserWaitingAgreedIds(userId, othersIds);
        if (VerifyUtil.isListEmpty(userWaitingAgreedIds)) {
            userWaitingAgreedIds = new ArrayList<>();
        }

        long notFriend = 0;
        long bothAgreed = 0;
        long userWaiting = 0;
        long friendWaiting = 0;
        List<Long>             userAgreedAndWaitingIds   = new ArrayList<>();
        List<NurseFriendsBean> userAgreedAndWaitingBeans = entities2beans(userAgreedAndWaiting);
        // one day ago
        long oneDayAgo = System.currentTimeMillis() - 3600000*24;
        for (NurseFriendsBean friendsBean : userAgreedAndWaitingBeans) {
            boolean isWaiting = false;
            if (userWaitingAgreedIds.contains(friendsBean.getFriendId())) {
                friendsBean.setIsAgreed(AgreeType.WAITING);
                friendsBean.setWaitFor(AgreeType.WAIT_FOR_FRIEND_AGREE);
                isWaiting = true;
                userWaiting ++;
            }
            else {
                if (friendsBean.getIsAgreed()==AgreeType.WAITING) {
                    friendsBean.setWaitFor(AgreeType.WAIT_FOR_MY_AGREE);
                    isWaiting = true;
                    friendWaiting ++;
                }
                else {
                    friendsBean.setIsFriend(true);
                    bothAgreed ++;
                }
            }

            if (isWaiting) {
                long requestTime = friendsBean.getDateTime().getTime();
                friendsBean.setWaitMoreThan1Day((requestTime-oneDayAgo) < 0);
            }
            userAgreedAndWaitingIds.add(friendsBean.getFriendId());
        }

        for (Long userSearchedFriendId : othersIds) {
            if (userAgreedAndWaitingIds.contains(userSearchedFriendId)) {
                continue;
            }
            NurseFriendsBean notFriendBean = new NurseFriendsBean();
            notFriendBean.setUserId(userId);
            notFriendBean.setFriendId(userSearchedFriendId);
            notFriendBean.setIsFriend(false);
            notFriendBean.setWaitMoreThan1Day(true);
            userAgreedAndWaitingBeans.add(notFriendBean);
            notFriend ++;
        }

        logger.info("bothAgreed={}个 friendWaiting={}个 userWaiting={}个 notFriend={}个",
                bothAgreed, friendWaiting, userWaiting, notFriend);
        return userAgreedAndWaitingBeans;
    }

    public List<NurseFriendsBean> searchFriendshipByName(long userId, String name){
        logger.info("get user {} friend name like {}", userId, name);
        List<NurseFriendsEntity> friendsE = getFriendshipByNameLike(userId, "%"+name+"%");
        List<NurseFriendsBean>   friendsB = entities2beans(friendsE);
        fillOtherProperties(friendsB);
        return friendsB;
    }

    private List<NurseFriendsEntity> getFriendshipByPage(long userId, PageRequest page) {
        return friendsRepository.findFriendshipByPage(userId, page);
    }

    private List<NurseFriendsEntity> getFriendship(long userId) {
        return friendsRepository.findFriendshipAgreed(userId);
    }

    private List<Long> getFriendshipIds(long userId, Pageable page) {
        return friendsRepository.findFriendshipAgreedIds(userId, page);
    }

    private List<NurseFriendsEntity> findFriendshipAgreedAndWaiting(long userId, List<Long> othersIds) {
        return friendsRepository.findAgreedAndWaitingIds(userId, othersIds);
    }

    private List<Long> findFriendshipUserWaitingAgreedIds(long userId, List<Long> othersIds) {
        return friendsRepository.findUserWaitingAgreeIds(userId, othersIds);
    }

    private List<NurseFriendsEntity> getFriendshipWaitingAgree(long userId) {
        return friendsRepository.findFriendshipWaitAgree(userId);
    }

    private List<NurseFriendsEntity> getFriendshipByNameLike(long userId, String nameLike) {
        return friendsRepository.findFriendsByName(userId, nameLike);
    }

    private List<NurseFriendsBean> entities2beans(List<NurseFriendsEntity> entities) {
        if(null==entities || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseFriendsBean> beans     = new ArrayList<NurseFriendsBean>();
        for(NurseFriendsEntity entity : entities){
            NurseFriendsBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }

        return fillOtherProperties(beans);
    }

    private List<NurseFriendsBean> fillOtherProperties(List<NurseFriendsBean> beans) {
        if(null==beans || beans.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long>        friendIds = new ArrayList<Long>();
        List<NurseBean>   nurses    = null;

        for(NurseFriendsBean bean : beans){
            friendIds.add(bean.getFriendId());
        }
        nurses = nurseService.getNurse(friendIds);

        // set friend name and profile photo image url
        for (NurseFriendsBean nurseFriend : beans) {
            for (NurseBean nurse : nurses) {
                if (nurseFriend.getFriendId()==nurse.getId()) {
                    nurseFriend.setFriendName(nurse.getName());
                    nurseFriend.setHeadPhotoUrl(nurse.getProfilePhotoUrl());
                    break;
                }
            }
        }
        return beans;
    }
}
