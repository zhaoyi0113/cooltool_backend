package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.UserReExaminationDateEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/7/3.
 */
public interface UserReExaminationDateRepository extends JpaRepository<UserReExaminationDateEntity, Long> {

    List<UserReExaminationDateEntity> findByUserIdAndGroupIdAndStatusIn(Long userId, Long groupId, List<CommonStatus> status, Sort sort);

    List<UserReExaminationDateEntity> findByUserIdAndStatusIn(Long userId, List<CommonStatus> status, Sort sort);
}
