package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseHospitalRelationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public interface NurseHospitalRelationRepository extends CrudRepository<NurseHospitalRelationEntity, Long> {
    List<NurseHospitalRelationEntity> findByNurseId(Long userId);
}
