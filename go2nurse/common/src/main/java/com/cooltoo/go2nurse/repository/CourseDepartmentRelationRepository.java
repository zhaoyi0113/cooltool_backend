package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.CourseDepartmentRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Long> findByDepartmentIdAndStatus(Integer departmentId, CommonStatus status, Sort sort);

    @Query("SELECT DISTINCT relation.departmentId FROM CourseDepartmentRelationEntity relation" +
            " WHERE (relation.courseId IN (?1))" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<Integer> findByCourseIdAndStatus(List<Long> courseIds, CommonStatus status, Sort sort);

    List<CourseDepartmentRelationEntity> findByDepartmentIdAndCourseId(Integer departmentId, Long courseId, Sort sort);
}
