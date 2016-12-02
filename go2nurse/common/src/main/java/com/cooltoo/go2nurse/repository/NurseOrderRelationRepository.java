package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 16/10/8.
 */
public interface NurseOrderRelationRepository extends JpaRepository<NurseOrderRelationEntity, Long> {
    @Query("SELECT nor.orderId FROM NurseOrderRelationEntity nor" +
            " WHERE nor.nurseId=?1" +
            " AND  (?2 IS NULL OR nor.status=?2)")
    List<Long> findByNurseIdAndStatus(long nurseId, CommonStatus status, Sort sort);
    List<NurseOrderRelationEntity> findByNurseIdAndOrderId(long nurseId, long orderId, Sort sort);
    List<NurseOrderRelationEntity> findByOrderId(long orderId, Sort sort);
    List<NurseOrderRelationEntity> findByOrderIdInAndStatus(List<Long> orderIds, CommonStatus status, Sort sort);
    @Query("SELECT nor.orderId FROM NurseOrderRelationEntity nor WHERE nor.nurseId=?1")
    List<Long> findOrderIdByNurseId(long nurseId);
    List<NurseOrderRelationEntity> findByNurseIdInAndStatus(List<Long> nursesId, CommonStatus status);
}
