package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.entities.Nurse360CourseHospitalRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
public interface Nurse360CourseHospitalRelationRepository extends JpaRepository<Nurse360CourseHospitalRelationEntity, Long> {
    @Query("SELECT DISTINCT relation.courseId FROM Nurse360CourseHospitalRelationEntity relation" +
            " WHERE relation.hospitalId=?1" +
            " AND relation.departmentId=?2" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findByHospitalIdAndDepartmentIdAndStatus(Integer hospitalId, Integer departmentId, CommonStatus status);

    @Query("SELECT DISTINCT relation.courseId FROM Nurse360CourseHospitalRelationEntity relation" +
            " WHERE relation.hospitalId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findByHospitalIdAndStatus(Integer hospitalId, CommonStatus status);

    @Query("SELECT DISTINCT relation.hospitalId FROM Nurse360CourseHospitalRelationEntity relation" +
            " WHERE relation.courseId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Integer> findHospitalIdByCourseIdAndStatus(Long courseId, CommonStatus status, Sort sort);

    @Query("SELECT DISTINCT relation.departmentId FROM Nurse360CourseHospitalRelationEntity relation" +
            " WHERE relation.courseId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Integer> findDepartmentIdByCourseIdAndStatus(Long courseId, CommonStatus status, Sort sort);

    List<Nurse360CourseHospitalRelationEntity> findByCourseId(Long courseId, Sort sort);
    List<Nurse360CourseHospitalRelationEntity> findByCourseIdIn(List<Long> courseId);
}
