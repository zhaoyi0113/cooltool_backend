package com.cooltoo.repository;

import com.cooltoo.entities.NurseExtensionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/8/11.
 */
public interface NurseExtensionRepository extends JpaRepository<NurseExtensionEntity, Long> {

    List<NurseExtensionEntity> findByNurseIdIn(List<Long> nurseIds);

    List<NurseExtensionEntity> findByNurseId(Long nurseId);
}
