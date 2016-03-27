package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.HospitalDepartmentRelationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalDepartmentRelationRepository extends CrudRepository<HospitalDepartmentRelationEntity, Integer>{

    public List<HospitalDepartmentRelationEntity> findRelationByHospitalId(int hospitalId);

    public List<HospitalDepartmentRelationEntity> findRelationByDepartmentId(int departmentId);
}
