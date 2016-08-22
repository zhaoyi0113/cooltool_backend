package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created by hp on 2016/7/13.
 */
public interface ServiceOrderRepository extends JpaRepository<ServiceOrderEntity, Long> {

    List<ServiceOrderEntity> findByUserId(long userId, Sort sort);
    long countByOrderNo(String orderNo);

    @Query("SELECT count(order1.id) FROM ServiceOrderEntity order1" +
            " WHERE (?1 IS NULL OR order1.serviceItemId=?1)" +
            " AND (?2 IS NULL OR order1.userId=?2)" +
            " AND (?3 IS NULL OR order1.categoryId=?3)" +
            " AND (?4 IS NULL OR order1.topCategoryId=?4)" +
            " AND (?5 IS NULL OR order1.vendorType=?5)" +
            " AND (?6 IS NULL OR order1.vendorId=?6)" +
            " AND (?7 IS NULL OR order1.orderStatus=?7)")
    long countByConditions(Long itemId, Long userId, Long categoryId, Long topCategoryId, ServiceVendorType vendorType, Long vendorId, OrderStatus orderStatus);

    @Query("FROM ServiceOrderEntity order1" +
            " WHERE (?1 IS NULL OR order1.serviceItemId=?1)" +
            " AND (?2 IS NULL OR order1.userId=?2)" +
            " AND (?3 IS NULL OR order1.categoryId=?3)" +
            " AND (?4 IS NULL OR order1.topCategoryId=?4)" +
            " AND (?5 IS NULL OR order1.vendorType=?5)" +
            " AND (?6 IS NULL OR order1.vendorId=?6)" +
            " AND (?7 IS NULL OR order1.orderStatus=?7)")
    Page<ServiceOrderEntity> findByConditions(Long itemId, Long userId, Long categoryId, Long topCategoryId, ServiceVendorType vendorType, Long vendorId, OrderStatus orderStatus, Pageable page);

}
