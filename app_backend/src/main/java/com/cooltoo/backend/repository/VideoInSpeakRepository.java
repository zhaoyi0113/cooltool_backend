package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.VideoInSpeakEntity;
import com.cooltoo.constants.VideoPlatform;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/14.
 */
public interface VideoInSpeakRepository extends CrudRepository<VideoInSpeakEntity, Long> {
    List<VideoInSpeakEntity> findByPlatformAndVideoId(VideoPlatform platform, String videoId);
    List<VideoInSpeakEntity> findBySpeakIdIn(List<Long> speakIds);
    List<VideoInSpeakEntity> findBySpeakIdIn(List<Long> speakIds, Sort sort);
    List<VideoInSpeakEntity> findByVideoIdAndPlatform(String videoId, VideoPlatform platform);
    long countBySpeakId(Long speakId);
}
