package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SpeakType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by yzzhao on 3/15/16.
 */
public interface NurseSpeakRepository extends JpaRepository<NurseSpeakEntity, Long> {


    //=====================================
    //    Admin use
    //=====================================
    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak " +
            " WHERE (speak.speakType=?1 OR 0=?1) AND speak.content LIKE %?2 AND time>=?3 AND time<=?4")
    long countBySpeakTypeAndContentAndTime(int speakTypeId, String contentLike, Date startTime, Date endTime);

    @Query("FROM NurseSpeakEntity speak " +
            " WHERE (speak.speakType=?1 OR 0=?1) AND speak.content LIKE %?2 AND time>=?3 AND time<=?4")
    Page<NurseSpeakEntity> findBySpeakTypeAndContentAndTime(int speakTypeId, String contentLike, Date startTime, Date endTime, Pageable page);

    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak " +
            " WHERE speak.userId=?1 AND speak.content LIKE %?2 AND time>=?3 AND time<=?4")
    long countByUserIdContentAndTime(long userId, String contentLike, Date startTime, Date endTime);

    @Query("FROM NurseSpeakEntity speak " +
            " WHERE speak.userId=?1 AND speak.content LIKE %?2 AND time>=?3 AND time<=?4")
    Page<NurseSpeakEntity> findByUserIdContentAndTime(long userId, String contentLike, Date startTime, Date endTime, Pageable page);

    //=====================================
    //    User use
    //=====================================
    //List<NurseSpeakEntity> findByUserId(long userId);

    @Query("SELECT speak.userId, count(speak.id) FROM NurseSpeakEntity speak" +
            " WHERE speak.userId in (?1)" +
            " AND speak.status<>?2" +
            " GROUP BY speak.userId")
    List<Object[]> countByUserIdInAndStatusNot(List<Long> userIds, CommonStatus status);

    @Query("FROM NurseSpeakEntity speak" +
            " WHERE speak.userId=?1" +
            " AND speak.speakType IN (?2)" +
            " AND speak.status<>?3" +
            " AND speak.userId NOT IN (?4)")
    Page<NurseSpeakEntity> findSpecialTypeSpeakAndStatusNot(long userId, List<Integer> speakTypeIds, CommonStatus status, List<Long> denyUserIds, Pageable page);

    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak" +
            " WHERE speak.userId=?1" +
            " AND speak.speakType IN (?2)" +
            " AND speak.status<>?3" +
            " AND speak.userId NOT IN (?4)")
    long countSpecialTypeSpeakAndStatusNot(long userId, List<Integer> speakTypeIds, CommonStatus status, List<Long> denyUserIds);

    @Query("FROM NurseSpeakEntity speak" +
            " WHERE speak.speakType IN (?1)" +
            " AND speak.status<>?2" +
            " AND speak.userId NOT IN (?3)")
    Page<NurseSpeakEntity> findSpecialTypeSpeakAndStatusNot(List<Integer> speakTypeIds, CommonStatus status, List<Long> denyUserIds, Pageable page);

    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak" +
            " WHERE speak.speakType IN (?1)" +
            " AND speak.status<>?2" +
            " AND speak.userId NOT IN (?3)")
    long countSpecialTypeSpeakAndStatusNot(List<Integer> speakTypeIds, CommonStatus status, List<Long> denyUserIds);

    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak" +
            " WHERE speak.id IN (?1)" +
            " AND speak.speakType=?2" +
            " AND speak.status<>?3" +
            " AND speak.userId NOT IN (?4)")
    long countSortSpeakByTypeAndStatus(List<Long> speakIds, Integer speakTypeId, CommonStatus status, List<Long> denyUserIds);
}
