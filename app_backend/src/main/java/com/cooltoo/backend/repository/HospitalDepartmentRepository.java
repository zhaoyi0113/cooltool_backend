package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.HospitalDepartmentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalDepartmentRepository extends CrudRepository<HospitalDepartmentEntity, Integer> {
    List<HospitalDepartmentEntity> findDepartmentByIdIn(List<Integer> ids);
}
