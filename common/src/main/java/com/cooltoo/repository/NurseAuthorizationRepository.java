package com.cooltoo.repository;

import com.cooltoo.entities.NurseAuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 19/12/2016.
 */
public interface NurseAuthorizationRepository extends JpaRepository<NurseAuthorizationEntity, Long> {
    List<NurseAuthorizationEntity> findByNurseId(Long nurseId);
    List<NurseAuthorizationEntity> findByNurseIdIn(List<Long> nurseIds);
}
