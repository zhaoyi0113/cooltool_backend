package com.cooltoo.nurse360.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.entities.Nurse360CourseCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/6/8.
 */
public interface Nurse360CourseCategoryRepository extends JpaRepository<Nurse360CourseCategoryEntity, Long> {
    long countByStatus(CommonStatus status);
    long countByName(String name);
    Page<Nurse360CourseCategoryEntity> findByStatus(CommonStatus status, Pageable page);
    List<Nurse360CourseCategoryEntity> findByIdIn(List<Long> ids, Sort sort);
    List<Nurse360CourseCategoryEntity> findByStatusAndIdIn(CommonStatus status, List<Long> ids, Sort sort);
}
