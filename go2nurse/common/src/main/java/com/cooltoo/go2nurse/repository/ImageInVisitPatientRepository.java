package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.ImageInVisitPatientEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/11/06.
 */
public interface ImageInVisitPatientRepository extends JpaRepository<ImageInVisitPatientEntity, Long>{

    @Query("SELECT count(nurseVisitPatientId.id) FROM ImageInVisitPatientEntity iiuc" +
            " WHERE (?1 IS NULL OR iiuc.nurseVisitPatientId=?1)")
    long countByNurseVisitPatientId(Long nurseVisitPatientId);

    List<ImageInVisitPatientEntity> findByNurseVisitPatientId(Long nurseVisitPatientId, Sort sort);
    List<ImageInVisitPatientEntity> findByNurseVisitPatientId(Long nurseVisitPatientId);
    List<ImageInVisitPatientEntity> findByNurseVisitPatientIdIn(List<Long> nurseVisitPatientIds, Sort sort);
}
