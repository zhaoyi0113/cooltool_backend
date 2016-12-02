package com.cooltoo.repository;

import com.cooltoo.entities.NurseQualificationEntity;
import com.cooltoo.constants.VetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/3/23.
 */
public interface NurseQualificationRepository extends JpaRepository<NurseQualificationEntity, Long> {

    Page<NurseQualificationEntity> findByStatus(VetStatus status, Pageable page);
    List<NurseQualificationEntity> findNurseQualificationByUserId(long userId, Sort sort);
    List<NurseQualificationEntity> findByIdIn(List<Long> ids);
    List<NurseQualificationEntity> findByUserIdIn(List<Long> usersId);
    long countByStatus(VetStatus status);
}
