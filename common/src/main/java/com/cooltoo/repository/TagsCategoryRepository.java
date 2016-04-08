package com.cooltoo.repository;

import com.cooltoo.entities.TagsCategoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
public interface TagsCategoryRepository extends CrudRepository<TagsCategoryEntity, Long> {
    public List<TagsCategoryEntity> findAll(Sort sort);
    public List<TagsCategoryEntity> findByIdIn(List<Long> ids);
    public long                     countByName(String name);
}
