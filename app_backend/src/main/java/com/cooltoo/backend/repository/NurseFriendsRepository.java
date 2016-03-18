package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseFriendsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public interface NurseFriendsRepository extends JpaRepository<NurseFriendsEntity, Long> {

    List<NurseFriendsEntity> findByUserIdAndFriendId(long userId, long friendId);

    List<NurseFriendsEntity> findByUserId(long userId);

    @Query("select friend from NurseFriendsEntity as friend, NurseEntity as nurse where friend.userId = :userId and nurse.id = :userId and nurse.name LIKE %:filter%")
    List<NurseFriendsEntity> searchFriends(@Param("userId") long userId, @Param("filter") String filter);

    Page<NurseFriendsEntity> findNurseFriendByUserId(long userId, Pageable pageable);

    long countByUserIdAndFriendId(long userId, long friendId);

}
