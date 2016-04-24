package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseFriendsEntity;
import com.cooltoo.backend.entities.NurseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yzzhao on 3/10/16.
 */
public interface NurseFriendsRepository extends JpaRepository<NurseFriendsEntity, Long> {

    List<NurseFriendsEntity> findByUserIdAndFriendId(long userId, long friendId);

    long countByUserIdAndFriendId(long userId, long friendId);

    @Query("SELECT count(a) FROM NurseFriendsEntity a WHERE a.isAgreed<>0 AND a.userId=?1 AND a.friendId IN (\n" +
            "        SELECT b.userId FROM NurseFriendsEntity b WHERE b.friendId=?1 AND b.isAgreed<>0)")
    long countFriendship(long userId);

    @Query("FROM NurseFriendsEntity a WHERE a.isAgreed<>0 AND a.userId=?1 AND a.friendId IN (\n" +
            "        SELECT b.userId FROM NurseFriendsEntity b WHERE b.friendId=?1 AND b.isAgreed<>0)")
    List<NurseFriendsEntity> findFriendshipByPage(long userId, Pageable pageable);

    @Query("FROM NurseFriendsEntity a WHERE a.isAgreed<>0 AND a.userId=?1 AND a.friendId IN (\n" +
            "        SELECT b.userId FROM NurseFriendsEntity b WHERE b.friendId=?1 AND b.isAgreed<>0)")
    List<NurseFriendsEntity> findFriendshipAgreed(long userId);

    @Query("FROM NurseFriendsEntity a WHERE a.isAgreed=0  AND a.userId=?1 AND a.friendId IN (\n" +
            "        SELECT b.userId FROM NurseFriendsEntity b WHERE b.friendId=?1 AND b.isAgreed<>0)")
    List<NurseFriendsEntity> findFriendshipWaitAgree(long userId);

    @Query("SELECT a.friendId FROM NurseFriendsEntity a WHERE a.isAgreed<>0 AND a.userId=?1 AND a.friendId IN (\n" +
            "        SELECT b.userId FROM NurseFriendsEntity b WHERE b.friendId=?1 AND b.isAgreed<>0)")
    List<Long> findFriendshipAgreedIds(long userId, Pageable pageable);

    @Query("FROM NurseFriendsEntity a WHERE a.userId=?1 AND a.friendId IN (\n" +
            "        SELECT b.userId FROM NurseFriendsEntity b WHERE b.friendId=?1 AND b.userId IN (?2))")
    List<NurseFriendsEntity> findAgreedAndWaitingIds(long userId, List<Long> others);

    @Query("SELECT a.friendId FROM NurseFriendsEntity a WHERE a.isAgreed<>0 AND a.userId=?1 AND a.friendId IN (\n" +
            "        SELECT b.userId FROM NurseFriendsEntity b WHERE b.isAgreed=0 AND b.friendId=?1 AND b.userId IN (?2))")
    List<Long> findUserWaitingAgreeIds(long userId, List<Long> others);

    @Query("FROM NurseFriendsEntity a WHERE a.userId=:userId AND a.friendId IN (\n" +
            "     SELECT b.id FROM NurseEntity b WHERE b.name LIKE :name AND b.id IN (\n" +
            "          SELECT c.friendId FROM NurseFriendsEntity c WHERE c.isAgreed<>0 AND c.userId=:userId AND c.friendId IN (\n" +
            "               SELECT d.userId FROM NurseFriendsEntity d WHERE d.friendId=:userId AND d.isAgreed<>0)))")
    List<NurseFriendsEntity> findFriendsByName(@Param("userId") long userId, @Param("name") String nameLike);

    @Modifying
    @Transactional
    @Query("DELETE FROM NurseFriendsEntity a WHERE (a.userId=?1 AND a.friendId=?2) OR (a.userId=?2 AND a.friendId=?1)")
    void deleteFriendship(long userId, long friendId);
}
