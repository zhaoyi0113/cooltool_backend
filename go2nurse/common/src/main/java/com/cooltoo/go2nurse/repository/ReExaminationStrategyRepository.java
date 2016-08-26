package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.ReExaminationStrategyEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/8/26.
 */
public interface ReExaminationStrategyRepository extends JpaRepository<ReExaminationStrategyEntity, Long> {

    List<ReExaminationStrategyEntity> findByDepartmentId(Integer departmentId, Sort sort);

}
