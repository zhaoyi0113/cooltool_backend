package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakThumbsUpEntity;
import com.cooltoo.constants.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by hp on 2016/3/18.
 */
public interface NurseSpeakThumbsUpRepository extends JpaRepository<NurseSpeakThumbsUpEntity, Long> {
    @Query("SELECT count(thumbsUp.id) FROM NurseSpeakThumbsUpEntity thumbsUp WHERE thumbsUp.status=?2 AND thumbsUp.nurseSpeakId IN " +
            "(SELECT speak.id FROM NurseSpeakEntity speak WHERE speak.userId=?1)")
    long countOthersThumbsUpUser(long userId, CommonStatus status);
    @Query("SELECT count(thumbsUp.id) FROM NurseSpeakThumbsUpEntity thumbsUp WHERE thumbsUp.thumbsUpUserId=?1 AND thumbsUp.status=?2")
    long countUserThumbsUpOthers(long userId, CommonStatus status);
    @Query("SELECT a.thumbsUpUserId FROM NurseSpeakThumbsUpEntity a WHERE a.nurseSpeakId=?1 AND a.status=?2")
    List<Long> findThumbsupUserId(long nurseSpeakId, CommonStatus status);
    List<NurseSpeakThumbsUpEntity> findByStatusAndNurseSpeakId(CommonStatus status, long nurseSpeakId);
    List<NurseSpeakThumbsUpEntity> findByStatusAndNurseSpeakIdIn(CommonStatus status, List<Long> nurseSpeakIds);
    List<NurseSpeakThumbsUpEntity> findByStatusAndNurseSpeakIdIn(CommonStatus status, List<Long> nurseSpeakIds, Sort sort);
    List<NurseSpeakThumbsUpEntity> findByStatusAndNurseSpeakIdAndThumbsUpUserId(CommonStatus status, long nurseSpeakId, long thumbsUpUserId);
    long countByNurseSpeakId(long nurseSpeakId);

    @Query("SELECT thumbsUp.id FROM NurseSpeakThumbsUpEntity thumbsUp WHERE thumbsUp.nurseSpeakId=?1 AND thumbsUp.thumbsUpUserId=?2")
    List<Long> findThumbsUpIdBySpeakIdAndMakerId(long nurseSpeakId, long thumbsUpMakerId);
}
