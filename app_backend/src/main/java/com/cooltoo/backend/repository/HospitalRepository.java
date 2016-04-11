package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.HospitalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalRepository extends CrudRepository<HospitalEntity, Integer> {
    Page<HospitalEntity> findAll(Pageable page);
    List<HospitalEntity> findByName(String name);
    @Query("select hospital from HospitalEntity hospital, NurseHospitalRelationEntity relation where relation.nurseId = :userId and relation.hospitalId = hospital.id")
    List<HospitalEntity> getNurseHospitals(@Param("userId") long userId);
}
