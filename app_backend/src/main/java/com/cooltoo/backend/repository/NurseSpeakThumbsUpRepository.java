package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakThumbsUpEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by hp on 2016/3/18.
 */
public interface NurseSpeakThumbsUpRepository extends CrudRepository<NurseSpeakThumbsUpEntity, Long> {

    public List<NurseSpeakThumbsUpEntity> findNurseSpeakThumbsUpByNurseSpeakId(long nurseSpeakId);

    public NurseSpeakThumbsUpEntity findNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId);

    public void deleteNurseSpeakThumbsUpByNurseSpeakIdAndThumbsUpUserId(long nurseSpeakId, long thumbsUpUserId);

    public List<NurseSpeakThumbsUpEntity> findThumbsUpByNurseSpeakIdIn(List<Long> nurseSpeakIds, Sort sort);
}
