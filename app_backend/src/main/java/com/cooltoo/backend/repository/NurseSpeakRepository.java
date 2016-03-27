package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakEntity;
import com.cooltoo.constants.SpeakType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by yzzhao on 3/15/16.
 */
public interface NurseSpeakRepository extends JpaRepository<NurseSpeakEntity, Long> {

    Page<NurseSpeakEntity> findNurseSpeakByUserId(long userId, Pageable request);

    long countNurseSpeakByUserId(long userId);

    Page<NurseSpeakEntity> findNurseSpeakByUserIdAndSpeakType(long userId, SpeakType speakType, Pageable request);

    long countNurseSpeakByUserIdAndSpeakType(long userId, SpeakType speakType);
}
