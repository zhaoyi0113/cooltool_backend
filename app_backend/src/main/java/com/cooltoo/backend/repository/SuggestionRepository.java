package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.SuggestionEntity;
import com.cooltoo.constants.SuggestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/4/6.
 */
public interface SuggestionRepository extends CrudRepository<SuggestionEntity, Long> {

    Page<SuggestionEntity> findAll(Pageable page);
    Page<SuggestionEntity> findByStatus(SuggestionStatus status, Pageable page);
    List<SuggestionEntity> findByIdIn(List<Long> ids);
    List<SuggestionEntity> deleteByIdIn(List<Long> ids);
    long countByStatus(SuggestionStatus status);
}
