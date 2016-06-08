package com.cooltoo.go2nurse.repository;

import com.cooltoo.go2nurse.entities.CourseCategoryRelationEntity;
import org.apache.commons.logging.Log;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hp on 2016/6/8.
 */
public interface CourseCategoryRelationRepository  extends JpaRepository<CourseCategoryRelationEntity, Long> {
}
