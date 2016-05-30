package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakComplaintEntity;
import com.cooltoo.constants.SuggestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/5/30.
 */
public interface NurseSpeakComplaintRepository extends JpaRepository<NurseSpeakComplaintEntity, Long> {
    long countBySpeakId(long speakId);
    long countByStatus(SuggestionStatus status);
    Page<NurseSpeakComplaintEntity> findBySpeakId(long speakId, Pageable page);
    Page<NurseSpeakComplaintEntity> findByStatus(SuggestionStatus status, Pageable page);
    NurseSpeakComplaintEntity findByInformantIdAndSpeakId(long informantId, long speakId);
}
