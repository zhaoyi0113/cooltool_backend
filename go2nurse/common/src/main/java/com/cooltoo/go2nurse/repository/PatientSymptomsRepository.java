package com.cooltoo.go2nurse.repository;


import com.cooltoo.go2nurse.entities.PatientSymptomsEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 01/03/2017.
 */
public interface PatientSymptomsRepository extends JpaRepository<PatientSymptomsEntity, Long> {

    List<PatientSymptomsEntity> findByOrderIdIn(List<Long> orderIds);
    List<PatientSymptomsEntity> findByUserIdAndPatientId(long userId, long patientId, Sort sort);
}
