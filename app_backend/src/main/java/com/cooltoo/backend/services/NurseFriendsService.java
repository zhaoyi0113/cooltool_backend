package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.converter.NurseFriendBeanConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseFriendsEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.backend.repository.NurseFriendsRepository;
import com.cooltoo.backend.repository.NurseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 3/10/16.
 */
@Service("NurseFriendsService")
public class NurseFriendsService {

    private static final Logger logger = Logger.getLogger(NurseFriendsService.class.getName());

    @Autowired
    private NurseFriendsRepository friendsRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private NurseFriendBeanConverter beanConverter;

    @Transactional
    public void addFriend(long userId, long friendId){
        if(userId == friendId){
            logger.severe("user can't be himself friend.");
        }
        insertFriendToDB(userId, friendId);
        insertFriendToDB(friendId, userId);
    }

    private void insertFriendToDB(long userId, long friendId){
        validateUserId(userId, friendId);
        long count = friendsRepository.countByUserIdAndFriendId(userId, friendId);
        if(count <= 0){
            NurseFriendsEntity entity = new NurseFriendsEntity();
            entity.setUserId(userId);
            entity.setFriendId(friendId);
            friendsRepository.save(entity);
        }
    }

    private void validateUserId(long userId, long friendId) {
        if(!nurseRepository.exists(userId) || !nurseRepository.exists(friendId)){
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
    }

    @Transactional
    public void removeFriend(long userId, long friendId){
        validateUserId(userId, friendId);
        List<NurseFriendsEntity> entities = friendsRepository.findByUserIdAndFriendId(userId, friendId);
        if(!entities.isEmpty()){
            friendsRepository.delete(entities.get(0).getId());
        }
    }

    public List<NurseFriendsBean> getFriendList(long userId){
        List<NurseFriendsEntity> entities = friendsRepository.findByUserId(userId);
        return convertToNurseFriendsBeans(entities);
    }

    private List<NurseFriendsBean> convertToNurseFriendsBeans(List<NurseFriendsEntity> entities) {
        List<NurseFriendsBean> beans = new ArrayList<NurseFriendsBean>();
        for(NurseFriendsEntity entity:entities){
            beans.add(beanConverter.convert(entity));
        }
        return beans;
    }

    public List<NurseFriendsBean> searchFriends(long userId, String name){
        List<NurseFriendsEntity> friendMaps = friendsRepository.findByUserId(userId);
        logger.info("search nurse friends is : " +friendMaps);
        List<Long> friendIds = new ArrayList<Long>();
        for (NurseFriendsEntity friend : friendMaps) {
            friendIds.add(friend.getFriendId());
        }
        List<NurseEntity> friends = nurseRepository.findNurseByIdIn(friendIds);
        logger.info("search nurse friends is : " +friends);
        for (NurseEntity friend : friends) {
            if (friend.getName().contains(name)) {
            }
            else {
                friendIds.remove(friend.getId());
            }
        }
        List<NurseFriendsEntity> friendsFiltered = new ArrayList<NurseFriendsEntity>();
        for (NurseFriendsEntity friend : friendMaps) {
            if (friendIds.contains(friend.getFriendId())) {
                friendsFiltered.add(friend);
            }
        }

        return convertToNurseFriendsBeans(friendsFiltered);
    }

    public List<NurseFriendsBean> getFriends(long userId, long searchId, int pageIdx, int number){
        logger.info("get friends for the user " + searchId);
        boolean                  searchSelf         = userId==searchId;
        PageRequest              pageSort           = new PageRequest(pageIdx, number, Sort.Direction.DESC, "dateTime");
        List<Long>               friendIds          = new ArrayList<Long>();
        List<Long>               friendNotExistIds  = new ArrayList<Long>();
        List<NurseFriendsBean>   userFriends        = new ArrayList<NurseFriendsBean>();
        List<NurseFriendsBean>   searchFriends      = new ArrayList<NurseFriendsBean>();
        Page<NurseFriendsEntity> searchFriendsE     = null;

        // search self friends
        if (!searchSelf) {
            List<NurseFriendsEntity> userFriendsE = friendsRepository.findByUserId(userId);
            for(NurseFriendsEntity userFriend: userFriendsE){
                userFriends.add(beanConverter.convert(userFriend));
            }
        }

        // search searchId's friends
        searchFriendsE = friendsRepository.findNurseFriendByUserId(searchId, pageSort);
        for(NurseFriendsEntity searchFriendE: searchFriendsE){
            NurseFriendsBean searchFriend = beanConverter.convert(searchFriendE);
            searchFriends.add(searchFriend);
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
}
