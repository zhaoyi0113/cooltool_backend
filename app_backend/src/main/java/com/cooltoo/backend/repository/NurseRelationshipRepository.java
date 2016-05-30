package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.NurseRelationshipEntity;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.RelationshipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/5/30.
 */
public interface NurseRelationshipRepository extends JpaRepository<NurseRelationshipEntity, Long> {
    NurseRelationshipEntity findByUserIdAndRelativeUserIdAndRelationType(long userId, long relativeUserId, RelationshipType relationType);

    @Query("SELECT count(relation.id) FROM NurseRelationshipEntity relation " +
            " WHERE (relation.userId=?1 OR 0=?1)" +
            " AND (relation.relativeUserId=?2 OR 0=?2)" +
            " AND (relation.relationType=?3 OR (?3 IS NULL))" +
            " AND (relation.status=?4 OR ?4 IS NULL)")
    long countByConditions(long userId, long relativeUserId, RelationshipType relationType, CommonStatus status);

    @Query("FROM NurseRelationshipEntity relation " +
            " WHERE (relation.userId=?1 OR 0=?1)" +
            " AND (relation.relativeUserId=?2 OR 0=?2)" +
            " AND (relation.relationType=?3 OR (?3 IS NULL))" +
            " AND (relation.status=?4 OR ?4 IS NULL)")
    Page<NurseRelationshipEntity> findByConditions(long userId, long relativeUserId, RelationshipType relationType, CommonStatus status, Pageable page);

    @Query("FROM NurseRelationshipEntity relation " +
            " WHERE (relation.userId=?1 OR 0=?1)" +
            " AND (relation.relativeUserId=?2 OR 0=?2)" +
            " AND (relation.relationType=?3 OR (?3 IS NULL))" +
            " AND (relation.status=?4 OR ?4 IS NULL)")
    List<NurseRelationshipEntity> findByConditions(long userId, long relativeUserId, RelationshipType relationType, CommonStatus status, Sort sort);

    @Query("SELECT relation.relativeUserId FROM NurseRelationshipEntity relation " +
            " WHERE (relation.userId=?1)" +
            " AND (relation.relationType=?2 OR (?2 IS NULL))" +
            " AND (relation.status=?3 OR ?3 IS NULL)")
    List<Long> findRelativeUserIdByCondition(long userId, RelationshipType relationshipType, CommonStatus status);
}
