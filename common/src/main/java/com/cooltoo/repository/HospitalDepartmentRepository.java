package com.cooltoo.repository;

import com.cooltoo.entities.HospitalDepartmentEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalDepartmentRepository extends CrudRepository<HospitalDepartmentEntity, Integer> {
    List<HospitalDepartmentEntity> findByIdIn(List<Integer> ids, Sort sort);
    List<HospitalDepartmentEntity> findByName(String name);
    List<HospitalDepartmentEntity> findAll(Sort sort);
    List<HospitalDepartmentEntity> findByUniqueId(String uniqueId);
    long countByUniqueId(String uniqueId);
}
