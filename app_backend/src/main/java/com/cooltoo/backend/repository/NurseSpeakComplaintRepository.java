package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakComplaintEntity;
import com.cooltoo.constants.ReadingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/5/30.
 */
public interface NurseSpeakComplaintRepository extends JpaRepository<NurseSpeakComplaintEntity, Long> {
    long countBySpeakId(long speakId);
    long countByStatus(ReadingStatus status);
    Page<NurseSpeakComplaintEntity> findBySpeakId(long speakId, Pageable page);
    Page<NurseSpeakComplaintEntity> findByStatus(ReadingStatus status, Pageable page);
    NurseSpeakComplaintEntity findByInformantIdAndSpeakId(long informantId, long speakId);
}
