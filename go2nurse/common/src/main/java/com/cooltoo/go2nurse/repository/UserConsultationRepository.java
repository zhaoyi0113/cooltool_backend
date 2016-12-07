package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ConsultationReason;
import com.cooltoo.go2nurse.entities.UserConsultationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by hp on 2016/8/28.
 */
public interface UserConsultationRepository extends JpaRepository<UserConsultationEntity, Long>{


    @Query("SELECT count(uc.id) FROM UserConsultationEntity uc" +
            " WHERE (?1 IS NULL OR ?1=uc.userId)" +
            " AND   (?2 IS NULL OR ?2=uc.patientId)" +
            " AND   (?3 IS NULL OR ?3=uc.nurseId)" +
            " AND   (?4 IS NULL OR ?4=uc.categoryId)" +
            " AND  ((?5 IS NULL) OR (uc.diseaseDescription LIKE %?5%) OR (uc.clinicalHistory LIKE %?5%))")
    long countByConditions(Long userId, Long patientId, Long nurseId, Long categoryId, String contentLike);


    @Query("FROM UserConsultationEntity uc" +
            " WHERE (?1 IS NULL OR ?1=uc.userId)" +
            " AND   (?2 IS NULL OR ?2=uc.patientId)" +
            " AND   (?3 IS NULL OR ?3=uc.nurseId)" +
            " AND   (?4 IS NULL OR ?4=uc.categoryId)" +
            " AND  ((?5 IS NULL)OR (uc.diseaseDescription LIKE %?5%) OR (uc.clinicalHistory LIKE %?5%))")
    Page<UserConsultationEntity> findByConditions(Long userId, Long patientId, Long nurseId, Long categoryId, String contentLike, Pageable page);


    @Query("FROM UserConsultationEntity uc" +
            " WHERE (?1 IS NULL OR ?1=uc.userId)" +
            " AND   (?2 IS NULL OR ?2=uc.patientId)" +
            " AND   (?3 IS NULL OR ?3=uc.nurseId)" +
            " AND   (?4 IS NULL OR ?4=uc.categoryId)" +
            " AND   (?5 IS NULL OR ?5<>uc.status)" +
            " AND  ((?6 IS NULL) OR (uc.diseaseDescription LIKE %?6%) OR (uc.clinicalHistory LIKE %?6%))" +
            " AND   (?7 IS NULL OR ?7=uc.reason)")
    Page<UserConsultationEntity> findByUserNurseStatusNotAndContentLike(Long userId, Long patientId, Long nurseId, Long categoryId, CommonStatus status, String contentLike, ConsultationReason reason, Pageable page);


    @Query("FROM UserConsultationEntity uc" +
            " WHERE (?1 IS NULL OR ?1=uc.userId)" +
            " AND   (?2 IS NULL OR ?2=uc.patientId)" +
            " AND   (?3 IS NULL OR ?3<>uc.status)" +
            " AND   (?4 IS NULL OR ?4=uc.nurseId)" +
            " AND   (?5 IS NULL OR ?5=uc.reason)")
    Page<UserConsultationEntity> findByUserIdAndStatusNotAndNurseId(Long userId, Long patientId, CommonStatus status, Long nurseId, ConsultationReason reason, Pageable page);
}
