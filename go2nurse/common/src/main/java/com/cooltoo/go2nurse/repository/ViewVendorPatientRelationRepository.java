package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.entities.ViewVendorPatientRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by zhaolisong on 2016/11/24.
 */
public interface ViewVendorPatientRelationRepository extends JpaRepository<ViewVendorPatientRelationEntity, String> {

    @Query("SELECT DISTINCT vvpr.userId, vvpr.patientId FROM ViewVendorPatientRelationEntity vvpr" +
            " WHERE (?1 IS NULL OR ?1=vvpr.vendorType)" +
            " AND   (?2 IS NULL OR ?2=vvpr.vendorId)" +
            " AND   (?3 IS NULL OR ?3=vvpr.vendorDepartId)")
    List<Object[]> findByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId);
    @Query("SELECT DISTINCT vvpr.userId, vvpr.patientId  FROM ViewVendorPatientRelationEntity vvpr" +
            " WHERE (?1 IS NULL OR ?1=vvpr.vendorType)" +
            " AND   (?2 IS NULL OR ?2=vvpr.vendorId)" +
            " AND   (?3 IS NULL OR ?3=vvpr.vendorDepartId)")
    List<Object[]> findByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, Sort sort);
    @Query("SELECT DISTINCT vvpr.userId, vvpr.patientId  FROM ViewVendorPatientRelationEntity vvpr" +
            " WHERE (?1 IS NULL OR ?1=vvpr.vendorType)" +
            " AND   (?2 IS NULL OR ?2=vvpr.vendorId)" +
            " AND   (?3 IS NULL OR ?3=vvpr.vendorDepartId)")
    Page<Object[]> findByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, Pageable page);



    @Query("SELECT vvpr.userId, vvpr.patientId, vvpr.recordFrom, vvpr.recordId FROM ViewVendorPatientRelationEntity vvpr" +
            " WHERE (?1 IS NULL OR ?1=vvpr.vendorType)" +
            " AND   (?2 IS NULL OR ?2=vvpr.vendorId)" +
            " AND   (?3 IS NULL OR ?3=vvpr.vendorDepartId)" +
            " AND   (?4 IS NULL OR vvpr.recordFrom LIKE ?4)")
    List<Object[]> findRecordByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, String recordFrom);
}
