package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.ImageInCaseEntity;
import com.cooltoo.go2nurse.entities.ImageInUserConsultationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/23.
 */
public interface ImageInCaseRepository extends JpaRepository<ImageInCaseEntity, Long>{

    @Query("SELECT count(iiuc.id) FROM ImageInCaseEntity iiuc" +
            " WHERE (?1 IS NULL OR iiuc.casebookId=?1)" +
            " AND (?2 IS NULL OR iiuc.caseId=?2)")
    long countByCasebookIdAndCaseId(Long casebookId, Long caseId);

    long countByCasebookIdAndCaseIdAndImageId(long casebookId, long caseId, long imageId);

    List<ImageInCaseEntity> findByCasebookId(Long casebookId, Sort sort);
    List<ImageInCaseEntity> findByCaseIdIn(List<Long> talkIds);
    List<ImageInCaseEntity> findByCasebookId(Long consultationId);
    List<ImageInCaseEntity> findByImageIdIn(List<Long> imageIds);
}
