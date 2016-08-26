package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.ReExaminationStrategyEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/8/26.
 */
public interface ReExaminationStrategyRepository extends JpaRepository<ReExaminationStrategyEntity, Long> {

    long countByStatusIn(List<CommonStatus> statuses);
    List<ReExaminationStrategyEntity> findByStatusIn(List<CommonStatus> statuses, Sort sort);
    List<ReExaminationStrategyEntity> findByStatusIn(List<CommonStatus> statuses, Pageable page);

    List<ReExaminationStrategyEntity> findByDepartmentId(Integer departmentId, Sort sort);

}
