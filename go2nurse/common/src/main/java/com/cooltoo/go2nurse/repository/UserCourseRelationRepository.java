package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.entities.UserCourseRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/14.
 */
public interface UserCourseRelationRepository extends JpaRepository<UserCourseRelationEntity, Long> {
    @Query("SELECT count(relation.id) FROM UserCourseRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (relation.readingStatus IN (?2))" +
            " AND (?3 IS NULL OR relation.status=?3)")
    long countByUserIdAndReadStatusAndStatus(Long userId, List<ReadingStatus> readingStatus, CommonStatus status);
    @Query("SELECT relation.courseId FROM UserCourseRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (relation.readingStatus IN (?2))" +
            " AND (?3 IS NULL OR relation.status=?3)")
    List<Long> findCourseIdByUserIdAndReadStatusAndStatus(Long userId, List<ReadingStatus> readingStatus, CommonStatus status, Sort sort);
    @Query("FROM UserCourseRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (relation.readingStatus IN (?2))" +
            " AND (?3 IS NULL OR relation.status=?3)")
    List<UserCourseRelationEntity> findByUserIdAndReadStatusAndStatus(Long userId, List<ReadingStatus> readingStatus, CommonStatus status, Sort sort);
    @Query("FROM UserCourseRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (relation.readingStatus IN (?2))" +
            " AND (?3 IS NULL OR relation.status=?3)")
    Page<UserCourseRelationEntity> findByUserIdAndReadStatusAndStatus(Long userId, List<ReadingStatus> readingStatus, CommonStatus status, Pageable page);

    List<UserCourseRelationEntity> findByUserIdAndCourseId(Long userId, Long courseId, Sort sort);
}
