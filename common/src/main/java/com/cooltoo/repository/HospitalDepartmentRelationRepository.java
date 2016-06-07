package com.cooltoo.repository;

import com.cooltoo.entities.HospitalDepartmentRelationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalDepartmentRelationRepository extends CrudRepository<HospitalDepartmentRelationEntity, Integer>{

    List<HospitalDepartmentRelationEntity> findRelationByHospitalId(int hospitalId);
    List<HospitalDepartmentRelationEntity> findRelationByHospitalIdIn(List<Integer> hospitalIds);
    List<HospitalDepartmentRelationEntity> findRelationByDepartmentId(int departmentId);
    List<HospitalDepartmentRelationEntity> findRelationByDepartmentIdIn(List<Integer> departmentIds);
}
