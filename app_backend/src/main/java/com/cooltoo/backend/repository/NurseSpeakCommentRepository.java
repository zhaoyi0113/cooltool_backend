package com.cooltoo.backend.repository;

import com.cooltoo.backend.beans.NurseSpeakCommentBean;
import com.cooltoo.backend.entities.NurseSpeakCommentEntity;
import com.cooltoo.constants.CommonStatus;
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
    @Query("SELECT DISTINCT comment.nurseSpeakId FROM NurseSpeakCommentEntity comment WHERE comment.commentMakerId=?1 AND comment.status=?2")
    List<Long> findSpeakWithCommentUserMake(long userId, CommonStatus status);
    List<NurseSpeakCommentEntity> findByStatusAndIdIn(CommonStatus status, List<Long> commentIds);
    List<NurseSpeakCommentEntity> findByStatusAndNurseSpeakId(CommonStatus status, Long nurseSpeakId, Sort sort);
    List<NurseSpeakCommentEntity> findByStatusAndNurseSpeakIdIn(CommonStatus status, List<Long> nurseSpeakIds);
    List<NurseSpeakCommentEntity> findByStatusAndNurseSpeakIdIn(CommonStatus status, List<Long> nurseSpeakIds, Sort sort);
    @Query("FROM NurseSpeakCommentEntity comment " +
            " WHERE (comment.commentReceiverId=?1 OR comment.commentReceiverId=0) AND comment.nurseSpeakId IN (?2)")
    Page<NurseSpeakCommentEntity> findByStatusAndReceiverIdAndSpeakIdIn(CommonStatus status, long receiverId, Iterable<Long> nurseSpeakIds, Pageable page);

    @Query("SELECT comment.id FROM NurseSpeakCommentEntity comment WHERE comment.nurseSpeakId=?1 AND comment.commentMakerId=?2")
    List<Long> findCommentIdBySpeakIdAndMakerId(long nurseSpeakId, long commentMakerId);
}
