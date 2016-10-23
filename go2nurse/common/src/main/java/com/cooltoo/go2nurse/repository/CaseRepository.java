package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.go2nurse.constants.ConsultationTalkStatus;
import com.cooltoo.go2nurse.entities.CaseEntity;
import com.cooltoo.go2nurse.entities.UserConsultationTalkEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/23.
 */
public interface CaseRepository extends JpaRepository<CaseEntity, Long>{

    List<CaseEntity> findByStatusNotAndCasebookIdIn(CommonStatus status, List<Long> casebookIds, Sort sort);
    List<CaseEntity> findByStatusNotAndIdIn(CommonStatus status, List<Long> caseIds);
}
