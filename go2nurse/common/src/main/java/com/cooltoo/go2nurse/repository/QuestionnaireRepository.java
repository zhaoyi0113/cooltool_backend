package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.QuestionnaireEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireEntity, Long> {
    long countByTitle(String title);
    List<QuestionnaireEntity> findByIdIn(List<Long> ids, Sort sort);
    List<QuestionnaireEntity> findByHospitalId(Integer hospitalId, Sort sort);
}
