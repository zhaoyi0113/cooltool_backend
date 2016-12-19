package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.DoctorClinicDateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

/**
 * Created by hp on 2016/8/4.
 */
public interface DoctorClinicDateRepository extends JpaRepository<DoctorClinicDateEntity, Long> {

    long countByDoctorIdAndClinicDate(long doctorId, Date clinicDate);
    List<DoctorClinicDateEntity> findByDoctorIdAndClinicDate(long doctorId, Date clinicDate, Sort sort);


    @Query("SELECT count(cDate.id) FROM DoctorClinicDateEntity cDate" +
            " WHERE cDate.doctorId=?1" +
            " AND cDate.status IN (?2)" +
            " AND (?3 IS NULL OR cDate.clinicDate>=?3)" +
            " AND (?4 IS NULL OR cDate.clinicDate<=?4)")
    long countDoctorByConditions(long doctorId, List<CommonStatus> status, Date start, Date end);
    @Query("FROM DoctorClinicDateEntity cDate" +
            " WHERE cDate.doctorId=?1" +
            " AND cDate.status IN (?2)" +
            " AND (?3 IS NULL OR cDate.clinicDate>=?3)" +
            " AND (?4 IS NULL OR cDate.clinicDate<=?4)")
    List<DoctorClinicDateEntity> findDoctorByConditions(long doctorId, List<CommonStatus> status, Date start, Date end, Sort sort);
    @Query("FROM DoctorClinicDateEntity cDate" +
            " WHERE cDate.doctorId=?1" +
            " AND cDate.status IN (?2)" +
            " AND (?3 IS NULL OR cDate.clinicDate>=?3)" +
            " AND (?4 IS NULL OR cDate.clinicDate<=?4)")
    Page<DoctorClinicDateEntity> findDoctorByConditions(long doctorId, List<CommonStatus> status, Date start, Date end, Pageable page);

    @Query("SELECT cDate.doctorId FROM DoctorClinicDateEntity cDate" +
            " WHERE cDate.doctorId IN (?1)" +
            "   AND ?2 =  cDate.status" +
            "   AND ?3 <= cDate.clinicDate")
    List<Object> findDoctorHasClinicDate(List<Long> doctorIds, CommonStatus status, Date time);
}
