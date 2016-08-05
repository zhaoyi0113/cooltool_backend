package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.DoctorClinicHoursEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.List;

/**
 * Created by hp on 2016/8/4.
 */
public interface DoctorClinicHoursRepository extends JpaRepository<DoctorClinicHoursEntity, Long> {

    List<DoctorClinicHoursEntity> findByClinicDateIdIn(List<Long> clinicDateIds, Sort sort);
    List<DoctorClinicHoursEntity> findByClinicDateId(long clinicDateId, Sort sort);
    int deleteByClinicDateId(long clinicDateId);
}
