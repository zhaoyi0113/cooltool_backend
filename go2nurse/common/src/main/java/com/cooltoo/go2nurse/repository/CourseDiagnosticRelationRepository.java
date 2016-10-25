package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.CourseDiagnosticRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
public interface CourseDiagnosticRelationRepository extends JpaRepository<CourseDiagnosticRelationEntity, Long> {

    @Query("SELECT DISTINCT relation.diagnosticId FROM CourseDiagnosticRelationEntity relation" +
            " WHERE (relation.courseId IN (?1))" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findDiagnosticIdByStatusCourseId(List<Long> courseIds, CommonStatus status, Sort sort);

    List<CourseDiagnosticRelationEntity> findByDiagnosticIdAndCourseId(Long diagnosticId, Long courseId, Sort sort);


    @Query("FROM CourseDiagnosticRelationEntity relation" +
            " WHERE (?1 IS NULL OR relation.courseId=?1)")
    List<CourseDiagnosticRelationEntity> findByCourseId(Long courseId);

    @Query("FROM CourseDiagnosticRelationEntity relation" +
           " WHERE (?1 IS NULL OR relation.status=?1)" +
           " AND   relation.courseId IN (?2)")
    List<CourseDiagnosticRelationEntity> findByStatusCoursesIds(CommonStatus status, List<Long> coursesId);



    @Query("SELECT DISTINCT relation.courseId FROM CourseDiagnosticRelationEntity relation" +
            " WHERE (?1 IS NULL OR relation.status=?1)" +
            " AND   (?2 IS NULL OR relation.diagnosticId=?2)")
    List<Long> findCourseIdByStatusDiagnosticId(CommonStatus status, Long diagnosticId);

    @Query("SELECT DISTINCT relation.courseId FROM CourseDiagnosticRelationEntity relation" +
            " WHERE (?1 IS NULL OR relation.status=?1)" +
            " AND   (?2 IS NULL OR relation.diagnosticId=?2)" +
            " AND   relation.courseId IN (?3)")
    List<Long> findCourseIdByStatusDiagnosticIdCourseIds(CommonStatus status, Long diagnosticId, List<Long> courseIds);
}
