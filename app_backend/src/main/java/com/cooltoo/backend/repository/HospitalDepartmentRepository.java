package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.HospitalDepartmentEntity;
import com.cooltoo.backend.services.HospitalService;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalDepartmentRepository extends CrudRepository<HospitalDepartmentEntity, Integer> {
    List<HospitalDepartmentEntity> findByIdIn(List<Integer> ids);
    List<HospitalDepartmentEntity> findByName(String name);
}
