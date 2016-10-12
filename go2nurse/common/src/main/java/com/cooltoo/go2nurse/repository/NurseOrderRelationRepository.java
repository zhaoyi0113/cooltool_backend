package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 16/10/8.
 */
public interface NurseOrderRelationRepository extends JpaRepository<NurseOrderRelationEntity, Long> {

    List<NurseOrderRelationEntity> findByNurseIdAndStatus(long nurseId, CommonStatus status, Sort sort);
    List<NurseOrderRelationEntity> findByNurseIdAndOrderId(long nurseId, long orderId, Sort sort);
    List<NurseOrderRelationEntity> findByOrderId(long orderId, Sort sort);
    List<NurseOrderRelationEntity> findByOrderIdInAndStatus(List<Long> orderIds, CommonStatus status);
}
