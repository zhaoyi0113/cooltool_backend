package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.WalletInOutType;
import com.cooltoo.go2nurse.entities.NurseWalletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 12/12/2016.
 */
public interface NurseWalletRepository extends JpaRepository<NurseWalletEntity, Long> {

    List<NurseWalletEntity> findByNurseId(long nurseId);
    List<NurseWalletEntity> findByNurseIdAndReasonAndReasonId(long nurseId, WalletInOutType inOutType, long reasonId, Sort sort);

    @Query("SELECT nw.nurseId, nw.amount FROM NurseWalletEntity nw" +
            " WHERE nw.nurseId IN (?1)")
    List<Object[]> findNurseWalletInOut(List<Long> nurseIds);

    @Query("FROM NurseWalletEntity nw" +
            " WHERE (?1 IS NULL OR ?1=nw.nurseId)" +
            "   AND (nw.status IN (?2))")
    Page<NurseWalletEntity> findByNurseIdAndStatus(Long nurseId, List<CommonStatus> statuses, Pageable page);
}
