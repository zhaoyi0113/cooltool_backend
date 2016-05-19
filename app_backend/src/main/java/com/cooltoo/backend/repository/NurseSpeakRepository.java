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
    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak WHERE speak.content LIKE %?1 AND time>=?2 AND time<=?3")
    long countByContentAndTime(String contentLike, Date startTime, Date endTime);

    @Query("FROM NurseSpeakEntity speak WHERE speak.content LIKE %?1 AND time>=?2 AND time<=?3")
    Page<NurseSpeakEntity> findByContentAndTime(String contentLike, Date startTime, Date endTime, Pageable page);

    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak " +
            " WHERE speak.userId=?1 AND speak.content LIKE %?2 AND time>=?3 AND time<=?4")
    long countByUserIdContentAndTime(long userId, String contentLike, Date startTime, Date endTime);

    @Query("FROM NurseSpeakEntity speak " +
            " WHERE speak.userId=?1 AND speak.content LIKE %?2 AND time>=?3 AND time<=?4")
    Page<NurseSpeakEntity> findByUserIdContentAndTime(long userId, String contentLike, Date startTime, Date endTime, Pageable page);

    //=====================================
    //    User use
    //=====================================
    List<NurseSpeakEntity> findByUserId(long userId);

    @Query("SELECT speak.userId, count(speak.id) FROM NurseSpeakEntity speak WHERE speak.userId in (?1) GROUP BY speak.userId")
    List<Object[]> countByUserIdIn(List<Long> userIds);

    @Query("FROM NurseSpeakEntity speak WHERE speak.userId=?1 AND speak.speakType IN (?2)")
    Page<NurseSpeakEntity> findSpecialTypeSpeak(long userId, List<Integer> speakTypeIds, Pageable page);

    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak WHERE speak.userId=?1 AND speak.speakType IN (?2)")
    long countSpecialTypeSpeak(long userId, List<Integer> speakTypeIds);

    @Query("FROM NurseSpeakEntity speak WHERE speak.speakType IN (?1)")
    Page<NurseSpeakEntity> findSpecialTypeSpeak(List<Integer> speakTypeIds, Pageable page);

    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak WHERE speak.speakType IN (?1)")
    long countSpecialTypeSpeak(List<Integer> speakTypeIds);

    @Query("SELECT count(speak.id) FROM NurseSpeakEntity speak WHERE speak.id IN (?1) AND speak.speakType=?2")
    long countSortSpeakByType(List<Long> speakIds, Integer speakTypeId);
}
