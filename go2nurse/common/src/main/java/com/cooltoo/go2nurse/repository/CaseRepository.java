package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.entities.CaseEntity;
import com.cooltoo.go2nurse.entities.UserConsultationTalkEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/23.
 */
public interface CaseRepository extends JpaRepository<CaseEntity, Long>{

    @Query("SELECT ce.casebookId, ce.id FROM CaseEntity ce" +
            " WHERE (?1 IS NULL OR ce.status<>?1)" +
            "   AND (ce.casebookId IN (?2))")
    List<Object[]> findCasebookIdAndCaseId(CommonStatus statusNot, List<Long> casebookIds);
    @Query("SELECT ce.casebookId, ce.time FROM CaseEntity ce" +
            " WHERE (?1 IS NULL OR ce.status<>?1)" +
            "   AND (ce.casebookId IN (?2))")
    List<Object[]> findCasebookIdAndCaseRecordTime(CommonStatus statusNot, List<Long> casebookIds);
    List<CaseEntity> findByStatusNotAndCasebookIdIn(CommonStatus statusNot, List<Long> casebookIds, Sort sort);
    List<CaseEntity> findByStatusNotAndIdIn(CommonStatus statusNot, List<Long> caseIds);
}
