package com.cooltoo.nurse360.repository;

import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.nurse360.entities.Nurse360CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/8.
 */
public interface Nurse360CourseRepository extends JpaRepository<Nurse360CourseEntity, Long> {
    @Query("SELECT count(course.id) FROM Nurse360CourseEntity course" +
            " WHERE (?1 IS NULL OR course.name LIKE %?1)" +
            " AND (?2 IS NULL OR course.status=?2)")
    long countByNameLikeAndStatus(String nameLike, CourseStatus status);
    @Query("FROM Nurse360CourseEntity course" +
            " WHERE (?1 IS NULL OR course.name LIKE %?1)" +
            " AND (?2 IS NULL OR course.status=?2)")
    List<Nurse360CourseEntity> findByNameLikeAndStatus(String nameLike, CourseStatus status, Sort sort);
    @Query("FROM Nurse360CourseEntity course" +
            " WHERE (?1 IS NULL OR course.name LIKE %?1)" +
            " AND (?2 IS NULL OR course.status=?2)")
    Page<Nurse360CourseEntity> findByNameLikeAndStatus(String nameLike, CourseStatus status, Pageable page);
    long countByName(String name);
    List<Nurse360CourseEntity> findByName(String name, Sort sort);
    List<Nurse360CourseEntity> findByIdIn(List<Long> ids, Sort sort);
    List<Nurse360CourseEntity> findByIdIn(List<Long> ids, Page page);
    List<Nurse360CourseEntity> findByStatusAndIdIn(CourseStatus status, List<Long> ids, Sort sort);
    @Query("SELECT course.id FROM Nurse360CourseEntity course" +
            " WHERE (?1 IS NULL OR course.status=?1)" +
            " AND course.id IN (?2)")
    List<Long> findCourseIdByStatusAndIdIn(CourseStatus status, List<Long> ids, Sort sort);
    long countByUniqueId(String uniqueId);
    List<Nurse360CourseEntity> findByUniqueId(String uniqueId, Sort sort);
}
