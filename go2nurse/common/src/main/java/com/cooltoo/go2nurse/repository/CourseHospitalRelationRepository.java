package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.CourseHospitalRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
@Deprecated
public interface CourseHospitalRelationRepository extends JpaRepository<CourseHospitalRelationEntity, Long> {
    @Query("SELECT DISTINCT relation.courseId FROM CourseHospitalRelationEntity relation" +
            " WHERE relation.hospitalId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findByHospitalIdAndStatus(Integer hospitalId, CommonStatus status, Sort sort);

    @Query("SELECT DISTINCT relation.hospitalId FROM CourseHospitalRelationEntity relation" +
            " WHERE relation.courseId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Integer> findByCourseIdAndStatus(Long courseId, CommonStatus status, Sort sort);

    List<CourseHospitalRelationEntity> findByHospitalIdAndCourseId(Integer hospitalId, Long courseId, Sort sort);
}
