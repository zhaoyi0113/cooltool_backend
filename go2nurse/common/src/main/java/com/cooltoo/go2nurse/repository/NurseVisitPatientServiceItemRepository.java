package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.NurseVisitPatientServiceItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/7/25.
 */
public interface NurseVisitPatientServiceItemRepository extends JpaRepository<NurseVisitPatientServiceItemEntity, Long> {

    @Query("FROM NurseVisitPatientServiceItemEntity nvpsi" +
            " WHERE (nvpsi.id IN (?1))" +
            " AND (nvpsi.status IN (?2))")
    List<NurseVisitPatientServiceItemEntity> findByIdInAndStatusIn(List<Long> itemIds, List<CommonStatus> status, Sort sort);
    @Query("SELECT count(nvpsi.id) FROM NurseVisitPatientServiceItemEntity nvpsi" +
            " WHERE (?1 IS NULL OR nvpsi.hospitalId=?1)" +
            " AND   (?2 IS NULL OR nvpsi.departmentId=?2)" +
            " AND   (nvpsi.status IN (?3))")
    public long countByConditions(Integer hospitalId, Integer departmentId, List<CommonStatus> status);
    @Query("FROM NurseVisitPatientServiceItemEntity nvpsi" +
            " WHERE (?1 IS NULL OR nvpsi.hospitalId=?1)" +
            " AND   (?2 IS NULL OR nvpsi.departmentId=?2)" +
            " AND   (nvpsi.status IN (?3))")
    List<NurseVisitPatientServiceItemEntity> findByConditions(Integer hospitalId, Integer departmentId, List<CommonStatus> status, Sort sort);
    @Query("FROM NurseVisitPatientServiceItemEntity nvpsi" +
            " WHERE (?1 IS NULL OR nvpsi.hospitalId=?1)" +
            " AND   (?2 IS NULL OR nvpsi.departmentId=?2)" +
            " AND   (nvpsi.status IN (?3))")
    Page<NurseVisitPatientServiceItemEntity> findByConditions(Integer hospitalId, Integer departmentId, List<CommonStatus> status, Pageable page);
}
