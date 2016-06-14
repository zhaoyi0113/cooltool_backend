package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.UserHospitalizedRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/6/14.
 */
public interface UserHospitalizedRelationRepository extends JpaRepository<UserHospitalizedRelationEntity, Long> {
}
