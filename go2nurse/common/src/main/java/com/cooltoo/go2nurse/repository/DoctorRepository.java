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
            " WHERE (doctor.id IN (?1))" +
            " AND (doctor.status IN (?2))")
    long countByIdInAndStatusIn(List<Long> doctorIds, List<CommonStatus> status);
    @Query("FROM DoctorEntity doctor" +
            " WHERE (doctor.id IN (?1))" +
            " AND (doctor.status IN (?2))")
    List<DoctorEntity> findEntityByIdInAndStatusIn(List<Long> doctorIds, List<CommonStatus> status);
    @Query("SELECT doctor.id FROM DoctorEntity doctor" +
            " WHERE (doctor.id IN (?1))" +
            " AND (doctor.status IN (?2))")
    List<Long> findIdByIdInAndStatusIn(List<Long> doctorIds, List<CommonStatus> status);

    List<DoctorEntity> findByHospitalIdAndStatusIn(Integer hospitalId, List<CommonStatus> status, Sort sort);
}
