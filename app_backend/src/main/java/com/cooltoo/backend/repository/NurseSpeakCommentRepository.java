package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakCommentEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Test111 on 2016/3/18.
 */
public interface NurseSpeakCommentRepository extends JpaRepository<NurseSpeakCommentEntity, Long> {

    List<NurseSpeakCommentEntity> findNurseSpeakCommentByNurseSpeakIdIn(List<Long> nurseSpeakIds, Sort sort);

    List<NurseSpeakCommentEntity> findNurseSpeakCommentByNurseSpeakId(Long nurseSpeakId, Sort sort);
}
