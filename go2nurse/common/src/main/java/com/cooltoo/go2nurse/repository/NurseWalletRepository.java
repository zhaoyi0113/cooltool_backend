package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.WalletInOutType;
import com.cooltoo.go2nurse.constants.WalletProcess;
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

    @Query("FROM NurseWalletEntity nw" +
            " WHERE nw.nurseId IN (?1)" +
            "   AND (?2 IS NULL OR ?2=nw.process)")
    List<NurseWalletEntity> findNurseWalletInOut(List<Long> nurseIds, WalletProcess process);

    @Query("FROM NurseWalletEntity nw" +
            " WHERE (?1 IS NULL OR ?1=nw.nurseId)" +
            "   AND (?2 IS NULL OR ?2=nw.process)" +
            "   AND (nw.status IN (?3))")
    Page<NurseWalletEntity> findByNurseIdAndStatus(Long nurseId, WalletProcess process, List<CommonStatus> statuses, Pageable page);




    @Query("SELECT count(nw.id) FROM NurseWalletEntity nw" +
            " WHERE (?1 IS NULL OR ?1=nw.nurseId)" +
            "   AND (?2 IS NULL OR ?2=nw.process)" +
            "   AND (?3 IS NULL OR ?3=nw.reason)" +
            "   AND (?4 IS NULL OR (nw.summary LIKE %?4))")
    long countByConditions(Long nurseId, WalletProcess process, WalletInOutType reason, String summary);

    @Query("FROM NurseWalletEntity nw" +
            " WHERE (?1 IS NULL OR ?1=nw.nurseId)" +
            "   AND (?2 IS NULL OR ?2=nw.process)" +
            "   AND (?3 IS NULL OR ?3=nw.reason)" +
            "   AND (?4 IS NULL OR (nw.summary LIKE %?4))")
    Page<NurseWalletEntity> findByConditions(Long nurseId, WalletProcess process, WalletInOutType reason, String summary, Pageable page);
}
