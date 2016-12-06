package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.CasebookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by hp on 2016/8/28.
 */
public interface CasebookRepository extends JpaRepository<CasebookEntity, Long>{


    @Query("SELECT count(uc.id) FROM CasebookEntity uc" +
            " WHERE (?1 IS NULL OR uc.userId=?1)" +
            " AND (?2 IS NULL OR uc.patientId=?2)" +
            " AND (?3 IS NULL OR uc.nurseId=?3)" +
            " AND ((?4 IS NULL) OR (uc.description LIKE %?4) OR (uc.name LIKE %?4))" +
            " AND (?5 IS NULL OR ?5=uc.hospitalId)" +
            " AND (?6 IS NULL OR ?6=uc.departmentId)")
    long countByConditions(Long userId, Long patientId, Long nurseId, String contentLike, Integer hospitalId, Integer departmentId);


    @Query("FROM CasebookEntity uc" +
            " WHERE (?1 IS NULL OR uc.userId=?1)" +
            " AND (?2 IS NULL OR uc.patientId=?2)" +
            " AND (?3 IS NULL OR uc.nurseId=?3)" +
            " AND ((?4 IS NULL) OR (uc.description LIKE %?4) OR (uc.name LIKE %?4))" +
            " AND (?5 IS NULL OR ?5=uc.hospitalId)" +
            " AND (?6 IS NULL OR ?6=uc.departmentId)")
    Page<CasebookEntity> findByConditions(Long userId, Long patientId, Long nurseId, String contentLike, Integer hospitalId, Integer departmentId, Pageable page);


    @Query("FROM CasebookEntity uc" +
            " WHERE (?1 IS NULL OR ?1=uc.userId)" +
            " AND   (?3 IS NULL OR ?2=uc.patientId)" +
            " AND   (?3 IS NULL OR ?3=uc.nurseId)" +
            " AND   (?4 IS NULL OR ?4<>uc.status)" +
            " AND  ((?5 IS NULL) OR (uc.description LIKE %?5) OR (uc.name LIKE %?5))")
    Page<CasebookEntity> findByUserNurseStatusNotAndContentLike(Long userId, Long patientId, Long nurseId, CommonStatus status, String contentLike, Pageable page);


    @Query("FROM CasebookEntity uc" +
            " WHERE (?1 IS NULL OR ?1=uc.userId)" +
            " AND   (?2 IS NULL OR ?2=uc.patientId)" +
            " AND   (?3 IS NULL OR ?3=uc.status)" +
            " AND   (?4 IS NULL OR ?4=uc.nurseId)")
    Page<CasebookEntity> findByUserIdAndStatusNotAndNurseId(Long userId, Long patientId, CommonStatus status, Long nurseId, Pageable page);
}
