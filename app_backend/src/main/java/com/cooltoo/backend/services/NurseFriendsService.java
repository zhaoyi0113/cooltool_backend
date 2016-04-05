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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
            friendsRepository.save(entity);
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
        validateUserId(userId, friendId);
        List<NurseFriendsEntity> entities = friendsRepository.findByUserIdAndFriendId(userId, friendId);
        if(!entities.isEmpty()){
            friendsRepository.delete(entities.get(0).getId());
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
        if (isLimit1 && isLimit1) {
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
        List<NurseFriendsBean>   friendsB         = convertToNurseFriendsBeans(friendsE);
        List<NurseFriendsBean>   friendsWaitPassB = new ArrayList<NurseFriendsBean>();
        for (NurseFriendsBean friend : friendsB) {
            if (AgreeType.WAITING==friend.getIsAgreed()) {
                friendsWaitPassB.add(friend);
            }
        }
        return friendsWaitPassB;
    }

    public List<NurseFriendsBean> getUserWaitingFriendAgreed(long userId) {
        List<NurseFriendsEntity> friendsE         = friendsRepository.findByFriendId(userId);
        List<NurseFriendsBean>   friendsB         = convertToNurseFriendsBeans(friendsE);
        List<NurseFriendsBean>   friendsNotAgreeB = new ArrayList<NurseFriendsBean>();
        for (NurseFriendsBean friend : friendsB) {
            if (AgreeType.WAITING==friend.getIsAgreed()) {
                friendsNotAgreeB.add(friend);
            }
        }
        return friendsNotAgreeB;
    }

    public List<NurseFriendsBean> getFriendList(long userId){
        List<NurseFriendsEntity> entities = friendsRepository.findByUserId(userId);
        return convertToNurseFriendsBeans(entities);
    }

    public List<NurseFriendsBean> searchFriends(long userId, String name){
        List<NurseFriendsBean>   friendsB        = null;
        List<NurseFriendsBean>   filteredFriends = new ArrayList<NurseFriendsBean>();

        List<NurseFriendsEntity> friends         = friendsRepository.findByUserId(userId);
        logger.info("search nurse friends is : " +friends);

        friendsB = convertToNurseFriendsBeans(friends);
        for (NurseFriendsBean friendB : friendsB) {
            if (friendB.getFriendName().contains(name)) {
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
        List<NurseFriendsBean>   userFriends        = new ArrayList<NurseFriendsBean>();
        List<NurseFriendsBean>   searchFriends      = new ArrayList<NurseFriendsBean>();
        Page<NurseFriendsEntity> searchFriendsPE    = null;
        List<NurseFriendsEntity> searchFriendsE     = new ArrayList<NurseFriendsEntity>();

        // search self friends
        if (!searchSelf) {
            List<NurseFriendsEntity> userFriendsE = friendsRepository.findByUserId(userId);
            userFriends = convertToNurseFriendsBeans(userFriendsE);
        }

        // search searchId's friends
        searchFriendsPE = friendsRepository.findNurseFriendByUserId(searchId, pageSort);
        for (NurseFriendsEntity searchFriendE: searchFriendsPE) {
            searchFriendsE.add(searchFriendE);
        }

        searchFriends = convertToNurseFriendsBeans(searchFriendsE);
        for(NurseFriendsBean searchFriend: searchFriends){
            // jump over not agreed of user who been searched
            //if (AgreeType.AGREED!=searchFriendE.getIsAgreed()) {
            //    continue;
            //}

            friendIds.add(searchFriend.getFriendId());
            // judge is self friends
            if(searchSelf){
                searchFriend.setIsFriend(true);
            }else {
                for (NurseFriendsBean userF : userFriends) {
                    if (userF.getFriendId() == searchFriend.getFriendId()) {
                        searchFriend.setIsFriend(true);
                        break;
                    }
                }
            }
        }

        List<NurseEntity> friendExist = nurseRepository.findNurseByIdIn(friendIds);
        boolean exist = false;
        for (int i=0, count=searchFriends.size(); i < count; i ++) {
            exist = false;
            NurseFriendsBean friend = searchFriends.get(i);
            for (NurseEntity nurseExist : friendExist) {
                if (nurseExist.getId()==friend.getFriendId()) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                searchFriends.remove(friend);
                count --;
                i     --;
                friendNotExistIds.add(friend.getFriendId());
            }
        }
        if (!friendNotExistIds.isEmpty()) {
            friendsRepository.deleteByFriendIdIn(friendNotExistIds);
            friendsRepository.deleteByUserIdIn(friendNotExistIds);
        }

        return searchFriends;
    }

    public int getFriendsCount(long userId){
        return getFriendList(userId).size();
    }

    private List<NurseFriendsBean> convertToNurseFriendsBeans(List<NurseFriendsEntity> entities) {
        List<Long>             friendIds = new ArrayList<Long>();
        List<NurseEntity>      nurses    = null;
        List<Long>             imageIds  = new ArrayList<Long>();
        Map<Long, String>      img2URLs  = null;

        List<NurseFriendsBean> beans     = new ArrayList<NurseFriendsBean>();
        for(NurseFriendsEntity entity : entities){
            NurseFriendsBean bean = beanConverter.convert(entity);
            beans.add(bean);
            friendIds.add(bean.getFriendId());
        }

        nurses = nurseRepository.findNurseByIdIn(friendIds);
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
