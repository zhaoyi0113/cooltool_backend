package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.UserDiagnosticPointRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/14.
 */
public interface UserDiagnosticPointRelationRepository extends JpaRepository<UserDiagnosticPointRelationEntity, Long> {
    @Query("SELECT count(relation.id) FROM UserDiagnosticPointRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    long countByUserIdAndStatus(Long userId, CommonStatus status);
    @Query("FROM UserDiagnosticPointRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<UserDiagnosticPointRelationEntity> findByUserIdAndStatus(Long userId, CommonStatus status, Sort sort);
    @Query("FROM UserDiagnosticPointRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    Page<UserDiagnosticPointRelationEntity> findByUserIdAndStatus(Long userId, CommonStatus status, Pageable page);
}
