package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakThumbsUpEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by hp on 2016/3/18.
 */
public interface NurseSpeakThumbsUpRepository extends CrudRepository<NurseSpeakThumbsUpEntity, Long> {
    @Query("SELECT count(thumbsUp.id) FROM NurseSpeakThumbsUpEntity thumbsUp WHERE thumbsUp.nurseSpeakId IN " +
            "(SELECT speak.id FROM NurseSpeakEntity speak WHERE speak.userId=?1)")
    long countOthersThumbsUpUser(long userId);
    @Query("SELECT count(thumbsUp.id) FROM NurseSpeakThumbsUpEntity thumbsUp WHERE thumbsUp.thumbsUpUserId=?1")
    long countUserThumbsUpOthers(long userId);
    @Query("SELECT a.thumbsUpUserId FROM NurseSpeakThumbsUpEntity a WHERE a.nurseSpeakId=?1")
    List<Long> findThumbsupUserId(long nurseSpeakId);
    List<NurseSpeakThumbsUpEntity> findUpByNurseSpeakId(long nurseSpeakId);
    List<NurseSpeakThumbsUpEntity> findByNurseSpeakIdIn(List<Long> nurseSpeakIds);
    List<NurseSpeakThumbsUpEntity> findByNurseSpeakIdIn(List<Long> nurseSpeakIds, Sort sort);
    List<NurseSpeakThumbsUpEntity> findByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId);
    long countByNurseSpeakId(long nurseSpeakId);
}
