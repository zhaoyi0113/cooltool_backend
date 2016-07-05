package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.QuestionnaireCategoryEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/7/5.
 */
public interface QuestionnaireCategoryRepository extends JpaRepository<QuestionnaireCategoryEntity, Long> {
    long countByName(String name);
    List<QuestionnaireCategoryEntity> findByIdIn(List<Long> ids, Sort sort);
}
