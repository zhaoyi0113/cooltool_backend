package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.constants.SpeakType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yzzhao on 3/15/16.
 */
public interface NurseSpeakRepository extends JpaRepository<NurseSpeakEntity, Long> {

    List<NurseSpeakEntity> findByIdIn(List<Long> speakIds);
    Page<NurseSpeakEntity> findByUserId(long userId, Pageable request);
    Page<NurseSpeakEntity> findByUserIdAndSpeakType(long userId, int speakTypeId, Pageable request);
    Page<NurseSpeakEntity> findBySpeakType(int speakTypeId, Pageable request);
    long         countByUserId(long userId);
    long         countByUserIdAndSpeakType(long userId, int speakTypeId);

    @Query(value = "SELECT speak.userId, count(speak.id) FROM NurseSpeakEntity speak WHERE speak.userId in (?1) GROUP BY speak.userId")
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
