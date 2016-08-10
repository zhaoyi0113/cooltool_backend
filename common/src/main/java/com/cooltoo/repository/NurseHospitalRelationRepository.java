package com.cooltoo.repository;

import com.cooltoo.entities.NurseHospitalRelationEntity;
import com.cooltoo.entities.HospitalEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface NurseHospitalRelationRepository extends CrudRepository<NurseHospitalRelationEntity, Long> {
    List<NurseHospitalRelationEntity> findByNurseId(Long userId);
    List<NurseHospitalRelationEntity> findByNurseIdIn(List<Long> userIds);
    List<NurseHospitalRelationEntity> findByHospitalIdIn(List<Integer> departmentIds);
    List<NurseHospitalRelationEntity> findByDepartmentIdIn(List<Integer> departmentIds);
    @Query("select hospital from HospitalEntity hospital, NurseHospitalRelationEntity relation where relation.nurseId = :userId and relation.hospitalId = hospital.id")
    List<HospitalEntity> getNurseHospitals(@Param("userId") long userId);
    void deleteByDepartmentIdIn(List<Integer> departIds);
    void deleteByHospitalIdIn(List<Integer> hospitalIds);
}
