package com.cooltoo.go2nurse.repository;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.entities.AdvertisementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hp on 2016/9/6.
 */
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Long> {
    List<AdvertisementEntity> findByStatus(CommonStatus status, Sort sort);
    Page<AdvertisementEntity> findByStatus(CommonStatus status, Pageable page);
    long countByStatus(CommonStatus status);
}
