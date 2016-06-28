package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.QuestionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByIdIn(List<Long> ids, Sort sort);

    List<QuestionEntity> findByQuestionnaireId(Long questionnaireId, Sort sort);

    List<QuestionEntity> findByQuestionnaireIdIn(List<Long> questionnaireIds, Sort sort);

    void deleteByQuestionnaireId(Long questionnaireId);
}
