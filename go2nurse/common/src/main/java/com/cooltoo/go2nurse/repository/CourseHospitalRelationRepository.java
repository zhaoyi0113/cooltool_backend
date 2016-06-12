package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.CourseHospitalRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/6/12.
 */
public interface CourseHospitalRelationRepository extends JpaRepository<CourseHospitalRelationEntity, Long> {

    List<CourseHospitalRelationEntity> findByHospitalIdAndCourseId(Integer hospitalId, Long courseId, Sort sort);
}
