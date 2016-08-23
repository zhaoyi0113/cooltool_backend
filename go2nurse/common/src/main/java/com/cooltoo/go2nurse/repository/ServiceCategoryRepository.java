package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.ServiceCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/7/13.
 */
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategoryEntity, Long> {

    long countByParentIdAndStatusIn(Long parentId, List<CommonStatus> statuses);
    List<ServiceCategoryEntity> findByParentIdAndStatusIn(Long parentId, List<CommonStatus> statuses, Sort sort);
    Page<ServiceCategoryEntity> findByParentIdAndStatusIn(Long parentId, List<CommonStatus> statuses, Pageable sort);

    List<ServiceCategoryEntity> findByIdIn(List<Long> ids);

    @Query("SELECT category.id, category.parentId FROM ServiceCategoryEntity category WHERE category.id IN (?1)")
    List<Object[]> findIdAndParentIdByIdIn(List<Long> ids);

    @Modifying
    @Query("UPDATE ServiceCategoryEntity category SET category.parentId=0" +
            " WHERE category.parentId IN (?1) ")
    int setPatentIdToNone(List<Long> parentIds);
}
