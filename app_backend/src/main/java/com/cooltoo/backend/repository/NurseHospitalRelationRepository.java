package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseHospitalRelationEntity;
import com.cooltoo.beans.NurseHospitalRelationBean;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface NurseHospitalRelationRepository extends CrudRepository<NurseHospitalRelationEntity, Long> {
    List<NurseHospitalRelationEntity> findByNurseId(Long userId);
    List<NurseHospitalRelationEntity> findByNurseIdIn(List<Long> userIds);
    List<NurseHospitalRelationEntity> findByHospitalIdIn(List<Integer> departmentIds);
    List<NurseHospitalRelationEntity> findByDepartmentIdIn(List<Integer> departmentIds);
    void deleteByDepartmentIdIn(List<Integer> departIds);
    void deleteByHospitalIdIn(List<Integer> hospitalIds);
}
