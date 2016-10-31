package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.CourseDepartmentRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
public interface CourseDepartmentRelationRepository extends JpaRepository<CourseDepartmentRelationEntity, Long> {

    @Query("SELECT DISTINCT relation.courseId FROM CourseDepartmentRelationEntity relation" +
            " WHERE relation.departmentId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)" +
            " AND relation.courseId IN (?3)")
    List<Long> findCourseIdByDepartmentAndStatusAndCourses(Integer departmentId, CommonStatus status, List<Long> coursesId, Sort sort);

    @Query("SELECT DISTINCT relation.courseId FROM CourseDepartmentRelationEntity relation" +
            " WHERE relation.departmentId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findCourseIdByDepartmentIdAndStatus(Integer departmentId, CommonStatus status, Sort sort);

    @Query("SELECT DISTINCT relation.departmentId FROM CourseDepartmentRelationEntity relation" +
            " WHERE (relation.courseId IN (?1))" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Integer> findDepartmentIdByCourseIdAndStatus(List<Long> courseIds, CommonStatus status, Sort sort);

    @Query("SELECT DISTINCT relation.hospitalId FROM CourseDepartmentRelationEntity relation" +
            " WHERE (?1 IS NULL OR relation.courseId=?1)" +
            " AND   (?2 IS NULL OR relation.status=?2)")
    List<Integer> findHospitalIdByCourseIdAndStatus(Long courseId, CommonStatus status, Sort sort);

    @Query("FROM CourseDepartmentRelationEntity relation" +
           " WHERE (?1 IS NULL OR relation.courseId=?1)")
    List<CourseDepartmentRelationEntity> findByCourseId(Long courseId);

    List<CourseDepartmentRelationEntity> findByCourseIdIn(List<Long> courseIds);

    @Query("SELECT DISTINCT relation.courseId FROM CourseDepartmentRelationEntity relation" +
            " WHERE " +
            "     (?1 IS NULL OR relation.hospitalId=?1)" +
            " AND (?2 IS NULL OR relation.departmentId=?2)" +
            " AND (?3 IS NULL OR relation.status=?3)")
    List<Long> findCourseIdByHospitalDepartmentAndStatus(Integer hospitalId, Integer departmentId, CommonStatus status);
}
