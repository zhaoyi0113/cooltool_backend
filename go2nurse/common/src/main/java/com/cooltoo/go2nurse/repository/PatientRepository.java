package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.GenderType;
import com.cooltoo.go2nurse.entities.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yzzhao on 2/29/16.
 */
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    @Query("SELECT count(patient.id) FROM PatientEntity patient" +
            " WHERE (?1 IS NULL OR patient.status=?1)" +
            " AND (?2 IS NULL OR patient.name LIKE %?2)" +
            " AND (?3 IS NULL OR patient.gender=?3)" +
            " AND (?4 IS NULL OR patient.identityCard LIKE %?4)" +
            " AND (?5 IS NULL OR patient.mobile LIKE %?5)")
    long countByConditions(CommonStatus status, String name, GenderType gender, String identity, String mobile);

    @Query("SELECT count(patient.id) FROM PatientEntity patient" +
            " WHERE (?1 IS NULL OR patient.status=?1)" +
            " AND (?2 IS NULL OR patient.name LIKE %?2)" +
            " AND (?3 IS NULL OR patient.gender=?3)" +
            " AND (?4 IS NULL OR patient.identityCard LIKE %?4)" +
            " AND (?5 IS NULL OR patient.mobile LIKE %?5)")
    Page<PatientEntity> findByConditions(CommonStatus status, String name, GenderType gender, String identity, String mobile, Pageable page);

    List<PatientEntity> findByStatusAndIdIn(CommonStatus status, List<Long> ids, Sort sort);
}
