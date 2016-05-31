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
    @Query("SELECT DISTINCT comment.nurseSpeakId FROM NurseSpeakCommentEntity comment WHERE comment.commentMakerId=?1 AND comment.status<>?2")
    List<Long> findSpeakWithCommentUserMakeAndStatusNot(long userId, CommonStatus status);
    List<NurseSpeakCommentEntity> findByStatusNotAndIdIn(CommonStatus status, List<Long> commentIds);
    List<NurseSpeakCommentEntity> findByStatusNotAndNurseSpeakId(CommonStatus status, Long nurseSpeakId, Sort sort);
    List<NurseSpeakCommentEntity> findByStatusNotAndNurseSpeakIdIn(CommonStatus status, List<Long> nurseSpeakIds);
    List<NurseSpeakCommentEntity> findByStatusNotAndNurseSpeakIdIn(CommonStatus status, List<Long> nurseSpeakIds, Sort sort);
    @Query("SELECT comment.id FROM NurseSpeakCommentEntity comment WHERE comment.nurseSpeakId=?1 AND comment.commentMakerId=?2")
    List<Long> findCommentIdBySpeakIdAndMakerId(long nurseSpeakId, long commentMakerId);
}
