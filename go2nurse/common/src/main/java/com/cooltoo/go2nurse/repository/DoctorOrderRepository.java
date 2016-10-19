package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.DoctorOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/14.
 */
public interface DoctorOrderRepository extends JpaRepository<DoctorOrderEntity, Long> {

    @Query("SELECT doctorOrder.doctorId FROM DoctorOrderEntity doctorOrder" +
            " WHERE (?1 IS NULL OR doctorOrder.hospitalId=?1)")
    List<Long> findDoctorIdByHospitalId(Integer hospitalId, Sort sort);
    @Query("SELECT doctorOrder.doctorId FROM DoctorOrderEntity doctorOrder" +
            " WHERE (?1 IS NULL OR doctorOrder.hospitalId=?1)" +
            " AND (?2 IS NULL OR doctorOrder.departmentId=?2)")
    List<Long> findDoctorIdByHospitalIdAndDepartmentId(Integer hospitalId, Integer departmentId, Sort sort);

    long countOrderByHospitalId(Integer hospitalId);
    List<DoctorOrderEntity> findOrderByHospitalId(Integer hospitalId, Sort sort);
    Page<DoctorOrderEntity> findOrderByHospitalId(Integer hospitalId, Pageable sort);

    long countOrderByHospitalIdAndDepartmentId(Integer hospitalId, Integer departmentId);
    List<DoctorOrderEntity> findOrderByHospitalIdAndDepartmentId(Integer hospitalId, Integer departmentId, Sort sort);
    Page<DoctorOrderEntity> findOrderByHospitalIdAndDepartmentId(Integer hospitalId, Integer departmentId, Pageable sort);

    List<DoctorOrderEntity> findOrderByHospitalIdAndDoctorId(Integer hospitalId, Long doctorId, Sort sort);
    List<DoctorOrderEntity> findOrderByHospitalIdAndDepartmentIdAndDoctorId(Integer hospitalId, Integer departmentId, Long doctorId, Sort sort);
}
