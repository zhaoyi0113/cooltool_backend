package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.NursePatientRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/10/8.
 */
public interface NursePatientRelationRepository extends JpaRepository<NursePatientRelationEntity, Long> {

    List<NursePatientRelationEntity> findByNurseIdAndStatus(long nurseId, CommonStatus status, Sort sort);
    List<NursePatientRelationEntity> findByNurseIdAndUserIdAndPatientId(long nurseId, long userId, long patientId, Sort sort);

    List<NursePatientRelationEntity> findByNurseIdInAndStatus(List<Long> nursesId, CommonStatus status);
    Page<NursePatientRelationEntity> findByNurseIdInAndStatus(List<Long> nursesId, CommonStatus status, Pageable page);
}
