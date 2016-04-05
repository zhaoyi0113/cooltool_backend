package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseQualificationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/3/23.
 */
public interface NurseQualificationRepository extends JpaRepository<NurseQualificationEntity, Long> {

    List<NurseQualificationEntity> findNurseQualificationByUserId(long userId, Sort sort);

    List<NurseQualificationEntity> findNurseQualificationByName(String name, Sort sort);

    List<NurseQualificationEntity> findNurseQualificationByUserIdAndName(long userId, String name, Sort sort);

    List<NurseQualificationEntity> findByIdIn(List<Long> ids);

    void deleteByIdIn(List<Long> ids);
}
