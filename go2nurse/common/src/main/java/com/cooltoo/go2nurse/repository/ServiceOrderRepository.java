package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.ManagedBy;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/7/13.
 */
public interface ServiceOrderRepository extends JpaRepository<ServiceOrderEntity, Long> {

    List<ServiceOrderEntity> findByUserId(long userId, Sort sort);
    long countByOrderNo(String orderNo);
    List<ServiceOrderEntity> findByOrderNo(String orderNo);

    @Query("SELECT count(order1.id) FROM ServiceOrderEntity order1" +
            " WHERE (?1 IS NULL OR ?1=order1.serviceItemId)" +
            " AND   (?2 IS NULL OR ?2=order1.userId)" +
            " AND   (?3 IS NULL OR ?3=order1.patientId)" +
            " AND   (?4 IS NULL OR ?4=order1.categoryId)" +
            " AND   (?5 IS NULL OR ?5=order1.topCategoryId)" +
            " AND   (?6 IS NULL OR ?6=order1.orderStatus)" +
            " AND   (?7 IS NULL OR ?7=order1.vendorType)" +
            " AND   (?8 IS NULL OR ?8=order1.vendorId)" +
            " AND   (?9 IS NULL OR ?9=order1.vendorDepartId)")
    long countByConditions(Long itemId, Long userId, Long patientId, Long categoryId, Long topCategoryId, OrderStatus orderStatus, ServiceVendorType vendorType, Long vendorId, Long vendorDepartId);

    @Query("FROM ServiceOrderEntity order1" +
            " WHERE (?1 IS NULL OR ?1=order1.serviceItemId)" +
            " AND   (?2 IS NULL OR ?2=order1.userId)" +
            " AND   (?3 IS NULL OR ?3=order1.patientId)" +
            " AND   (?4 IS NULL OR ?4=order1.categoryId)" +
            " AND   (?5 IS NULL OR ?5=order1.topCategoryId)" +
            " AND   (?6 IS NULL OR ?6=order1.orderStatus)" +
            " AND   (?7 IS NULL OR ?7=order1.vendorType)" +
            " AND   (?8 IS NULL OR ?8=order1.vendorId)" +
            " AND   (?9 IS NULL OR ?9=order1.vendorDepartId)")
    Page<ServiceOrderEntity> findByConditions(Long itemId, Long userId, Long patientId, Long categoryId, Long topCategoryId, OrderStatus orderStatus, ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, Pageable page);


    List<ServiceOrderEntity> findByOrderStatus(OrderStatus orderStatus);


    @Query("FROM ServiceOrderEntity so" +
            " WHERE (so.orderStatus IN (?1))" +
            "   AND (" +
            "            (" +
            "                   (?2 IS NULL OR ?2=so.vendorType)" +
            "               AND (?3 IS NULL OR ?3=so.vendorId)" +
            "               AND (?4 IS NULL OR ?4=so.vendorDepartId)" +
            "            )" +
            "         OR (" +
            "                   (?5 IS NULL OR ?5=so.managedBy)" +
            "            )" +
            "        )")
    Page<ServiceOrderEntity> findByConditions(List<OrderStatus> orderStatus, ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, ManagedBy managedBy, Pageable page);
    @Query("FROM ServiceOrderEntity so" +
            " WHERE (so.orderStatus IN (?1))" +
            "   AND (?5 IS NULL OR ?5=so.managedBy)")
    Page<ServiceOrderEntity> findByConditions(List<OrderStatus> orderStatus, ManagedBy managedBy, Pageable page);


    @Query("SELECT so.id FROM ServiceOrderEntity so" +
            " WHERE (so.id IN (?1))" +
            " AND   (?2 IS NULL OR so.orderStatus=?2)")
    List<Long> findByIdInAndOrderStatus(List<Long> orderIds, OrderStatus orderStatus);
    List<ServiceOrderEntity> findByIdIn(List<Long> orderIds);
}
