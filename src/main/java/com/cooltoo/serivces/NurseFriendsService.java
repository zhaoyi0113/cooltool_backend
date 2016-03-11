package com.cooltoo.serivces;

import com.cooltoo.beans.NurseFriendsBean;
import com.cooltoo.converter.NurseFriendBeanConverter;
import com.cooltoo.entities.NurseFriendsEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseFriendsRepository;
import com.cooltoo.repository.NurseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
@Service("NurseFriendsService")
public class NurseFriendsService {

    @Autowired
    private NurseFriendsRepository friendsRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private NurseFriendBeanConverter beanConverter;

    @Transactional
    public void addFriend(long userId, long friendId){
        validateUserId(userId, friendId);
        NurseFriendsEntity entity = new NurseFriendsEntity();
        entity.setUserId(userId);
        entity.setFriendId(friendId);
        friendsRepository.save(entity);
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

    public int getFriendsCount(long userId){
        return getFriendList(userId).size();
    }
}
