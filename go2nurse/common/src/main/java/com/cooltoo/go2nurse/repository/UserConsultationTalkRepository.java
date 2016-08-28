package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.UserConsultationTalkEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/8/28.
 */
public interface UserConsultationTalkRepository extends JpaRepository<UserConsultationTalkEntity, Long>{

    List<UserConsultationTalkEntity> findByStatusNotAndConsultationIdIn(CommonStatus status, List<Long> consultationIds, Sort sort);
    List<UserConsultationTalkEntity> findByStatusNotAndIdIn(CommonStatus status, List<Long> talkIds);
}
