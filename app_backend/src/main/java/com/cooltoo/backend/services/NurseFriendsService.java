package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.converter.NurseFriendBeanConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseFriendsEntity;
import com.cooltoo.constants.AgreeType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.backend.repository.NurseFriendsRepository;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private NurseRepository nurseRepository;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;
    @Autowired
    private NurseFriendBeanConverter beanConverter;

    //======================================================
    //            add friend relation
    //======================================================
    @Transactional
    public void addFriend(long userId, long friendId){
        if(userId == friendId){
            logger.error("user can't be himself friend.");
        }
        insertFriendToDB(userId, friendId, AgreeType.AGREED);
        insertFriendToDB(friendId, userId, AgreeType.WAITING);
    }

    private void insertFriendToDB(long userId, long friendId, AgreeType agreeType){
        validateUserId(userId, friendId);
        long count = friendsRepository.countByUserIdAndFriendId(userId, friendId);
        if(count <= 0){
            NurseFriendsEntity entity = new NurseFriendsEntity();
            entity.setUserId(userId);
            entity.setFriendId(friendId);
            entity.setIsAgreed(agreeType);
            entity.setDateTime(new Date());
            friendsRepository.save(entity);
        }
        else {
            List<NurseFriendsEntity> entity = friendsRepository.findByUserIdAndFriendId(userId, friendId);
            NurseFriendsEntity friend = entity.get(0);
            friend.setIsAgreed(agreeType);
            friend.setDateTime(new Date());
            friendsRepository.save(friend);
        }
    }

    private void validateUserId(long userId, long friendId) {
        if (!nurseRepository.exists(userId) || !nurseRepository.exists(friendId)) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
    }

    //======================================================
    //            delete friend relation
    //======================================================
    @Transactional
    public void removeFriend(long userId, long friendId){
        List<NurseFriendsEntity> entities = null;
        validateUserId(userId, friendId);

        entities = friendsRepository.findByUserIdAndFriendId(userId, friendId);
        if(null!=entities && !entities.isEmpty()){
            friendsRepository.delete(entities);
        }

        entities = friendsRepository.findByUserIdAndFriendId(friendId, userId);
        if(null!=entities && !entities.isEmpty()){
            friendsRepository.delete(entities);
        }
    }

    //======================================================
    //            modify friend relation
    //======================================================
    @Transactional
    public void modifyFriendAgreed(long userId, long friend, AgreeType agreeType) {
        List<NurseFriendsEntity> entities = null;
        NurseFriendsEntity entity = null;
        if (null == agreeType) {
            logger.info("AgreeType is null");
            return;
        } else if (AgreeType.WAITING == agreeType) {
            logger.info("Cannot set to WAITING again");
            return;
        } else if (AgreeType.AGREED == agreeType) {
            entities = friendsRepository.findByUserIdAndFriendId(userId, friend);
        } else {// AgreeType.ACCESS_ZONE_DENY, AgreeType.BLACKLIST;
            entities = friendsRepository.findByUserIdAndFriendId(friend, userId);
        }

        if (null == entities || entities.isEmpty()) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        entity = entities.get(0);
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
        return;
    }

    //======================================================
    //            get friend relation
    //======================================================

    public List<NurseFriendsBean> getFriendsWaitingUserAgree(long userId) {
        List<NurseFriendsEntity> friendsE         = friendsRepository.findByUserId(userId);
        List<NurseFriendsBean>   friendsWaitPassB = new ArrayList<NurseFriendsBean>();
        List<NurseFriendsBean>   friendsB         = convertToNurseFriendsBeans(friendsE);
        friendsB = fillOtherProperties(friendsB);
        for (NurseFriendsBean friend : friendsB) {
            if (AgreeType.WAITING==friend.getIsAgreed()) {
                friendsWaitPassB.add(friend);
            }
        }
        return friendsWaitPassB;
    }

    public List<NurseFriendsBean> getUserWaitingFriendAgreed(long userId) {
        List<NurseFriendsEntity> friendsE         = friendsRepository.findByFriendId(userId);
        List<NurseFriendsBean>   friendsNotAgreeB = new ArrayList<NurseFriendsBean>();
        List<NurseFriendsBean>   friendsB         = convertToNurseFriendsBeans(friendsE);
        friendsB = fillOtherProperties(friendsB);
        for (NurseFriendsBean friend : friendsB) {
            if (AgreeType.WAITING==friend.getIsAgreed()) {
                friendsNotAgreeB.add(friend);
            }
        }
        return friendsNotAgreeB;
    }

    public List<NurseFriendsBean> searchFriends(long userId, String name){
        List<NurseFriendsBean>   filteredFriends = new ArrayList<NurseFriendsBean>();

        List<NurseFriendsEntity> friendsE         = friendsRepository.findByUserId(userId);
        logger.info("search nurse friends is : " +friendsE);
        List<NurseFriendsBean>   friendsB         = convertToNurseFriendsBeans(friendsE);
        friendsB = fillOtherProperties(friendsB);
        for (NurseFriendsBean friendB : friendsB) {
            if (friendB.getFriendName()!=null && friendB.getFriendName().contains(name)) {
                filteredFriends.add(friendB);
            }
        }

        return filteredFriends;
    }

    public List<NurseFriendsBean> getFriends(long userId, long searchId, int pageIdx, int number){
        logger.info("get friends for the user " + searchId);
        boolean                  searchSelf         = userId==searchId;
        PageRequest              pageSort           = new PageRequest(pageIdx, number, Sort.Direction.DESC, "dateTime");
        List<Long>               friendIds          = new ArrayList<Long>();
        List<Long>               friendNotExistIds  = new ArrayList<Long>();
        List<NurseFriendsBean>   searchFriendsB     = new ArrayList<NurseFriendsBean>();
        List<NurseFriendsBean>   userFriendsB       = new ArrayList<NurseFriendsBean>();

        // search self friends
        if (!searchSelf) {
            userFriendsB = getFriend(userId);
        }

        // search searchId's friends
        searchFriendsB = getFriendsAgreeStatusNotWaiting(searchId);
        searchFriendsB = fillOtherProperties(searchFriendsB);

        // set isFriend
        long oneDayAgo = System.currentTimeMillis() - 3600000*24/* one day ago*/;
        for(int i=0, count=searchFriendsB.size(); i < count; i++) {
            NurseFriendsBean searchFriendB = searchFriendsB.get(i);
            // judge is self friends
            if(searchSelf){
                searchFriendB.setIsFriend(true);
                // remove waiting when search self
                if (AgreeType.WAIT_FOR_MY_AGREE.equals(searchFriendB.getWaitFor())
                 || AgreeType.WAIT_FOR_FRIEND_AGREE.equals(searchFriendB.getWaitFor())) {
                    searchFriendsB.remove(i);
                    i --;
                    count --;
                    continue;
                }
            }
            else {
                NurseFriendsBean userFriendB = null;
                for (NurseFriendsBean tmp : userFriendsB) {
                    if (tmp.getFriendId()==searchFriendB.getFriendId()) {
                        userFriendB = tmp;
                        break;
                    }
                }
                if (null!=userFriendB) {
                    if (AgreeType.WAITING.equals(userFriendB.getIsAgreed())) {
                        /**
                         "id": 10032,
                         "userId": 4,
                         "friendId": 3,
                         "headPhotoUrl": "b5/d09630d8e8fb6147a1fa4f2971eda390f1454a",
                         "friendName": "user003",
                         -reset--"isFriend": false,
                         -reset--"dateTime": 1460906623000,
                         -reset--"isAgreed": "WAITING",
                         -reset--"waitFor": "WaitingForFriendAgree",
                         -reset--"waitMoreThan1Day": false
                         */
                        searchFriendB.setIsFriend(false);
                        searchFriendB.setDateTime(userFriendB.getDateTime());
                        long requestTime = (null==searchFriendB.getDateTime()) ? 0 : searchFriendB.getDateTime().getTime();
                        searchFriendB.setIsAgreed(AgreeType.WAITING);
                        searchFriendB.setWaitFor(userFriendB.getWaitFor());
                        searchFriendB.setWaitMoreThan1Day((requestTime-oneDayAgo) < 0);
                    }
                    else {
                        searchFriendB.setIsFriend(true);
                    }
                }
            }
            friendIds.add(searchFriendB.getFriendId());
        }

        List<NurseEntity> friendExist = nurseRepository.findByIdIn(friendIds);
        boolean exist = false;
        for (int i=0, count=searchFriendsB.size(); i < count; i ++) {
            exist = false;
            NurseFriendsBean friend = searchFriendsB.get(i);
            for (NurseEntity nurseExist : friendExist) {
                if (nurseExist.getId()==friend.getFriendId()) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                searchFriendsB.remove(friend);
                count --;
                i     --;
                friendNotExistIds.add(friend.getFriendId());
            }
        }
        if (!friendNotExistIds.isEmpty()) {
            friendsRepository.deleteByFriendIdIn(friendNotExistIds);
            friendsRepository.deleteByUserIdIn(friendNotExistIds);
        }

        List<NurseFriendsBean> retVal = new ArrayList<>();
        int offset = pageIdx*number;
        for (int i=offset, count=searchFriendsB.size(); i<offset+number; i++) {
            if (i >= count) {
                break;
            }
            NurseFriendsBean bean = searchFriendsB.get(i);
            retVal.add(bean);
        }

        return retVal;
    }

    public List<NurseFriendsBean> getFriendsAgreeStatusNotWaiting(long userId) {
        List<NurseFriendsBean> allFriends = getFriend(userId);
        for (int i=0, count=allFriends.size(); i < count; i ++) {
            NurseFriendsBean friendB = allFriends.get(i);
            boolean isWait = false;
            if (AgreeType.WAIT_FOR_FRIEND_AGREE.equals(friendB.getWaitFor())) {
                isWait = true;
            }
            else if (AgreeType.WAIT_FOR_MY_AGREE.equals(friendB.getWaitFor())) {
                isWait = true;
            }
            if (isWait) {
                allFriends.remove(i);
                i --;
                count --;
            }
        }
        return allFriends;
    }

    public List<NurseFriendsBean> getFriend(long userId) {
        if (!nurseRepository.exists(userId)) {
            List<Long> userIds = new ArrayList<Long>();
            userIds.add(userId);
            friendsRepository.deleteByFriendIdIn(userIds);
            friendsRepository.deleteByUserIdIn(userIds);
            return new ArrayList<>();
        }

        List<NurseFriendsBean> userFriendsB   = new ArrayList<>();

        // get userId 's friend relationship
        Sort                     sort         = new Sort(new Sort.Order(Sort.Direction.DESC, "dateTime"));
        List<NurseFriendsEntity> friendsMapE  = friendsRepository.findByUserIdOrFriendId(userId, userId, sort);
        if (null!=friendsMapE && !friendsMapE.isEmpty()) {
            List<NurseFriendsEntity> agreeTypeNull= new ArrayList<>();
            List<NurseFriendsEntity> friendsUserE = new ArrayList<>();
            for (NurseFriendsEntity friendMap : friendsMapE) {
                if (null==friendMap.getIsAgreed()) {
                    agreeTypeNull.add(friendMap);
                    continue;
                }
                if (friendMap.getUserId()==userId) {
                    NurseFriendsBean bean = beanConverter.convert(friendMap);
                    userFriendsB.add(bean);
                }
                else {
                    friendsUserE.add(friendMap);
                }
            }
            friendsRepository.delete(agreeTypeNull);

            // remove friend agree status is null
            for (NurseFriendsEntity friendUser : friendsUserE) {
                for (int i=0; i<userFriendsB.size(); i++) {
                    NurseFriendsBean userFriend = userFriendsB.get(i);
                    if (userFriend.getFriendId()==friendUser.getUserId()) {
                        if (AgreeType.WAITING.equals(userFriend.getIsAgreed())) {
                            userFriend.setWaitFor(AgreeType.WAIT_FOR_MY_AGREE);
                        }
                        else if (AgreeType.WAITING.equals(friendUser.getIsAgreed())){
                            userFriend.setWaitFor(AgreeType.WAIT_FOR_FRIEND_AGREE);
                            userFriend.setIsAgreed(AgreeType.WAITING);
                        }
                        break;
                    }
                }
            }
            return userFriendsB;
        }
        return new ArrayList<>();
    }

    public int getFriendsCount(long userId){
        return getFriendsAgreeStatusNotWaiting(userId).size();
    }


    private List<NurseFriendsBean> convertToNurseFriendsBeans(List<NurseFriendsEntity> entities) {
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

        List<Long>             friendIds = new ArrayList<Long>();
        List<NurseEntity>      nurses    = null;
        List<Long>             imageIds  = new ArrayList<Long>();
        Map<Long, String>      img2URLs  = null;

        for(NurseFriendsBean bean : beans){
            friendIds.add(bean.getFriendId());
        }

        nurses = nurseRepository.findByIdIn(friendIds);
        for (NurseEntity nurse : nurses) {
            imageIds.add(nurse.getProfilePhotoId());
        }
        img2URLs = storageService.getFilePath(imageIds);

        // set friend name and profile photo image url
        String friendName     = null;
        String profilePhoto   = null;
        Long   profilePhotoId = -1L;
        for (NurseFriendsBean nurseFriend : beans) {
            for (NurseEntity nurse : nurses) {
                if (nurseFriend.getFriendId()==nurse.getId()) {
                    friendName     = nurse.getName();
                    profilePhotoId = nurse.getProfilePhotoId();
                    profilePhoto   = img2URLs.get(profilePhotoId);

                    nurseFriend.setFriendName(friendName);
                    nurseFriend.setHeadPhotoUrl(profilePhoto);
                    break;
                }
            }
        }
        return beans;
    }
}
