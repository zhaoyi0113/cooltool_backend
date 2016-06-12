package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.CourseDiagnosticRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
public interface CourseDiagnosticRelationRepository extends JpaRepository<CourseDiagnosticRelationEntity, Long> {

    @Query("SELECT DISTINCT relation.courseId FROM CourseDiagnosticRelationEntity relation" +
            " WHERE relation.diagnosticId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)" +
            " AND relation.courseId IN (?3)")
    List<Long> findByDiagnosticIdAndStatusAndCoursesId(Long diagnosticId, CommonStatus status, List<Long> coursesId, Sort sort);

    @Query("SELECT DISTINCT relation.courseId FROM CourseDiagnosticRelationEntity relation" +
            " WHERE relation.diagnosticId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findByDiagnosticIdAndStatus(Long diagnosticId, CommonStatus status, Sort sort);

    @Query("SELECT DISTINCT relation.diagnosticId FROM CourseDiagnosticRelationEntity relation" +
            " WHERE (relation.courseId IN (?1))" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findByCourseIdAndStatus(List<Long> courseIds, CommonStatus status, Sort sort);

    List<CourseDiagnosticRelationEntity> findByDiagnosticIdAndCourseId(Long diagnosticId, Long courseId, Sort sort);
}
