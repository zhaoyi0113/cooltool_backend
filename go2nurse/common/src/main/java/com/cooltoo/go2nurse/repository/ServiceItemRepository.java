package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.entities.ServiceItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/7/13.
 */
public interface ServiceItemRepository extends JpaRepository<ServiceItemEntity, Long> {

    List<ServiceItemEntity> findByCategoryId(Long categoryId);

    long countByCategoryIdAndStatusIn(Long categoryId, List<CommonStatus> statuses);
    List<ServiceItemEntity> findByCategoryIdAndStatusIn(Long categoryId, List<CommonStatus> statuses, Sort sort);
    Page<ServiceItemEntity> findByCategoryIdAndStatusIn(Long categoryId, List<CommonStatus> statuses, Pageable sort);

    long countByVendorIdAndVendorTypeAndStatusIn(Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses);
    List<ServiceItemEntity> findByVendorIdAndVendorTypeAndStatusIn(Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses, Sort sort);
    Page<ServiceItemEntity> findByVendorIdAndVendorTypeAndStatusIn(Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses, Pageable sort);

    List<ServiceItemEntity> findByIdIn(List<Long> ids, Sort sort);


    @Modifying
    @Query("UPDATE ServiceItemEntity item SET item.categoryId=0" +
            " WHERE item.categoryId IN ?1 ")
    int setCategoryIdToNone(List<Long> categoryId);

    @Modifying
    @Query("UPDATE ServiceItemEntity item SET item.vendorId=0, item.vendorType=0" +
            " WHERE item.vendorId IN ?1 ")
    int setVendorIdToNone(List<Long> vendorId);


    //========================================================================
    //                   get by conditions 1
    //========================================================================
    @Query("SELECT count(item.id) FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR ?1=item.vendorType)" +
            "   AND (?2 IS NULL OR ?2=item.vendorId)" +
            "   AND (?3 IS NULL OR ?3=item.vendorDepartId)" +
            "   AND (?4 IS NULL OR ?4=item.categoryId)" +
            "   AND (?5 IS NULL OR ?5=item.needVisitPatientRecord)" +
            "   AND (?6 IS NULL OR ?6=item.managerApproved)" +
            "   AND (item.status IN (?7))")
    long countByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, Long categoryId, YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved, List<CommonStatus> statuses);
    @Query("FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR ?1=item.vendorType)" +
            "   AND (?2 IS NULL OR ?2=item.vendorId)" +
            "   AND (?3 IS NULL OR ?3=item.vendorDepartId)" +
            "   AND (?4 IS NULL OR ?4=item.categoryId)" +
            "   AND (?5 IS NULL OR ?5=item.needVisitPatientRecord)" +
            "   AND (?6 IS NULL OR ?6=item.managerApproved)" +
            "   AND (item.status IN (?7))")
    List<ServiceItemEntity> findByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, Long categoryId, YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved, List<CommonStatus> statuses, Sort sort);
    @Query("FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR ?1=item.vendorType)" +
            "   AND (?2 IS NULL OR ?2=item.vendorId)" +
            "   AND (?3 IS NULL OR ?3=item.vendorDepartId)" +
            "   AND (?4 IS NULL OR ?4=item.categoryId)" +
            "   AND (?5 IS NULL OR ?5=item.needVisitPatientRecord)" +
            "   AND (?6 IS NULL OR ?6=item.managerApproved)" +
            "   AND (item.status IN (?7))")
    Page<ServiceItemEntity> findByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, Long categoryId, YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved, List<CommonStatus> statuses, Pageable page);

    //========================================================================
    //                   get by conditions 2
    //========================================================================
    @Query("SELECT count(item.id) FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR ?1=item.vendorType)" +
            "   AND (?2 IS NULL OR ?2=item.vendorId)" +
            "   AND (?3 IS NULL OR ?3=item.vendorDepartId)" +
            "   AND (item.categoryId IN (?4))" +
            "   AND (?5 IS NULL OR ?5=item.needVisitPatientRecord)" +
            "   AND (?6 IS NULL OR ?6=item.managerApproved)" +
            "   AND (item.status IN (?7))")
    long countByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, List<Long> categoryId, YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved, List<CommonStatus> statuses);
    @Query("FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR ?1=item.vendorType)" +
            "   AND (?2 IS NULL OR ?2=item.vendorId)" +
            "   AND (?3 IS NULL OR ?3=item.vendorDepartId)" +
            "   AND (item.categoryId IN (?4))" +
            "   AND (?5 IS NULL OR ?5=item.needVisitPatientRecord)" +
            "   AND (?6 IS NULL OR ?6=item.managerApproved)" +
            "   AND (item.status IN (?7))")
    List<ServiceItemEntity> findByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, List<Long> categoryId, YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved, List<CommonStatus> statuses, Sort sort);
    @Query("FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR ?1=item.vendorType)" +
            "   AND (?2 IS NULL OR ?2=item.vendorId)" +
            "   AND (?3 IS NULL OR ?3=item.vendorDepartId)" +
            "   AND (item.categoryId IN (?4))" +
            "   AND (?5 IS NULL OR ?5=item.needVisitPatientRecord)" +
            "   AND (?6 IS NULL OR ?6=item.managerApproved)" +
            "   AND (item.status IN (?7))")
    Page<ServiceItemEntity> findByConditions(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, List<Long> categoryId, YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved, List<CommonStatus> statuses, Pageable page);






    @Query("SELECT count(item.id) FROM ServiceItemEntity item" +
            " WHERE (item.categoryId IN (?1))" +
            " AND (?2 IS NULL OR item.vendorId=?2)" +
            " AND (?3 IS NULL OR item.vendorType=?3)" +
            " AND (item.status IN (?4))")
   long countByCategoryVendorAndStatus(List<Long> categoryIds, Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses);
    @Query("SELECT count(item.id) FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR item.vendorId=?1)" +
            " AND (?2 IS NULL OR item.vendorType=?2)" +
            " AND (item.status IN (?3))")
    long countByVendorAndStatus(Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses);

    @Query("FROM ServiceItemEntity item" +
            " WHERE (item.categoryId IN (?1))" +
            " AND (?2 IS NULL OR item.vendorId=?2)" +
            " AND (?3 IS NULL OR item.vendorType=?3)" +
            " AND (item.status IN (?4))")
    Page<ServiceItemEntity> findByCategoryVendorAndStatus(List<Long> categoryIds, Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses, Pageable page);
    @Query("FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR item.vendorId=?1)" +
            " AND (?2 IS NULL OR item.vendorType=?2)" +
            " AND (item.status IN (?3))")
    Page<ServiceItemEntity> findByVendorAndStatus(Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses, Pageable page);
}
