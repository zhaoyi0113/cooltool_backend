package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.UserConsultationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by hp on 2016/8/28.
 */
public interface UserConsultationRepository extends JpaRepository<UserConsultationEntity, Long>{
    @Query("FROM UserConsultationEntity uc" +
            " WHERE (?1 IS NULL OR uc.userId=?1)" +
            " AND (?2 IS NULL OR uc.status<>?2)" +
            " AND ((?3 IS NULL) OR (uc.diseaseDescription LIKE %?3) OR (uc.clinicalHistory LIKE %?3))")
    Page<UserConsultationEntity> findByUserIdAndStatusNotAndContentLike(long userId, CommonStatus status, String contentLike, Pageable page);
}
