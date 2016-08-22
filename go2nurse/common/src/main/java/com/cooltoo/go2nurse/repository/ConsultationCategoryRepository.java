package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.ConsultationCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/7/13.
 */
public interface ConsultationCategoryRepository extends JpaRepository<ConsultationCategoryEntity, Long> {

    long countByStatusIn(List<CommonStatus> statuses);
    List<ConsultationCategoryEntity> findByStatusIn(List<CommonStatus> statuses, Sort sort);
    Page<ConsultationCategoryEntity> findByStatusIn(List<CommonStatus> statuses, Pageable sort);

    List<ConsultationCategoryEntity> findByIdIn(List<Long> ids);
}
