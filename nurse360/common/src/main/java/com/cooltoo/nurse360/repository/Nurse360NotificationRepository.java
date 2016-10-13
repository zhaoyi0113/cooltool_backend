package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.entities.Nurse360NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/11.
 */
public interface Nurse360NotificationRepository extends JpaRepository<Nurse360NotificationEntity, Long> {
    @Query("SELECT count(notification.id) FROM Nurse360NotificationEntity notification" +
            " WHERE (?1 IS NULL OR notification.title LIKE %?1)" +
            " AND (?2 IS NULL OR notification.status=?2)")
    long countByTitleLikeAndStatus(String titleLike, CommonStatus status);
    @Query("FROM Nurse360NotificationEntity notification" +
            " WHERE (?1 IS NULL OR notification.title LIKE %?1)" +
            " AND (?2 IS NULL OR notification.status=?2)")
    List<Nurse360NotificationEntity> findByTitleLikeAndStatus(String titleLike, CommonStatus status, Sort sort);
    @Query("FROM Nurse360NotificationEntity notification" +
            " WHERE (?1 IS NULL OR notification.title LIKE %?1)" +
            " AND (?2 IS NULL OR notification.status=?2)")
    Page<Nurse360NotificationEntity> findByTitleLikeAndStatus(String titleLike, CommonStatus status, Pageable page);
    long countByTitle(String title);
    List<Nurse360NotificationEntity> findByIdIn(List<Long> ids, Sort sort);
    List<Nurse360NotificationEntity> findByIdIn(List<Long> ids, Pageable page);
    List<Nurse360NotificationEntity> findByStatusAndIdIn(CommonStatus status, List<Long> ids, Sort sort);
    @Query("SELECT notification.id FROM Nurse360NotificationEntity notification" +
            " WHERE (?1 IS NULL OR notification.status=?1)" +
            " AND notification.id IN (?2)")
    List<Long> findNotificationIdByStatusAndIdIn(CommonStatus status, List<Long> ids, Sort sort);
}
