package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.SuggestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
public interface SuggestionRepository extends CrudRepository<SuggestionEntity, Long> {
    public Page<SuggestionEntity> findAll(Pageable page);
    public List<SuggestionEntity> deleteByIdIn(List<Long> ids);
}
