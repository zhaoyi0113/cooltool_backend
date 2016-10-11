package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.nurse360.entities.NurseNotificationRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/11.
 */
public interface NurseNotificationRelationRepository extends JpaRepository<NurseNotificationRelationEntity, Long> {

    @Query("SELECT notification.notificationId FROM NurseNotificationRelationEntity notification" +
            " WHERE (?1 IS NULL OR notification.nurseId=?1)" +
            " AND   (?2 IS NULL OR notification.readingStatus=?2)")
    List<Long> findNotificationIdByNurseIdAndReadingStatus(Long nurseId, ReadingStatus readingStatus);

    @Query("FROM NurseNotificationRelationEntity notification" +
            " WHERE (?1 IS NULL OR notification.nurseId=?1)" +
            " AND   (?2 IS NULL OR notification.notificationId=?2)")
    List<NurseNotificationRelationEntity> findByNurseIdAndNotificationId(Long nurseId, Long notificationId);
}
