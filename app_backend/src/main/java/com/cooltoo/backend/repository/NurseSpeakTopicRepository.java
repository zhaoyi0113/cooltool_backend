package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseSpeakTopicEntity;
import com.cooltoo.constants.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by hp on 2016/6/2.
 */
public interface NurseSpeakTopicRepository extends JpaRepository<NurseSpeakTopicEntity, Long> {
    long countByTitle(String title);

    @Query("SELECT count(topic.id) FROM NurseSpeakTopicEntity topic" +
            " WHERE (?1 IS NULL OR topic.title LIKE %?1)" +
            " AND (?2 IS NULL OR topic.status=?2)")
    long countByTitleAndStatus(String fuzzyTitle, CommonStatus status);
    @Query("FROM NurseSpeakTopicEntity topic" +
            " WHERE (?1 IS NULL OR topic.title LIKE %?1)" +
            " AND (?2 IS NULL OR topic.status=?2)")
    Page<NurseSpeakTopicEntity> findByTitleAndStatus(String fuzzyTitle, CommonStatus status, Pageable page);


}
