package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.ImageInUserConsultationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/8/28.
 */
public interface ImageInUserConsultationRepository extends JpaRepository<ImageInUserConsultationEntity, Long>{

    @Query("SELECT count(iiuc.id) FROM ImageInUserConsultationEntity iiuc" +
            " WHERE (?1 IS NULL OR iiuc.consultationId=?1)" +
            " AND (?2 IS NULL OR iiuc.talkId=?2)")
    long countByConsultationIdAndTalkId(Long consultationId, Long talkId);

    List<ImageInUserConsultationEntity> findByConsultationId(Long consultationId, Sort sort);

    List<ImageInUserConsultationEntity> findByTalkIdIn(List<Long> talkIds);
    List<ImageInUserConsultationEntity> findByConsultationId(Long consultationId);
    List<ImageInUserConsultationEntity> findByConsultationIdInAndTalkId(List<Long> consultationId, Long talkId, Sort sort);
}
