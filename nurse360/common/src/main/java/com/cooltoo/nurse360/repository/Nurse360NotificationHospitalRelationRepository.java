package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.entities.Nurse360NotificationHospitalRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
public interface Nurse360NotificationHospitalRelationRepository extends JpaRepository<Nurse360NotificationHospitalRelationEntity, Long> {
    @Query("SELECT DISTINCT relation.notificationId FROM Nurse360NotificationHospitalRelationEntity relation" +
            " WHERE relation.hospitalId=?1" +
            " AND relation.departmentId=?2" +
            " AND (?3 IS NULL OR relation.status=?3)")
    List<Long> findByHospitalIdAndDepartmentIdAndStatus(Integer hospitalId, Integer departmentId, CommonStatus status);

    @Query("SELECT DISTINCT relation.notificationId FROM Nurse360NotificationHospitalRelationEntity relation" +
            " WHERE relation.hospitalId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findByHospitalIdAndStatus(Integer hospitalId, CommonStatus status);

    @Query("SELECT DISTINCT relation.hospitalId FROM Nurse360NotificationHospitalRelationEntity relation" +
            " WHERE relation.notificationId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Integer> findHospitalIdByNotificationIdAndStatus(Long notificationId, CommonStatus status, Sort sort);

    @Query("SELECT DISTINCT relation.departmentId FROM Nurse360NotificationHospitalRelationEntity relation" +
            " WHERE relation.notificationId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Integer> findDepartmentIdByNotificationIdAndStatus(Long notificationId, CommonStatus status, Sort sort);

    List<Nurse360NotificationHospitalRelationEntity> findByNotificationId(Long notificationId, Sort sort);
    List<Nurse360NotificationHospitalRelationEntity> findByNotificationIdIn(List<Long> notificationId);
}
