package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakTopicRelationEntity;
import com.cooltoo.constants.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/2.
 */
public interface NurseSpeakTopicRelationRepository extends JpaRepository<NurseSpeakTopicRelationEntity, Long> {

    @Query("SELECT count(topicRelation.id)" +
            " FROM NurseSpeakTopicRelationEntity topicRelation" +
            " WHERE topicRelation.topicId=?1" +
            " AND ?2 IS NOT NULL" +
            " AND topicRelation.status=?2")
    long countSpeakInTopic(long topicId, CommonStatus statues);

    @Query("SELECT topicRelation.speakId" +
            " FROM NurseSpeakTopicRelationEntity topicRelation" +
            " WHERE topicRelation.topicId=?1" +
            " AND ?2 IS NOT NULL" +
            " AND topicRelation.status=?2")
    Page<Long> findSpeakIdsInTopic(long topicId, CommonStatus status, Pageable page);

    @Query("SELECT DISTINCT topicRelation.topicId" +
            " FROM NurseSpeakTopicRelationEntity topicRelation" +
            " WHERE topicRelation.speakId=?1" +
            " AND ?2 IS NOT NULL" +
            " AND topicRelation.status=?2")
    long findTopicIdsBySpeakId(long speakId, CommonStatus status);

    @Modifying
    @Query("UPDATE NurseSpeakTopicRelationEntity topicRelation" +
            " SET topicRelation.status = ?3" +
            " WHERE topicRelation.speakId = ?1" +
            " AND ?2 IS NOT NULL" +
            " AND topicRelation.status IN (?2)" +
            " AND ?3 IS NOT NULL")
    long updateStatusBySpeakId(long speakId, List<CommonStatus> originalStatuses, CommonStatus status);

    @Modifying
    @Query("UPDATE NurseSpeakTopicRelationEntity topicRelation" +
            " SET topicRelation.status = ?3" +
            " WHERE topicRelation.topicId = ?1" +
            " AND ?2 IS NOT NULL" +
            " AND topicRelation.status IN (?2)" +
            " AND ?3 IS NOT NULL")
    long updateStatusByTopicId(long topicId, List<CommonStatus> originalStatuses, CommonStatus status);
}
