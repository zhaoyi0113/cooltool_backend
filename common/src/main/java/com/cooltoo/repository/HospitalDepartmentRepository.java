package com.cooltoo.repository;

import com.cooltoo.entities.HospitalDepartmentEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface HospitalDepartmentRepository extends CrudRepository<HospitalDepartmentEntity, Integer> {
    List<HospitalDepartmentEntity> findByHospitalIdAndParentId(Integer hospitalId, Integer parentId, Sort sort);
    List<HospitalDepartmentEntity> findByHospitalId(Integer hospitalId, Sort sort);
    long countByHospitalId(Integer hospitalId);
    List<HospitalDepartmentEntity> findByIdIn(List<Integer> ids, Sort sort);
    List<HospitalDepartmentEntity> findByName(String name);
    List<HospitalDepartmentEntity> findAll(Sort sort);
    List<HospitalDepartmentEntity> findByUniqueId(String uniqueId);
    long countByUniqueId(String uniqueId);

    @Query("FROM HospitalDepartmentEntity hd" +
            " WHERE (?1 IS NOT NULL AND hd.name LIKE %?1)" +
            " AND   (hd.enable=1)")
    List<HospitalDepartmentEntity> findByNameLike(String nameLike);

    HospitalDepartmentEntity findById(int id);
}
