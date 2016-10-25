package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.entities.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/8.
 */
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    @Query("SELECT count(course.id) FROM CourseEntity course" +
            " WHERE (?1 IS NULL OR course.name LIKE %?1)" +
            " AND (?2 IS NULL OR course.status=?2)")
    long countByNameLikeAndStatus(String nameLike, CourseStatus status);
    @Query("FROM CourseEntity course" +
            " WHERE (?1 IS NULL OR course.name LIKE %?1)" +
            " AND (?2 IS NULL OR course.status=?2)")
    List<CourseEntity> findByNameLikeAndStatus(String nameLike, CourseStatus status, Sort sort);
    @Query("FROM CourseEntity course" +
            " WHERE (?1 IS NULL OR course.name LIKE %?1)" +
            " AND (?2 IS NULL OR course.status=?2)")
    Page<CourseEntity> findByNameLikeAndStatus(String nameLike, CourseStatus status, Pageable page);



    List<CourseEntity> findByName(String name, Sort sort);
    long countByUniqueId(String uniqueId);
    List<CourseEntity> findByUniqueId(String uniqueId, Sort sort);
    @Query("SELECT course.id FROM CourseEntity course" +
            " WHERE (?1 IS NULL OR course.status=?1)" +
            " AND course.id IN (?2)")
    List<Long> findCourseIdByStatusAndIdIn(CourseStatus status, List<Long> ids, Sort sort);
    @Query("FROM CourseEntity course" +
            " WHERE course.id IN (?1)")
    List<CourseEntity> findCourseByIdIn(List<Long> ids, Sort sort);
    @Query("FROM CourseEntity course" +
            " WHERE course.id IN (?1)")
    Page<CourseEntity> findCourseByIdIn(List<Long> ids, Pageable page);
    @Query("FROM CourseEntity course" +
            " WHERE (?1 IS NULL OR course.status=?1)" +
            " AND course.id IN (?2)")
    List<CourseEntity> findCourseByStatusAndIdIn(CourseStatus status, List<Long> ids, Sort sort);
    @Query("FROM CourseEntity course" +
           " WHERE (?1 IS NULL OR course.status=?1)" +
           " AND course.id IN (?2)")
    Page<CourseEntity> findCourseByStatusAndIdIn(CourseStatus status, List<Long> ids, Pageable page);
}
