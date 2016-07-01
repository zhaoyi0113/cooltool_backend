package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.CourseCategoryRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/8.
 */
public interface CourseCategoryRelationRepository  extends JpaRepository<CourseCategoryRelationEntity, Long> {
    List<CourseCategoryRelationEntity> findByCourseIdAndCourseCategoryId(long courseId, long categoryId, Sort sort);

    @Query("SELECT DISTINCT relation.courseCategoryId FROM CourseCategoryRelationEntity relation" +
            " WHERE (relation.status=?1 OR ?1 IS NULL)" +
            " AND relation.courseId=?2")
    List<Long> findCategoryIdByStatusAndCourseId(CommonStatus status, long courseId);

    @Query("SELECT DISTINCT relation.courseCategoryId FROM CourseCategoryRelationEntity relation" +
            " WHERE (relation.status=?1 OR ?1 IS NULL)" +
            " AND relation.courseId IN (?2)")
    List<Long> findCategoryIdByStatusAndCourseIdIn(CommonStatus status, List<Long> courseIds);

    @Query("FROM CourseCategoryRelationEntity relation" +
            " WHERE (relation.status=?1 OR ?1 IS NULL)" +
            " AND relation.courseId IN (?2)")
    List<CourseCategoryRelationEntity> findByStatusAndCourseIdIn(CommonStatus status, List<Long> courseIds);

    @Query("SELECT DISTINCT relation.courseId FROM CourseCategoryRelationEntity relation" +
            " WHERE (relation.status=?1 OR ?1 IS NULL)" +
            " AND relation.courseCategoryId=?2")
    List<Long> findCourseIdByStatusAndCategoryId(CommonStatus status, long categoryId);
}
