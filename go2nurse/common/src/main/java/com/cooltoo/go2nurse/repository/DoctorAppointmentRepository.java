package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.DoctorAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/8/14.
 */
public interface DoctorAppointmentRepository extends JpaRepository<DoctorAppointmentEntity, Long> {
}
