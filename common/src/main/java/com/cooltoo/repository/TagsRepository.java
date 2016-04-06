package com.cooltoo.repository;

import com.cooltoo.entities.TagsEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
public interface TagsRepository extends CrudRepository<TagsEntity, Long> {
    List<TagsEntity> findByCategoryId(long categoryId, Sort sort);
    void deleteByCategoryId(long categoryId);
}
