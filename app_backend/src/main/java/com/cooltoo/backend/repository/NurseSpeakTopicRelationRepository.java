package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakTopicRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/2.
 */
public interface NurseSpeakTopicRelationRepository extends JpaRepository<NurseSpeakTopicRelationEntity, Long> {

    @Query("SELECT DISTINCT topicRelation.userId FROM NurseSpeakTopicRelationEntity topicRelation" +
            " WHERE topicRelation.topicId=?1")
    List<Long> getUserIdInTopic(long topicId, Sort sort);

    @Query("SELECT DISTINCT topicRelation.speakId" +
            " FROM NurseSpeakTopicRelationEntity topicRelation" +
            " WHERE topicRelation.topicId=?1")
    List<Long> findSpeakIdsInTopic(long topicId, Sort sort);

    @Query("SELECT DISTINCT topicRelation.topicId" +
            " FROM NurseSpeakTopicRelationEntity topicRelation" +
            " WHERE topicRelation.speakId=?1")
    List<Long> findTopicIdsBySpeakId(long speakId, Sort sort);

    @Query("SELECT topicRelation.speakId, topicRelation.topicId" +
            " FROM NurseSpeakTopicRelationEntity topicRelation" +
            " WHERE topicRelation.speakId IN (?1)" +
            " GROUP BY topicRelation.speakId")
    List<Long[]> findTopicIdsBySpeakIds(List<Long> speakIds, Sort sort);

//    @Modifying
//    @Query("UPDATE NurseSpeakTopicRelationEntity topicRelation" +
//            " SET topicRelation.status=?3" +
//            " WHERE topicRelation.speakId=?1" +
//            " AND topicRelation.status IN (?2)" +
//            " AND ?3 IS NOT NULL")
//    int updateStatusBySpeakId(long speakId, List<CommonStatus> originalStatuses, CommonStatus status);

//    @Modifying
//    @Query("UPDATE NurseSpeakTopicRelationEntity topicRelation" +
//            " SET topicRelation.status=?3" +
//            " WHERE topicRelation.topicId=?1" +
//            " AND topicRelation.status IN (?2)" +
//            " AND ?3 IS NOT NULL")
//    int updateStatusByTopicId(long topicId, List<CommonStatus> originalStatuses, CommonStatus status);
}
