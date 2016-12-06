package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.UserPatientRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/14.
 */
public interface UserPatientRelationRepository extends JpaRepository<UserPatientRelationEntity, Long> {

    @Query("SELECT DISTINCT relation.patientId FROM UserPatientRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findPatientIdByUserIdAndStatus(Long userId, CommonStatus status, Sort sort);

    @Query("SELECT DISTINCT relation.userId FROM UserPatientRelationEntity relation" +
            " WHERE (relation.patientId IN (?1))" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findUserIdByPatientIdAndStatus(List<Long> patientIds, CommonStatus status, Sort sort);

    List<UserPatientRelationEntity> findByPatientIdAndUserId(Long patientId, Long userId, Sort sort);

    List<UserPatientRelationEntity> findByPatientIdInAndStatus(List<Long> patientId, CommonStatus status, Sort sort);
    List<UserPatientRelationEntity> findByUserIdAndStatus(Long userId, CommonStatus status, Sort sort);
}
