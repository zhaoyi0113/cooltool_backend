package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.nurse360.entities.Nurse360NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/11.
 */
public interface Nurse360NotificationRepository extends JpaRepository<Nurse360NotificationEntity, Long> {

    @Query("SELECT count(notification.id) FROM Nurse360NotificationEntity notification" +
            " WHERE (?1 IS NULL OR (notification.title LIKE %?1))" +
            "   AND (notification.status IN (?2))" +
            "   AND (?3 IS NULL OR ?3=notification.vendorType)" +
            "   AND (?4 IS NULL OR ?4=notification.vendorId)" +
            "   AND (?5 IS NULL OR ?5=notification.departId)")
    long countByConditions(String titleLike, List<CommonStatus> status, ServiceVendorType vendorType, Long vendorId, Long departId);
    @Query("FROM Nurse360NotificationEntity notification" +
            " WHERE (?1 IS NULL OR (notification.title LIKE %?1))" +
            "   AND (notification.status IN (?2))" +
            "   AND (?3 IS NULL OR ?3=notification.vendorType)" +
            "   AND (?4 IS NULL OR ?4=notification.vendorId)" +
            "   AND (?5 IS NULL OR ?5=notification.departId)")
    Page<Nurse360NotificationEntity> findByConditions(String titleLike, List<CommonStatus> status, ServiceVendorType vendorType, Long vendorId, Long departId, Pageable page);

    @Query("SELECT notification.id FROM Nurse360NotificationEntity notification" +
            " WHERE (notification.status IN (?1))" +
            "   AND (?2 IS NULL OR ?2=notification.vendorType)" +
            "   AND (?3 IS NULL OR ?3=notification.vendorId)" +
            "   AND (?4 IS NULL OR ?4=notification.departId)")
    List<Object> findNotificationIdByConditions(List<CommonStatus> status, ServiceVendorType vendorType, Long vendorId, Long departId);

    List<Nurse360NotificationEntity> findByIdIn(List<Long> ids, Pageable page);
}
