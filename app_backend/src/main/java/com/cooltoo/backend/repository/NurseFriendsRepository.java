package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseFriendsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public interface NurseFriendsRepository extends JpaRepository<NurseFriendsEntity, Long> {

    List<NurseFriendsEntity> findByUserIdAndFriendId(long userId, long friendId);

    List<NurseFriendsEntity> findByUserIdOrFriendId(long userId, long friendId, Sort sort);

    List<NurseFriendsEntity> findByUserId(long userId);

    List<NurseFriendsEntity> findByFriendId(long friendId);

    Page<NurseFriendsEntity> findByUserId(long userId, Pageable pageable);

    long countByUserIdAndFriendId(long userId, long friendId);

    void deleteByUserIdIn(List<Long> userIds);

    void deleteByFriendIdIn(List<Long> friendIds);

}
