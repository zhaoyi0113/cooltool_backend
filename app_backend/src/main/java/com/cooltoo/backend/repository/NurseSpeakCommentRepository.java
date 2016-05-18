package com.cooltoo.backend.repository;

import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.entities.NurseSpeakCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Test111 on 2016/3/18.
 */
public interface NurseSpeakCommentRepository extends JpaRepository<NurseSpeakCommentEntity, Long> {
    @Query("SELECT DISTINCT comment.nurseSpeakId FROM NurseSpeakCommentEntity comment WHERE comment.commentMakerId=?1")
    List<Long> findSpeakWithCommentUserMake(long userId);
    List<NurseSpeakCommentEntity> findByIdIn(List<Long> commentIds);
    List<NurseSpeakCommentEntity> findByNurseSpeakId(Long nurseSpeakId, Sort sort);
    List<NurseSpeakCommentEntity> findByNurseSpeakIdIn(List<Long> nurseSpeakIds);
    List<NurseSpeakCommentEntity> findByNurseSpeakIdIn(List<Long> nurseSpeakIds, Sort sort);
    @Query("FROM NurseSpeakCommentEntity comment " +
            " WHERE (comment.commentReceiverId=?1 OR comment.commentReceiverId=0) AND comment.nurseSpeakId IN (?2)")
    Page<NurseSpeakCommentEntity> findByReceiverIdAndSpeakIdIn(long receiverId, Iterable<Long> nurseSpeakIds, Pageable page);
}
