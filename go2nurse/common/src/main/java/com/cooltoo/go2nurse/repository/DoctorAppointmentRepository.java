package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.entities.DoctorAppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

/**
 * Created by hp on 2016/8/14.
 */
public interface DoctorAppointmentRepository extends JpaRepository<DoctorAppointmentEntity, Long> {

    List<DoctorAppointmentEntity> findByClinicHoursId(long clinicHoursId, Sort sort);

    //============================================
    //          for user
    //============================================
    @Query("FROM DoctorAppointmentEntity da" +
            " WHERE (?1 IS NULL OR da.userId=?1)" +
            " AND (da.orderStatus IN (?2))")
    List<DoctorAppointmentEntity> findByConditionsForUser(Long userId, List<OrderStatus> orderStatuses);

    @Query("FROM DoctorAppointmentEntity da" +
            " WHERE (?1 IS NULL OR da.userId=?1)" +
            " AND (da.orderStatus IN (?2))")
    Page<DoctorAppointmentEntity> findByConditionsForUser(Long userId, List<OrderStatus> orderStatuses, Pageable page);


    //============================================
    //          for administrator
    //============================================

    @Query("SELECT count(da.id) FROM DoctorAppointmentEntity da" +
            " WHERE (?1 IS NULL OR da.hospitalId=?1)" +
            " AND (?2 IS NULL OR da.departmentId=?2)" +
            " AND (?3 IS NULL OR da.doctorId=?3)" +
            " AND (?4 IS NULL OR da.clinicDateId=?4)" +
            " AND (?5 IS NULL OR da.clinicHoursId=?5)" +
            " AND (?6 IS NULL OR da.clinicDate>?6)" +
            " AND (?7 IS NULL OR da.clinicDate<?7)")
    long countByConditionsForAdmin(Integer hospitalId, Integer departmentId, Long doctorId,
                                   Long clinicDateId, Long clinicHoursId,
                                   Date startDate, Date endDate);

    @Query("FROM DoctorAppointmentEntity da" +
            " WHERE (?1 IS NULL OR da.hospitalId=?1)" +
            " AND (?2 IS NULL OR da.departmentId=?2)" +
            " AND (?3 IS NULL OR da.doctorId=?3)" +
            " AND (?4 IS NULL OR da.clinicDateId=?4)" +
            " AND (?5 IS NULL OR da.clinicHoursId=?5)" +
            " AND (?6 IS NULL OR da.clinicDate>?6)" +
            " AND (?7 IS NULL OR da.clinicDate<?7)")
    Page<DoctorAppointmentEntity> findByConditionsForAdmin(Integer hospitalId, Integer departmentId, Long doctorId,
                                                           Long clinicDateId, Long clinicHoursId,
                                                           Date startDate, Date endDate,
                                                           Pageable page);
}
