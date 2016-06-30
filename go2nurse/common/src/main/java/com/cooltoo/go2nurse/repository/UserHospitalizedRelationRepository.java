package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.entities.UserHospitalizedRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hp on 2016/6/14.
 */
public interface UserHospitalizedRelationRepository extends JpaRepository<UserHospitalizedRelationEntity, Long> {

    List<UserHospitalizedRelationEntity> findByUserIdAndHospitalIdAndDepartmentIdAndGroupId(Long userId, Integer hospitalId, Integer departmentId, Long groupId, Sort sort);

    @Query("SELECT count(relation.id) FROM UserHospitalizedRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    long countByUserIdAndStatus(Long userId, CommonStatus status);
    @Query("FROM UserHospitalizedRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    List<UserHospitalizedRelationEntity> findByUserIdAndStatus(Long userId, CommonStatus status, Sort sort);
    @Query("FROM UserHospitalizedRelationEntity relation" +
            " WHERE relation.userId=?1" +
            " AND (?2 IS NULL OR relation.status=?2)")
    Page<UserHospitalizedRelationEntity> findByUserIdAndStatus(Long userId, CommonStatus status, Pageable page);

    List<UserHospitalizedRelationEntity> findByStatusAndUserIdAndHospitalIdAndDepartmentId(CommonStatus status, Long userId, Integer hospitalId, Integer departmentid);
}
