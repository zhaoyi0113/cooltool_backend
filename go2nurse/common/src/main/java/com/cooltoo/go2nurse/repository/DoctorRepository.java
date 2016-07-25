package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.DoctorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/7/25.
 */
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {

    long countByStatusIn(List<CommonStatus> status);
    Page<DoctorEntity> findByStatusIn(List<CommonStatus> status, Pageable page);

    @Query("SELECT count(doctor.id) FROM DoctorEntity doctor" +
            " WHERE (?1 IS NULL OR doctor.hospitalId=?1)" +
            " AND (?2 IS NULL OR doctor.departmentId=?2)" +
            " AND (doctor.status IN (?3))")
    long countByHospitalDepartmentStatusIn(Integer hospitalId, Integer departmentId, List<CommonStatus> status);
    @Query("FROM DoctorEntity doctor" +
            " WHERE (?1 IS NULL OR doctor.hospitalId=?1)" +
            " AND (?2 IS NULL OR doctor.departmentId=?2)" +
            " AND (doctor.status IN (?3))")
    Page<DoctorEntity> findByHospitalDepartmentStatusIn(Integer hospitalId, Integer departmentId, List<CommonStatus> status, Pageable page);
}
