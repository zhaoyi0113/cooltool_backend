package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.NursePatientFollowUpEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zhaolisong on 2016/11/7.
 */
public interface NursePatientFollowUpRepository extends JpaRepository<NursePatientFollowUpEntity, Long> {


    @Query("SELECT count(npfu.id) FROM NursePatientFollowUpEntity npfu" +
            " WHERE (?1 IS NULL OR npfu.hospitalId=?1)" +
            "   AND (?2 IS NULL OR npfu.departmentId=?2)" +
            "   AND (?3 IS NULL OR npfu.nurseId=?3)" +
            "   AND (?4 IS NULL OR npfu.userId=?4)" +
            "   AND (?5 IS NULL OR npfu.patientId=?5)")
    long countByConditions(Integer hospitalId, Integer departmentId, Long nurseId, Long userId, Long patientId);


    @Query("FROM NursePatientFollowUpEntity npfu" +
            " WHERE (?1 IS NULL OR npfu.hospitalId=?1)" +
            "   AND (?2 IS NULL OR npfu.departmentId=?2)" +
            "   AND (?3 IS NULL OR npfu.nurseId=?3)" +
            "   AND (?4 IS NULL OR npfu.userId=?4)" +
            "   AND (?5 IS NULL OR npfu.patientId=?5)")
    Page<NursePatientFollowUpEntity> findByConditions(Integer hospitalId, Integer departmentId, Long nurseId, Long userId, Long patientId, Pageable page);


    @Query("FROM NursePatientFollowUpEntity npfu" +
            " WHERE (?1 IS NULL OR npfu.hospitalId=?1)" +
            "   AND (?2 IS NULL OR npfu.departmentId=?2)" +
            "   AND (?3 IS NULL OR npfu.nurseId=?3)" +
            "   AND (?4 IS NULL OR npfu.userId=?4)" +
            "   AND (?5 IS NULL OR npfu.patientId=?5)" +
            "   AND (?6 IS NULL OR npfu.status<>?6)")
    Page<NursePatientFollowUpEntity> findByConditionsAndStatusNot(Integer hospitalId, Integer departmentId, Long nurseId, Long userId, Long patientId, CommonStatus status, Pageable page);
}
