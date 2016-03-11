package com.cooltoo.repository;

import com.cooltoo.entities.NurseFriendsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public interface NurseFriendsRepository extends CrudRepository<NurseFriendsEntity, Long>{

    List<NurseFriendsEntity> findByUserIdAndFriendId(long userId, long friendId);

    List<NurseFriendsEntity> findByUserId(long userId);

    @Query("select friend from NurseFriendsEntity as friend, NurseEntity as nurse where friend.userId = :userId and nurse.id = :userId and nurse.name LIKE %:filter%")
    List<NurseFriendsEntity> searchFriends(@Param("userId") long userId, @Param("filter") String filter);

}
