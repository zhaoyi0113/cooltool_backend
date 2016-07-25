package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
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

    long countByVendorIdAndStatusIn(Long vendorId, List<CommonStatus> statuses);
    List<ServiceItemEntity> findByVendorIdAndStatusIn(Long vendorId, List<CommonStatus> statuses, Sort sort);
    Page<ServiceItemEntity> findByVendorIdAndStatusIn(Long vendorId, List<CommonStatus> statuses, Pageable sort);

    List<ServiceItemEntity> findByIdIn(List<Long> ids);

    @Modifying
    @Query("UPDATE ServiceItemEntity item SET item.categoryId=0" +
            " WHERE item.categoryId IN ?2 ")
    int setCategoryIdToNone(List<Long> categoryId);

    @Modifying
    @Query("UPDATE ServiceItemEntity item SET item.vendorId=0, item.vendorType=0" +
            " WHERE item.vendorId IN ?2 ")
    int setVendorIdToNone(List<Long> vendorId);




    @Query("SELECT count(item.id) FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR item.categoryId IN (?1))" +
            " AND (?2 IS NULL OR item.vendorId=?2)" +
            " AND (?3 IS NULL OR item.vendorType=?3)" +
            " AND (?4 IS NULL OR item.status IN (?4))")
   long countByCategoryVendorAndStatus(List<Long> categoryIds, Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses);

    @Query("FROM ServiceItemEntity item" +
            " WHERE (?1 IS NULL OR item.categoryId IN (?1))" +
            " AND (?2 IS NULL OR item.vendorId=?2)" +
            " AND (?3 IS NULL OR item.vendorType=?3)" +
            " AND (?4 IS NULL OR item.status IN (?4))")
    Page<ServiceItemEntity> findByCategoryVendorAndStatus(List<Long> categoryIds, Long vendorId, ServiceVendorType vendorType, List<CommonStatus> statuses, Pageable page);
}
