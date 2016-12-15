package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.entities.NurseVisitPatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/11/6.
 */
public interface NurseVisitPatientRepository extends JpaRepository<NurseVisitPatientEntity, Long>{

    @Query("SELECT count(nvp.id) FROM NurseVisitPatientEntity nvp" +
            " WHERE (?1 IS NULL OR ?1=nvp.userId)" +
            "   AND (?2 IS NULL OR ?2=nvp.patientId)" +
            "   AND (?3 IS NULL OR ?3=nvp.nurseId)" +
            "   AND ((?4 IS NULL) OR (nvp.visitRecord LIKE %?4))" +
            "   AND (?5 IS NULL OR ?5=nvp.vendorType)" +
            "   AND (?6 IS NULL OR ?6=nvp.vendorId)" +
            "   AND (?7 IS NULL OR ?7=nvp.vendorDepartId)" +
            "   AND (?8 IS NULL OR ?8<>nvp.status)")
    long countByConditions(Long userId, Long patientId, Long nurseId, String contentLike, ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, CommonStatus statusNot);


    @Query("FROM NurseVisitPatientEntity nvp" +
            " WHERE (?1 IS NULL OR ?1=nvp.userId)" +
            "   AND (?2 IS NULL OR ?2=nvp.patientId)" +
            "   AND (?3 IS NULL OR ?3=nvp.nurseId)" +
            "   AND ((?4 IS NULL) OR (nvp.visitRecord LIKE %?4))" +
            "   AND (?5 IS NULL OR ?5=nvp.vendorType)" +
            "   AND (?6 IS NULL OR ?6=nvp.vendorId)" +
            "   AND (?7 IS NULL OR ?7=nvp.vendorDepartId)" +
            "   AND (?8 IS NULL OR ?8<>nvp.status)")
    Page<NurseVisitPatientEntity> findByConditions(Long userId, Long patientId, Long nurseId, String contentLike, ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, CommonStatus statusNot, Pageable page);


    @Query("FROM NurseVisitPatientEntity nvp" +
            " WHERE (?1 IS NULL OR nvp.userId=?1)" +
            " AND   (?2 IS NULL OR nvp.patientId=?2)" +
            " AND   (?3 IS NULL OR nvp.nurseId=?3)" +
            " AND   (?4 IS NULL OR nvp.status<>?4)" +
            " AND  ((?5 IS NULL) OR (nvp.visitRecord LIKE %?5))")
    Page<NurseVisitPatientEntity> findByUserNurseStatusNotAndContentLike(Long userId, Long patientId, Long nurseId, CommonStatus status, String contentLike, Pageable page);

    List<NurseVisitPatientEntity> findByOrderIdIn(List<Long> orderId, Sort sort);
}
