package com.cooltoo.repository;

import com.cooltoo.entities.TagsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
public interface TagsRepository extends CrudRepository<TagsEntity, Long> {
    List<TagsEntity> findAll(Sort sort);
    Page<TagsEntity> findAll(Pageable page);
    List<TagsEntity> findByIdIn(List<Long> ids);
    List<TagsEntity> findByCategoryIdIn(List<Long> categoryIds);
    List<TagsEntity> findByCategoryId(long categoryId, Sort sort);
    List<TagsEntity> findByCategoryIdLessThanEqual(long categoryId, Sort sort);
    long countByName(String name);
    long countByCategoryIdAndName(long categoryId, String name);
    void deleteByCategoryId(long categoryId);
    void deleteByCategoryIdIn(List<Long> categoryIds);

}
