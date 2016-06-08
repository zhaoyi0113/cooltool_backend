package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.CourseCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/6/8.
 */
public interface CourseCategoryRepository extends JpaRepository<CourseCategoryEntity, Long> {
    long countByStatus(CommonStatus status);
    long countByName(String name);
    Page<CourseCategoryEntity> findByStatus(CommonStatus status, Pageable page);
    List<CourseCategoryEntity> findByStatusAndIdIn(CommonStatus status, List<Long> ids, Sort sort);
}
