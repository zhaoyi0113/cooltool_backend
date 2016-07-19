package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.ServiceItemEntity;
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
public interface ServiceItemRepository extends JpaRepository<ServiceItemEntity, Long> {

    long countByCategoryId(Long categoryId);
    List<ServiceItemEntity> findByCategoryId(Long categoryId, Sort sort);
    Page<ServiceItemEntity> findByCategoryId(Long categoryId, Pageable sort);

    List<ServiceItemEntity> findByIdIn(List<Long> ids);

    @Modifying
    @Query("UPDATE ServiceItemEntity item SET item.categoryId=0" +
            " WHERE item.categoryId IN ?2 ")
    int setCategoryIdToNone(List<Long> categoryId);
}
