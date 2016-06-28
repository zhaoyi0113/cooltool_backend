package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.UserQuestionnaireAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/6/28.
 */
public interface UserQuestionnaireAnswerRepository extends JpaRepository<UserQuestionnaireAnswerEntity, Long> {
}
