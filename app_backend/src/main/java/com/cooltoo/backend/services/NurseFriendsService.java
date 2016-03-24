package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.converter.NurseFriendBeanConverter;
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
        List<NurseFriendsEntity> entities = friendsRepository.searchFriends(userId, name);
        return convertToNurseFriendsBeans(entities);
    }

    public List<NurseFriendsBean> getFriends(long userId, int pageIdx, int number){
        logger.info("get friends for the user "+userId);
        PageRequest request = new PageRequest(pageIdx, number, Sort.Direction.DESC, "dateTime");
        Page<NurseFriendsEntity> entities = friendsRepository.findNurseFriendByUserId(userId, request);
        List<NurseFriendsBean> friends = new ArrayList<NurseFriendsBean>();
        for(NurseFriendsEntity entity: entities){
            friends.add(beanConverter.convert(entity));
        }
        return friends;
    }

    public int getFriendsCount(long userId){
        return getFriendList(userId).size();
    }
}
