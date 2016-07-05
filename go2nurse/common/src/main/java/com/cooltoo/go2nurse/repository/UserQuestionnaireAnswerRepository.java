package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.UserQuestionnaireAnswerEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
public interface UserQuestionnaireAnswerRepository extends JpaRepository<UserQuestionnaireAnswerEntity, Long> {

    List<UserQuestionnaireAnswerEntity> findByUserIdAndGroupId(Long userId, Long questionnaireId, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserIdAndQuestionnaireId(Long userId, Long questionnaireId, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserIdAndGroupIdAndQuestionId(Long userId, Long groupId, Long questionId, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserIdAndQuestionIdIn(Long userId, List<Long> questionIds, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByUserId(long userId, Sort sort);

    List<UserQuestionnaireAnswerEntity> findByQuestionIdIn(List<Long> questionIds, Sort sort);
}
