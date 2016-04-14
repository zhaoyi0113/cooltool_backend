package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.ImagesInSpeakEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/14.
 */
public interface ImagesInSpeakRepository extends CrudRepository<ImagesInSpeakEntity, Long> {
    List<ImagesInSpeakEntity> findBySpeakIdIn(List<Long> speakIds);
    List<ImagesInSpeakEntity> findBySpeakIdIn(List<Long> speakIds, Sort sort);
    long countBySpeakId(Long speakId);
}
