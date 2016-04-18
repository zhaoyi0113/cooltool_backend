package com.cooltoo.backend.repository;

import com.cooltoo.backend.entities.CathartProfilePhotoEntity;
import com.cooltoo.constants.CommonStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/4/18.
 */
public interface CathartProfilePhotoRepository extends JpaRepository<CathartProfilePhotoEntity, Long> {
    List<CathartProfilePhotoEntity> findByEnable(CommonStatus enable, Sort sort);
}
