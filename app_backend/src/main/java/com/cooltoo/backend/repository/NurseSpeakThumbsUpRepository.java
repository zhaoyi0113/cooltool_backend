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
    @Query("SELECT a.thumbsUpUserId FROM NurseSpeakThumbsUpEntity a WHERE a.nurseSpeakId=?1")
    List<Long> findThumbsupUserId(long nurseSpeakId);
    List<NurseSpeakThumbsUpEntity> findUpByNurseSpeakId(long nurseSpeakId);
    List<NurseSpeakThumbsUpEntity> findByNurseSpeakIdIn(List<Long> nurseSpeakIds);
    List<NurseSpeakThumbsUpEntity> findByNurseSpeakIdIn(List<Long> nurseSpeakIds, Sort sort);
    List<NurseSpeakThumbsUpEntity> findByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId);
    long countByNurseSpeakId(long nurseSpeakId);
}
