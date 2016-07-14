package com.cooltoo.go2nurse.repository;

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

    long countByParentId(Long parentId);
    List<ServiceCategoryEntity> findByParentId(Long parentId, Sort sort);
    Page<ServiceCategoryEntity> findByParentId(Long parentId, Pageable sort);

    List<ServiceCategoryEntity> findByIdIn(List<Long> ids);

    @Modifying
    @Query("UPDATE ServiceCategoryEntity category SET category.parentId=0" +
            " WHERE category.parentId IN ?2 ")
    int setPatentIdToNone(List<Long> parentIds);
}
