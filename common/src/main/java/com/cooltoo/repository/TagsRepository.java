package com.cooltoo.repository;

import com.cooltoo.entities.TagsEntity;
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
    List<TagsEntity> findByCategoryId(long categoryId, Sort sort);
    List<TagsEntity> findByCategoryIdAndName(long categoryId, String name);
    void deleteByCategoryId(long categoryId);

}
